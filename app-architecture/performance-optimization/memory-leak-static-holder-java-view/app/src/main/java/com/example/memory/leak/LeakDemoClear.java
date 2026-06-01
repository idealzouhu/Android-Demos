package com.example.memory.leak;

/** 演示结束后清空本包内各类「坏引用」，避免影响后续操作。 */
final class LeakDemoClear {

    private LeakDemoClear() {}

    static void clearAll() {
        LeakStaticActivityRef.clear();
        LeakSingletonContext.get().clear();
        LeakHandlerDelayed.clear();
        LeakBackgroundThread.clear();
        LeakStaticView.clear();
    }
}
