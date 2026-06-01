#include <jni.h>

#define JNI_CLASS_NAME "com/example/jni/ndkbuild/JniDemoNdkBuild"

/**
 * 动态注册的 native 方法实现，返回动态注册相关的消息字符串
 *
 * @param env   JNI 环境指针，用于访问 JNI 函数和创建 Java 对象
 * @param clazz Java 类的引用（静态方法传入的是 jclass）
 * @return      返回 Java String 对象
 */
static jstring JNICALL native_getMessageDynamic(JNIEnv* env, jclass /* clazz */) {
    const char* msg = "ndk-build：动态注册（JNI_OnLoad + RegisterNatives）";
    return env->NewStringUTF(msg);
}

/**
 * 定义 native 方法与 Java 方法的映射关系数组
 * 格式：{Java方法名, 方法签名, C/C++函数指针}
 */
static const JNINativeMethod kMethods[] = {
        {"getMessageDynamic", "()Ljava/lang/String;",
         reinterpret_cast<void*>(native_getMessageDynamic)},
};

/**
 * JNI 库加载时的入口函数，用于动态注册 native 方法
 *
 * 当 System.loadLibrary() 加载库时自动调用此函数，完成 native 方法与 Java 方法的绑定
 *
 * @param vm       Java VM 指针，用于获取 JNI 环境
 * @param reserved 保留参数，当前未使用
 * @return         成功返回 JNI_VERSION_1_6，失败返回 JNI_ERR
 */
extern "C" JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM* vm, void* /* reserved */) {
    JNIEnv* env = nullptr;

    /**
   * 获取 JNI 环境指针，指定使用 JNI 1.6 版本
   */
    if (vm->GetEnv(reinterpret_cast<void**>(&env), JNI_VERSION_1_6) != JNI_OK) {
        return JNI_ERR;
    }

    /**
     * 查找目标 Java 类
     */
    jclass clazz = env->FindClass(JNI_CLASS_NAME);
    if (clazz == nullptr) {
        return JNI_ERR;
    }

    /**
    * 计算映射数组的方法数量并批量注册 native 方法
    */
    const jint n = static_cast<jint>(sizeof(kMethods) / sizeof(kMethods[0]));
    if (env->RegisterNatives(clazz, kMethods, n) != JNI_OK) {
        env->DeleteLocalRef(clazz);
        return JNI_ERR;
    }

    /**
    * 删除局部引用，避免内存泄漏
    */
    env->DeleteLocalRef(clazz);
    return JNI_VERSION_1_6;
}
