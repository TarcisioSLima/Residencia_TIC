from typing import Optional

from fastapi import APIRouter, Query

from ..utils.technical_log_store import read_technical_logs, clear_technical_logs


router = APIRouter(prefix="/technical-logs", tags=["technical-logs"])


@router.get("")
async def list_technical_logs(
    limit: int = Query(default=200, ge=1, le=1000),
    level: Optional[str] = None,
    source: Optional[str] = None,
    search: Optional[str] = None
):
    items = read_technical_logs(limit=limit, level=level, source=source, search=search)
    return {
        "content": items,
        "totalElements": len(items)
    }


@router.delete("")
async def delete_technical_logs():
    clear_technical_logs()
    return {
        "status": "ok",
        "message": "Logs técnicos removidos."
    }
