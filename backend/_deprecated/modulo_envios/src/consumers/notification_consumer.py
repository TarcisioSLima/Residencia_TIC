import time
import json
import asyncio
from typing import Any, Dict, List
from redis import Redis
from src.utils.redis_client import redis_client
from src.services.email_service import EmailService
from src.services.whatsapp_service import WhatsAppService
from src.services.external_integration_service import ExternalIntegrationService
from src.config import NOTIFICATION_QUEUE
from src.utils.technical_log_store import append_technical_log


class NotificationConsumer:
    def __init__(self):
        self.queue_name = NOTIFICATION_QUEUE
        self.redis: Redis = redis_client
        self.email_service = EmailService()
        self.whatsapp_service = WhatsAppService()
        self.external_integration_service = ExternalIntegrationService()
        self.status_list: List[Dict[str, Any]] = []
        # Mapeamento de idCanal -> { nomeStatus.lower() -> id }
        self.status_map: Dict[str, Dict[str, str]] = {}
        # caso futuramente adicione SMS ou outros canais, criar services correspondentes

    def _log(self, level: str, message: str, details: Dict[str, Any] | None = None) -> None:
        formatted = f"[NotificationConsumer] {message}"
        print(formatted)
        append_technical_log(level=level, source="NotificationConsumer", message=message, details=details)

    async def load_all_status(self) -> bool:
        """Busca todos os status disponíveis da API externa e armazena em cache."""
        self._log("INFO", "Carregando todos os status disponíveis...")
        try:
            fetched_status = await self.external_integration_service.get_all_status()
            self.status_list = fetched_status
            self.status_map = {}

            # Agrupar status por canal (idCanal) => { nomeStatus.lower(): id }
            for status in self.status_list:
                status_id = status.get("id")
                nome_status = status.get("nomeStatus")
                id_canal = status.get("idCanal")
                key = str(id_canal) if id_canal is not None else "unknown"
                if not status_id or not nome_status:
                    continue
                if key not in self.status_map:
                    self.status_map[key] = {}
                self.status_map[key][nome_status.lower()] = status_id

            self._log("INFO", f"{len(self.status_list)} status carregados com sucesso.")
            # Mostrar sumário por canal
            for canal_key, statuses in self.status_map.items():
                self._log("INFO", f"Canal {canal_key}: {list(statuses.keys())}")
            return True
        except Exception as e:
            self._log("ERROR", f"Erro ao carregar status: {e}")
            return False

    def get_status_id(self, idCanal: str, status_name: str = "Sucesso") -> str:
        """Obtém o ID de um status pelo nome dentro do canal indicado por `idCanal`.
        Padrão de `status_name` é 'Sucesso'. Se não encontrar, tenta fallback local ao canal,
        depois usa o primeiro status global disponível.
        """
        key = str(idCanal) if idCanal is not None else "unknown"
        canal_statuses = self.status_map.get(key, {})
        status_id = canal_statuses.get(status_name.lower()) if canal_statuses else None

        if status_id:
            return status_id

        # Fallback 1: usar qualquer status disponível no mesmo canal
        if canal_statuses:
            first = next(iter(canal_statuses.values()), None)
            self._log("WARN", f"Status '{status_name}' não encontrado para canal {key}. Usando fallback local: {first}")
            return first

        # Fallback 2: usar o primeiro status global carregado
        if self.status_list:
            fallback_global = self.status_list[0].get("id")
            self._log("WARN", f"Status '{status_name}' não encontrado. Usando fallback global: {fallback_global}")
            return fallback_global

        self._log("ERROR", f"Nenhum status disponível para obter ID do status '{status_name}'.")
        return None

    def start(self):
        """Inicia o consumer com carregamento inicial de status."""
        self._log("INFO", "Iniciando consumo da fila de envios...")
        self._log("INFO", f"Consumindo fila: {self.queue_name}")

        # Carregar status antes de começar
        loaded = asyncio.run(self.load_all_status())
        if not loaded:
            self._log("WARN", "Inicialização sem cache de status. O worker continuará ativo e tentará novamente sob demanda.")

        while True:
            _, message = self.redis.blpop(self.queue_name)
            payload = json.loads(message)
            asyncio.run(self.process_notification(payload))
            time.sleep(0.2)

    async def process_notification(self, payload: Dict[str, Any]):
        self._log("INFO", f"Processando notificação payload: {payload}.", payload)
        canal = payload.get("canal")
        usuarios = payload.get("usuarios", [])
        conteudo = payload.get("conteudo", "")
        alertas = payload.get("alertas", [])

        if not usuarios:
            self._log("WARN", f"Nenhum usuário para enviar no canal {canal.get('nomeCanal', 'desconhecido')}.")
            return

        if conteudo == "":
            self._log("WARN", f"Conteúdo vazio para o canal {canal.get('nomeCanal', 'desconhecido')}.")
            return

        nomeCanal = canal.get("nomeCanal")
        idCanal = canal.get("id")
        # Extrair IDs de alerta de forma robusta (suporta vários formatos)
        alert_ids: List[str] = []
        self._log("INFO", f"alertas recebidos: {alertas}")
        for a in alertas:
            if not isinstance(a, dict):
                continue
            id_val = None
            # novo formato: {'alerta': {'id': '...'}, ...}
            alerta_obj = a.get("alerta")
            if isinstance(alerta_obj, dict):
                id_val = alerta_obj.get("id") or alerta_obj.get("idAviso")

            # formatos alternativos: {'id': '...'} ou {'idAviso': '...'}
            if not id_val:
                id_val = a.get("id") or a.get("idAviso") or a.get("alertaId")

            if id_val:
                alert_ids.append(str(id_val))

        # Remover duplicados mantendo ordem
        seen = set()
        unique_alert_ids = []
        for x in alert_ids:
            if x not in seen:
                seen.add(x)
                unique_alert_ids.append(x)
        alert_ids = unique_alert_ids
        self._log("INFO", f"Alert IDs encontrados: {alert_ids}")

        try:
            if nomeCanal.lower() == "email":
                self.email_service.send_bulk(usuarios, conteudo)
            elif nomeCanal.lower() == "whatsapp":
                self.whatsapp_service.send_bulk(usuarios, conteudo)
            else:
                self._log("ERROR", f"Canal {nomeCanal} não suportado.")
                return

            # Após envio bem-sucedido, registrar envios na API externa para cada alerta
            if not alert_ids:
                self._log("WARN", "Nenhum alerta encontrado para registro de envios.")
            else:
                for aid in alert_ids:
                    await self.register_envios(
                        idCanal=idCanal,
                        idAviso=aid,
                        usuarios=usuarios,
                        status_name="Sucesso"
                    )
        except Exception as e:
            self._log("ERROR", f"Erro ao processar notificação: {e}")
            # Registrar com status de erro (se existir)
            try:
                if not alert_ids:
                    self._log("WARN", "Nenhum alerta encontrado para registro de falha de envio.")
                else:
                    for aid in alert_ids:
                        await self.register_envios(
                            idCanal=idCanal,
                            idAviso=aid,
                            usuarios=usuarios,
                            status_name="Falha"
                        )
            except Exception as register_error:
                self._log("ERROR", f"Erro ao registrar falha de envio: {register_error}")

    async def ensure_status_loaded(self) -> bool:
        if self.status_list:
            return True

        self._log("WARN", "Cache de status vazio. Tentando recarregar antes de registrar envios...")
        return await self.load_all_status()

    async def register_envios(
        self,
        idCanal: str,
        idAviso: str,
        usuarios: List[Dict[str, Any]],
        status_name: str = "Sucesso"
    ):
        """Registra os envios realizados na API externa."""
        if not await self.ensure_status_loaded():
            self._log("ERROR", "Não foi possível carregar status da API. Registro de envios adiado.")
            return

        idStatus = self.get_status_id(idCanal=idCanal, status_name=status_name)
        if not idStatus:
            self._log("ERROR", f"Não foi possível obter ID do status '{status_name}'. Abortando registro.")
            return

        self._log("INFO", f"Registrando {len(usuarios)} envio(s) na API externa...")
        for usuario in usuarios:
            idUsuarioDestinatario = usuario.get("id")
            if not idUsuarioDestinatario:
                self._log("WARN", f"Usuário sem ID: {usuario}. Pulando...")
                continue

            try:
                resultado = await self.external_integration_service.create_envio(
                    idCanal=idCanal,
                    idAviso=idAviso,
                    idUsuarioDestinatario=idUsuarioDestinatario,
                    idStatus=idStatus
                )
                self._log("INFO", f"Envio registrado com sucesso para usuário {idUsuarioDestinatario}. ID: {resultado.get('id')}")
            except Exception as e:
                self._log("ERROR", f"Erro ao registrar envio para usuário {idUsuarioDestinatario}: {e}")


if __name__ == "__main__":
    consumer = NotificationConsumer()
    consumer.start()
