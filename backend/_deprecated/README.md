# Modulos Depreciados

Estes modulos foram substituidos pela refatoracao em `modules/`.

| Modulo antigo | Substituido por | Motivo |
|---|---|---|
| `modulo_alertas` | `modules/etl/` | Migrado de cron/script para Celery async com retry e logging estruturado |
| `modulo_envios` | `modules/notifications/` | Migrado de FastAPI HTTP para consumer Redis com filtragem por preferencias |

O codigo aqui e mantido apenas para referencia historica. Nao deve ser usado em producao.

Para a stack atual, use `modules/docker-compose.yml`.
