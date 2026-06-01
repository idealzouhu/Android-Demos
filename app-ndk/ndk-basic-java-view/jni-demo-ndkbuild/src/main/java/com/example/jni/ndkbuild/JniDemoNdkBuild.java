package com.example.jni.ndkbuild;

/**
 * 演示 ndk-build 构建的 {@code libndkdemo.so}：
 * <ul>
 *   <li>{@link #getMessageStatic()} — 静态 JNI 注册</li>
 *   <li>{@link #getMessageDynamic()} — 在 {@code JNI_OnLoad} 中 {@code RegisterNatives} 动态注册</li>
 * </ul>
 */
public final class JniDemoNdkBuild {

    static {
        System.loadLibrary("ndkdemo");
    }

    private JniDemoNdkBuild() {
    }

    public static native String getMessageStatic();

    public static native String getMessageDynamic();
}
