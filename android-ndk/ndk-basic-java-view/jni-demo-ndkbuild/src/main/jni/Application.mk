APP_STL := c++_static
APP_CPPFLAGS := -std=c++17

# 原生库最低 Android API（与模块 minSdk 对齐）
APP_PLATFORM := android-24

# 参与编译的 CPU ABI
APP_ABI := arm64-v8a armeabi-v7a
