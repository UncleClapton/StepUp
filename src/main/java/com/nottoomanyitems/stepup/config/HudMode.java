package com.nottoomanyitems.stepup.config;

public enum HudMode {
    ALWAYS,
    CHANGE,
    ON_ONLY,
    OFF_ONLY,
    NEVER;

    public static boolean isDynamic(HudMode hudMode) {
        return hudMode == HudMode.CHANGE
                || hudMode == HudMode.ON_ONLY
                || hudMode == HudMode.OFF_ONLY;
    }
}
