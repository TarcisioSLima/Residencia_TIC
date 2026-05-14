.PHONY: up down stop ps logs logs-etl logs-notifications run-once seed clean rebuild prod-pull prod-up

## Sobe toda a stack: db + redis + ETL worker + Notifications consumer
up:
	docker compose up --build -d

## Rebuild forçado sem cache (útil após mudanças em Dockerfile ou dependências)
rebuild:
	docker compose build --no-cache && docker compose up -d

## Derruba os containers (mantém volumes)
down:
	docker compose down

## Para os containers sem remover
stop:
	docker compose stop

## Status dos containers
ps:
	docker compose ps

## Logs de todos os serviços
logs:
	docker compose logs -f

## Logs do worker ETL
logs-etl:
	docker compose logs -f celery_worker

## Logs do consumer de notificações
logs-notifications:
	docker compose logs -f notifications

## Dispara o pipeline ETL manualmente (sem aguardar o schedule das 6h)
run-once:
	docker compose exec celery_worker uv run python run_once.py

## Aplica seed de dados de teste (psql no container db; requer stack no ar)
seed:
	docker compose exec -T db psql -U cempa -d sigedam -f /docker-entrypoint-initdb.d/seed_e2e.sql

## Remove containers e volumes (reset completo)
clean:
	docker compose down -v

## Produção: baixa imagens do GHCR
prod-pull:
	docker compose -f docker-compose.prod.yml pull

## Produção: sobe containers a partir das imagens GHCR (sem build local)
prod-up:
	docker compose -f docker-compose.prod.yml up -d

## Produção: para e remove containers (mantém volumes)
prod-down:
	docker compose -f docker-compose.prod.yml down

## Produção: logs de todos os serviços
prod-logs:
	docker compose -f docker-compose.prod.yml logs -f
