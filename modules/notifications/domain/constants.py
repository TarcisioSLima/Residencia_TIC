"""Constantes de domínio para o módulo de notificações."""

REDIS_NOTIFICATIONS_QUEUE = "etl:notifications:ready"
REDIS_DEAD_LETTER_QUEUE = "etl:notifications:dead-letter"

# Tarefas do pipeline (coluna `application_logs.task`)
TASK_NAME_NOTIFICATIONS_CONSUMER = "NOTIFICATIONS:CONSUMER"
TASK_NAME_NOTIFICATIONS_DISPATCH = "NOTIFICATIONS:DISPATCH"

# Status de execução (coluna `application_logs.status`)
STATUS_STARTED = "STARTED"
STATUS_IN_PROGRESS = "IN_PROGRESS"
STATUS_SUCCESS = "SUCCESS"
STATUS_ERROR = "ERROR"

STATUS_SUCESSO = "Sucesso"
STATUS_FALHA = "Falha"

CANAL_EMAIL = "email"
CANAL_WHATSAPP = "whatsapp"
