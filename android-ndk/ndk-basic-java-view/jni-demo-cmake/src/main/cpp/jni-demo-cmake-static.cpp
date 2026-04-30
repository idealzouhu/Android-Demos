#include <jni.h>

// 静态注册：符号名遵循 Java_包路径_类名_方法名，loadLibrary 后由虚拟机自动绑定。
extern "C" JNIEXPORT jstring JNICALL
Java_com_example_jni_cmake_JniDemoCMake_getMessageStatic(JNIEnv* env, jclass /* clazz */) {
    const char* msg = "CMake：静态注册（Java_... 命名）";
    return env->NewStringUTF(msg);
}

// 故意崩溃：对空指针写入，产生 SIGSEGV，便于对照 tombstone 与 ndk-stack 解析 Native 栈。
extern "C" JNIEXPORT void JNICALL
Java_com_example_jni_cmake_JniDemoCMake_triggerNativeCrashForStackAnalysis(JNIEnv* /* env */,
                                                                           jclass /* clazz */) {
    volatile int* const null_ptr = nullptr;
    *null_ptr = 1;
}
