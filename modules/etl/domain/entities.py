from __future__ import annotations

from dataclasses import dataclass
from typing import Literal, Optional


AlertType = Literal["temperatura alta", "temperatura baixa", "umidade baixa", "vento", "chuva", "sensação térmica"]


@dataclass(slots=True)
class Alert:
    type: AlertType
    value: float
    unit: str
    threshold: Optional[float]
    difference: Optional[float]
    seconds: float
    date: str
    polygon_name: str


@dataclass(slots=True)
class AvisoRecord:
    polygon_name: str
    alert_type: AlertType
    value: float
    unit: str
    threshold: Optional[float]
    difference: Optional[float]
    seconds: Optional[float]
    date: str
