LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)
# 生成的 so 名：libndkdemo.so
LOCAL_MODULE := ndkdemo
LOCAL_SRC_FILES := jni-demo-ndkbuild-static.cpp \
    jni-demo-ndkbuild-dynamic.cpp
LOCAL_LDLIBS := -llog
include $(BUILD_SHARED_LIBRARY)
