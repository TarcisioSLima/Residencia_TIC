package org.ufg.dto;

import java.util.List;

public class ForceResendRequestDTO {
    private List<String> userIds;
    private boolean bypassPreferences = false;

    public List<String> getUserIds() {
        return userIds;
    }

    public void setUserIds(List<String> userIds) {
        this.userIds = userIds;
    }

    public boolean isBypassPreferences() {
        return bypassPreferences;
    }

    public void setBypassPreferences(boolean bypassPreferences) {
        this.bypassPreferences = bypassPreferences;
    }
}
