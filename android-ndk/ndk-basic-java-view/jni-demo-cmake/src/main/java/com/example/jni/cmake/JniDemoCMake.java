package com.example.jni.cmake;

/**
 * 演示 CMake 构建的 {@code libcmakedemo.so}：
 * <ul>
 *   <li>{@link #getMessageStatic()} — 静态 JNI 注册</li>
 *   <li>{@link #getMessageDynamic()} — 在 {@code JNI_OnLoad} 中 {@code RegisterNatives} 动态注册</li>
 *   <li>{@link #triggerNativeCrashForStackAnalysis()} — 故意 Native 崩溃（SIGSEGV），仅用于崩溃日志 / {@code ndk-stack} 练习</li>
 * </ul>
 */
public final class JniDemoCMake {

    static {
        System.loadLibrary("cmakedemo");
    }

    private JniDemoCMake() {
    }

    public static native String getMessageStatic();

    public static native String getMessageDynamic();

    /** 触发故意 Native 崩溃；不要在正常流程中调用。 */
    public static native void triggerNativeCrashForStackAnalysis();
}
