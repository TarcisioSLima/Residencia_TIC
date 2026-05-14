from __future__ import annotations

import math
from typing import Any, Dict, Optional, Tuple

from domain.entities import Alert
from domain.value_objects import Kelvin, magnus_rh
from infra.logger import JsonLogger


def _celsius_to_fahrenheit(t_c: float) -> float:
    return (9 / 5) * t_c + 32


def _fahrenheit_to_celsius(t_f: float) -> float:
    return (5 / 9) * (t_f - 32)


def _heat_index_fahrenheit(t_f: float, rh: float) -> float:
    """Equação completa de Rothfusz (1990) em Fahrenheit."""
    hi = (
        -42.379
        + 2.04901523 * t_f
        + 10.14333127 * rh
        - 0.22475541 * t_f * rh
        - 0.00683783 * t_f ** 2
        - 0.05481717 * rh ** 2
        + 0.00122874 * t_f ** 2 * rh
        + 0.00085282 * t_f * rh ** 2
        - 0.00000199 * t_f ** 2 * rh ** 2
    )

    # Ajuste ar muito seco: RH < 13% e 80°F ≤ T ≤ 112°F
    if rh < 13 and 80 <= t_f <= 112:
        adjustment = ((13 - rh) / 4) * math.sqrt((17 - abs(t_f - 95)) / 17)
        hi -= adjustment

    # Ajuste ar muito úmido: RH > 85% e 80°F ≤ T ≤ 87°F
    elif rh > 85 and 80 <= t_f <= 87:
        adjustment = ((rh - 85) / 10) * ((87 - t_f) / 5)
        hi += adjustment

    return hi


def compute_heat_index_celsius(t_c: float, rh: float) -> float:
    """Calcula o Heat Index (°C) a partir de temperatura (°C) e umidade relativa (%)."""
    t_f = _celsius_to_fahrenheit(t_c)

    # Fórmula simplificada de Steadman
    hi_f_simple = 0.5 * (t_f + 61.0 + ((t_f - 68.0) * 1.2) + (rh * 0.094))

    # Média entre HI simplificado e temperatura — critério de uso da equação completa
    if (hi_f_simple + t_f) / 2 < 80:
        return _fahrenheit_to_celsius(hi_f_simple)

    return _fahrenheit_to_celsius(_heat_index_fahrenheit(t_f, rh))


class HeatIndexAnalyzer:
    def __init__(self, logger: JsonLogger, min_threshold: float = 32.0):
        self._logger = logger
        self.min_threshold = float(min_threshold)

    def analyze(
        self, polygon_data: Dict[float, Dict[str, Any]], polygon_name: str
    ) -> Dict[str, Alert]:
        self._logger.debug(
            "Starting heat index analysis",
            polygon=polygon_name,
            threshold=self.min_threshold,
        )

        max_hi: float = float("-inf")
        max_payload: Optional[Tuple[float, Dict[str, Any], float]] = None

        records_processed = 0
        records_valid = 0

        for seconds, values in polygon_data.items():
            records_processed += 1
            t_ave_k = values.get("Tave")
            td_ave_k = values.get("TDave")
            if t_ave_k is None or td_ave_k is None:
                continue
            if any(v < 200 or v > 350 for v in (t_ave_k, td_ave_k)):
                continue

            records_valid += 1
            t_c = Kelvin(float(t_ave_k)).to_celsius().value
            td_c = Kelvin(float(td_ave_k)).to_celsius().value
            rh = magnus_rh(t_c, td_c)
            hi_c = compute_heat_index_celsius(t_c, rh)

            if hi_c > max_hi:
                max_hi = hi_c
                max_payload = (seconds, values, hi_c)

        self._logger.debug(
            "Heat index analysis done",
            polygon=polygon_name,
            records_processed=records_processed,
            records_valid=records_valid,
            max_hi_c=round(max_hi, 2) if max_payload else None,
        )

        alerts: Dict[str, Alert] = {}
        if max_payload and max_hi >= self.min_threshold:
            sec, vals, hi_val = max_payload
            alerts["sensação térmica"] = Alert(
                type="sensação térmica",
                value=round(hi_val, 2),
                unit="°C",
                threshold=self.min_threshold,
                difference=round(hi_val - self.min_threshold, 2),
                seconds=float(sec),
                date=str(vals.get("date", "")),
                polygon_name=polygon_name,
            )

        return alerts
