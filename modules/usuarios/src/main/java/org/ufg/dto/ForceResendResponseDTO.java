package org.ufg.dto;

public class ForceResendResponseDTO {
    private int alertsCount;
    private int targetUsersCount;

    public ForceResendResponseDTO(int alertsCount, int targetUsersCount) {
        this.alertsCount = alertsCount;
        this.targetUsersCount = targetUsersCount;
    }

    public int getAlertsCount() {
        return alertsCount;
    }

    public int getTargetUsersCount() {
        return targetUsersCount;
    }
}
