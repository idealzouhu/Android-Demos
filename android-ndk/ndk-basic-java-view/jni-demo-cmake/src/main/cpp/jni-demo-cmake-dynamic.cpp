#include <jni.h>

#define JNI_CLASS_NAME "com/example/jni/cmake/JniDemoCMake"

static jstring JNICALL native_getMessageDynamic(JNIEnv* env, jclass /* clazz */) {
    const char* msg = "CMake：动态注册（JNI_OnLoad + RegisterNatives）";
    return env->NewStringUTF(msg);
}

static const JNINativeMethod kMethods[] = {
        {"getMessageDynamic", "()Ljava/lang/String;",
         reinterpret_cast<void*>(native_getMessageDynamic)},
};

extern "C" JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM* vm, void* /* reserved */) {
    JNIEnv* env = nullptr;
    if (vm->GetEnv(reinterpret_cast<void**>(&env), JNI_VERSION_1_6) != JNI_OK) {
        return JNI_ERR;
    }
    jclass clazz = env->FindClass(JNI_CLASS_NAME);
    if (clazz == nullptr) {
        return JNI_ERR;
    }
    const jint n = static_cast<jint>(sizeof(kMethods) / sizeof(kMethods[0]));
    if (env->RegisterNatives(clazz, kMethods, n) != JNI_OK) {
        env->DeleteLocalRef(clazz);
        return JNI_ERR;
    }
    env->DeleteLocalRef(clazz);
    return JNI_VERSION_1_6;
}
