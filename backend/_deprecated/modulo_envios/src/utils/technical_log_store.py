import json
import os
from datetime import datetime
from pathlib import Path
from typing import Any, Dict, List, Optional


LOG_DIR = Path("/app/runtime")
LOG_FILE = LOG_DIR / "technical-logs.jsonl"
MAX_LOG_LINES = 1000


def append_technical_log(level: str, source: str, message: str, details: Optional[Dict[str, Any]] = None) -> None:
    LOG_DIR.mkdir(parents=True, exist_ok=True)

    payload = {
        "timestamp": datetime.now().isoformat(),
        "level": level.upper(),
        "source": source,
        "message": message,
        "details": details or {}
    }

    with LOG_FILE.open("a", encoding="utf-8") as fp:
        fp.write(json.dumps(payload, ensure_ascii=False) + "\n")

    _trim_log_file()


def read_technical_logs(
    limit: int = 200,
    level: Optional[str] = None,
    source: Optional[str] = None,
    search: Optional[str] = None
) -> List[Dict[str, Any]]:
    if not LOG_FILE.exists():
        return []

    with LOG_FILE.open("r", encoding="utf-8") as fp:
        lines = fp.readlines()

    logs: List[Dict[str, Any]] = []
    normalized_level = level.upper() if level else None
    normalized_search = search.lower() if search else None

    for raw_line in reversed(lines):
        line = raw_line.strip()
        if not line:
            continue

        try:
            entry = json.loads(line)
        except json.JSONDecodeError:
            continue

        if normalized_level and entry.get("level") != normalized_level:
            continue

        if source and entry.get("source") != source:
            continue

        if normalized_search:
            haystack = json.dumps(entry, ensure_ascii=False).lower()
            if normalized_search not in haystack:
                continue

        logs.append(entry)
        if len(logs) >= limit:
            break

    return logs


def clear_technical_logs() -> None:
    if LOG_FILE.exists():
        LOG_FILE.unlink()


def _trim_log_file() -> None:
    if not LOG_FILE.exists():
        return

    with LOG_FILE.open("r", encoding="utf-8") as fp:
        lines = fp.readlines()

    if len(lines) <= MAX_LOG_LINES:
        return

    trimmed = lines[-MAX_LOG_LINES:]
    with LOG_FILE.open("w", encoding="utf-8") as fp:
        fp.writelines(trimmed)
