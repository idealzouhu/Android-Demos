package com.example.hce.pos.iccsim;

/**
 * 供主界面显示的轻量状态（服务与 Activity 共用）。
 */
public final class IccHceDebugState {

    public static volatile boolean scriptLoaded;
    public static volatile String scriptLoadError;
    /** 与 {@link IccScriptSession#getNextStepIndex()} 一致：下一次期望的步骤下标。 */
    public static volatile int sessionNextStepIndex;
    public static volatile int sessionTotalSteps;

    private IccHceDebugState() {
    }

    public static void updateSessionProgress(int nextStepIndex, int totalSteps) {
        sessionNextStepIndex = nextStepIndex;
        sessionTotalSteps = totalSteps;
    }

    public static void setScriptLoaded(boolean ok, String errorMessage) {
        scriptLoaded = ok;
        scriptLoadError = errorMessage;
    }
}
