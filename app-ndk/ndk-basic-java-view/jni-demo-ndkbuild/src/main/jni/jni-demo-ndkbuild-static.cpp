#include <jni.h>

/**
 * JNI 静态注册方法，用于从 Java 层获取静态消息字符串
 *
 * 该方法通过 Java_... 命名规则进行静态注册，对应 Java 层的 native 方法
 *
 * @param env   JNI 环境指针，用于访问 JNI 函数和创建 Java 对象
 * @param clazz Java 类的引用（静态方法传入的是 jclass）
 * @return      返回Java String 对象
 */
extern "C" JNIEXPORT jstring JNICALL
Java_com_example_jni_ndkbuild_JniDemoNdkBuild_getMessageStatic(JNIEnv* env, jclass /* clazz */) {
    const char* msg = "ndk-build：静态注册（Java_... 命名）";
    return env->NewStringUTF(msg);
}
