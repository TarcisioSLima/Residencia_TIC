"""Orquestrador principal do pipeline de notificações."""

from __future__ import annotations

import uuid
from uuid import UUID

from domain.entities import ExecutionPayload
from domain.constants import (
    TASK_NAME_NOTIFICATIONS_CONSUMER,
    STATUS_ERROR,
    STATUS_IN_PROGRESS,
    STATUS_SUCCESS,
)
from infra.logger import JsonLogger
from infra.database import insert_application_log


def _log_safe(
    *,
    logger: JsonLogger,
    execution_id: UUID | None,
    message: str,
    status: str | None = None,
    extra: dict | None = None,
    task: str = TASK_NAME_NOTIFICATIONS_CONSUMER,
) -> None:
    """Insere um registro em `application_logs` sem derrubar o pipeline."""
    if execution_id is None:
        return

    try:
        insert_application_log(
            task=task,
            execution_id=execution_id,
            message=message,
            status=status,
            extra=extra,
        )
    except Exception as e:
        logger.exception(
            "Falha ao inserir application_log - verifique Postgres e tabela application_logs",
            error=str(e),
            message=message,
        )


class NotificationConsumer:
    """Consume payload da fila, resolve, filtra e despacha."""

    def __init__(
        self,
        logger: JsonLogger,
        resolver,
        filter_service,
        dispatcher,
    ):
        self._logger = logger
        self._resolver = resolver
        self._filter = filter_service
        self._dispatcher = dispatcher

    def process(self, payload: ExecutionPayload) -> None:
        """Pipeline: Resolve -> Filtra -> Despacha."""
        execution_id_uuid: UUID | None = None
        try:
            # payload.execution_id é uma string (vem do ETL). Validamos para evitar insert quebrando o pipeline.
            execution_id_uuid = UUID(str(payload.execution_id))
        except Exception:
            self._logger.warning(
                "execution_id inválido; ignorando application_logs",
                execution_id=payload.execution_id,
            )

        _log_safe(
            logger=self._logger,
            execution_id=execution_id_uuid,
            message="Notificações iniciadas",
            status=STATUS_IN_PROGRESS,
            extra={
                "avisos_count": payload.avisos_count,
                "alertas_no_payload": len(payload.alerts or []),
            },
        )

        if not payload.alerts:
            self._logger.info("Payload sem alertas", execution_id=payload.execution_id)
            _log_safe(
                logger=self._logger,
                execution_id=execution_id_uuid,
                message="Sem alertas no payload",
                status=STATUS_SUCCESS,
            )
            return

        try:
            users_alerts = self._resolver.resolve(payload.alerts)

            if payload.target_user_ids:
                target = set(payload.target_user_ids)
                users_alerts = {k: v for k, v in users_alerts.items() if k in target}
                self._logger.info(
                    "Reenvio restrito a usuários alvo",
                    target_count=len(target),
                    matched=len(users_alerts),
                )

            for uid, data in list(users_alerts.items()):
                before = len(data["alertas"])
                if not payload.bypass_preferences:
                    data["alertas"] = self._filter.apply(data["alertas"], data["usuario"])
                after = len(data["alertas"])
                self._logger.info(
                    "[DEBUG CONSUMER] filtro aplicado",
                    user_id=uid,
                    alertas_antes=before,
                    alertas_depois=after,
                    bypass_preferences=payload.bypass_preferences,
                )

            users_alerts = {k: v for k, v in users_alerts.items() if v["alertas"]}

            totals = {
                "usuarios_com_alertas": len(users_alerts),
                "envios_sucesso": 0,
                "envios_falha": 0,
                "canais_email": 0,
                "canais_whatsapp": 0,
            }

            for uid, data in users_alerts.items():
                try:
                    stats = self._dispatcher.dispatch(
                        data["usuario"],
                        data["alertas"],
                        execution_id=execution_id_uuid,
                    )
                    if stats:
                        for k, v in stats.items():
                            if k in totals and v is not None:
                                totals[k] += int(v)
                except Exception as e:
                    # Para erros por usuário, as linhas em application_logs entram via NOTIFICATIONS:DISPATCH
                    # (quando ocorrerem falhas tratadas no dispatcher). Aqui mantemos o stdout.
                    self._logger.exception(
                        "Erro ao despachar para usuário",
                        user_id=uid,
                        error=str(e),
                    )

            _log_safe(
                logger=self._logger,
                execution_id=execution_id_uuid,
                message="Processamento concluído",
                status=STATUS_SUCCESS,
                extra=totals,
            )

            self._logger.info(
                "Processamento concluído",
                execution_id=payload.execution_id,
                usuarios_notificados=len(users_alerts),
            )
        except Exception as e:
            self._logger.exception(
                "Erro no processamento de notificações",
                execution_id=payload.execution_id,
                error=str(e),
            )
            _log_safe(
                logger=self._logger,
                execution_id=execution_id_uuid,
                message="Erro no processamento",
                status=STATUS_ERROR,
                extra={"error": str(e)},
            )
