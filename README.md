# SIGEDAM - Sistema de Gestão de Alertas Meteorológicos #

## 👥 Equipe (Grupo 4) #

| Nome | GitHub | Contato |
| :--- | :--- | :--- |
| Maria Beatriz | [@Zbiatriz](https://github.com/Zbiatriz) | mara.beatriz99@gmail.com |
| Rubevaldo | [@RubevaldoJunior](https://github.com/RubevaldoJunior) | Rubevaldoj@gmail.com |
| Thayanne | [@Thayanne31](https://github.com/Thayanne31) | thayanne_araujo@discente.ufg.br |
| Tarcísio | [@TarcisioSLima](https://github.com/TarcisioSLima) | tarcisio.pesquisa.estudo@gmail.com |
| Natanael | [@Natan21s](https://github.com/Natan21s) | natanael.dutra@ufg.br |

---

## 🎯 Visão Geral da Solução

O **SIGEDAM** é uma solução crítica para o monitoramento e mitigação de riscos meteorológicos no estado de Goiás, integrando dados em tempo real do **CEMPA-CERRADO**.

* **Versão 1:** Focada no acesso e interpretação inicial de dados com envio de avisos simplificados via e-mail.
* **Versão 2 (Atual):** Refatoração sistêmica para garantir escalabilidade e confiança. Implementamos uma arquitetura distribuída baseada em **DDD (Domain-Driven Design)**, processamento assíncrono com **Celery/Redis** e foco total em **Observabilidade**, permitindo o rastreio auditável de cada alerta gerado e enviado.

---

## 📂 Organização do Repositório

O repositório está dividido em diretórios de acordo com as responsabilidades técnicas. Cada módulo possui seu próprio `README.md` com instruções específicas.

* **`backend/`**: O motor do sistema, contendo:
    * `etl/`: Módulo de Extração, Transformação e Análise (Núcleo de Inteligência).
    * `envios/`: Orquestrador de notificações multi-canal (E-mail e WhatsApp).
    * `usuarios/`: Gestão de perfis, polígonos de monitoramento e preferências.
* **`frontend/`**: Interface administrativa e **Application Timeline** para visualização técnica dos fluxos.
* **`documentacao/`**: Documentação técnica, manuais e planos de projeto.

---

## 🚀 Diferenciais da Refatoração (V2)

Diferente da estrutura anterior, a nova arquitetura foca em resolver os gargalos de suporte e a falta de visibilidade:

1.  **Eliminação da Opacidade Operacional:** Saímos de um sistema "caixa-preta" para uma gestão **100% auditável**.
2.  **Rastreabilidade Total:** Implementação de logs estruturados que permitem distinguir falhas técnicas de ausência de eventos meteorológicos.
3.  **Resiliência e Escala:** Arquitetura preparada para falhas com sistemas de retentativas automáticas e filas independentes por canal.
4.  **Menor Fricção:** Novo fluxo de onboarding via WhatsApp e interfaces administrativas para dar autonomia ao CEMPA.

**"Habilitamos a rastreabilidade total para converter a complexidade técnica em segurança operacional e confiança para os stakeholders."**

---

## Responsabilidades de Infraestrutura #
Como responsável pela infraestrutura, implementei os seguintes pilares:
* **Orquestração:** Docker Compose para gerenciamento de microserviços.
* **Segurança:** Hardening de servidor com Firewall UFW e políticas de acesso.
* **Backup:** Automação de dumps e retenção de 7 dias via Cron.
* **Monitoramento:** Sensores de integridade de disco e logs de execução.
* **CI/CD Local:** Versionamento de configurações via Git.

---

## Deploy

### Como funciona hoje

O CI/CD é feito via **GitHub Actions** (`.github/workflows/docker-build-push.yml`). A cada PR aberta ou atualizada contra `main`, o workflow constrói e publica automaticamente as imagens dos quatro módulos no registry:

| Módulo | Imagem |
|--------|--------|
| API de usuários | `ghcr.io/zbiatriz/s04-secti/usuarios:latest` |
| ETL | `ghcr.io/zbiatriz/s04-secti/etl:latest` |
| Notificações | `ghcr.io/zbiatriz/s04-secti/notifications:latest` |
| Frontend | `ghcr.io/zbiatriz/s04-secti/frontend:latest` |

> **Atenção:** as imagens estão publicadas em um registry pessoal (`ghcr.io`). O correto para um projeto de equipe seria usar o registry da organização. Isso deve ser migrado antes de qualquer entrega final.

### Atualizar o servidor via SSH

Após um merge em `main` (ou quando o CI terminar de publicar novas imagens), acesse o servidor e execute:

```bash
# 1. Entrar no diretório de deploy
cd ~/modules   # ajuste o caminho conforme o servidor

# 2. Baixar as novas imagens do registry
docker compose -f docker-compose.prod.yml pull

# 3. Recriar os containers com as imagens atualizadas
docker compose -f docker-compose.prod.yml up -d

# 4. Verificar se todos os containers subiram corretamente
docker compose -f docker-compose.prod.yml ps
```

Para acompanhar os logs de um serviço específico após a atualização:

```bash
docker compose -f docker-compose.prod.yml logs -f <serviço>
# Exemplos: etl | notifications | modulo_usuarios | frontend
```
