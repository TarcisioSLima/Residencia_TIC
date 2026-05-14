from __future__ import annotations

from typing import Any, Dict, Optional, Tuple

from domain.entities import Alert
from domain.value_objects import Kelvin, RelativeHumidity, magnus_rh
from infra.logger import JsonLogger


class HumidityAnalyzer:
    def __init__(self, logger: JsonLogger, min_threshold: float = 60.0):
        self._logger = logger
        self.min_threshold = float(min_threshold)

    @staticmethod
    def _magnus(t_c: float, td_c: float) -> RelativeHumidity:
        return RelativeHumidity(magnus_rh(t_c, td_c))

    def analyze(
        self, polygon_data: Dict[float, Dict[str, Any]], polygon_name: str
    ) -> Dict[str, Alert]:
        self._logger.debug(
            "Starting humidity analysis",
            polygon=polygon_name,
            threshold=self.min_threshold,
        )

        min_rh = float("inf")
        min_payload: Optional[Tuple[float, Dict[str, Any], float]] = None

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
            t_ave_c = Kelvin(float(t_ave_k)).to_celsius().value
            td_ave_c = Kelvin(float(td_ave_k)).to_celsius().value
            rh = self._magnus(t_ave_c, td_ave_c).value

            if rh < min_rh:
                min_rh = rh
                min_payload = (seconds, values, rh)

        alerts: Dict[str, Alert] = {}
        if min_payload and min_rh < self.min_threshold:
            sec, vals, rh_val = min_payload
            alerts["umidade baixa"] = Alert(
                type="umidade baixa",
                value=rh_val,
                unit="%",
                threshold=self.min_threshold,
                difference=rh_val - self.min_threshold,
                seconds=float(sec),
                date=str(vals.get("date", "")),
                polygon_name=polygon_name,
            )

        return alerts
