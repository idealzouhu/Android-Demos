## 一、SO 文件

### 1.1 为什么需要库文件

SherpaOnnx 需要以下 JNI 库文件（`.so` 文件）来部署和推理模型：

- `libsherpa-onnx-jni.so` - SherpaOnnx JNI 接口库
- `libonnxruntime.so` - ONNX Runtime 库

这些文件需要放置在 Android 项目的 `src/main/jniLibs/` 目录下，按照不同的 CPU 架构组织：

```
jniLibs/
├── arm64-v8a/
│   ├── libsherpa-onnx-jni.so
│   └── libonnxruntime.so
├── armeabi-v7a/
│   ├── libsherpa-onnx-jni.so
│   └── libonnxruntime.so
├── x86/
│   ├── libsherpa-onnx-jni.so
│   └── libonnxruntime.so
└── x86_64/
    ├── libsherpa-onnx-jni.so
    └── libonnxruntime.so
```



### 1.2 如何获取库文件 

我们可以根据 [Build sherpa-onnx for Android — sherpa 1.3 documentation](https://k2-fsa.github.io/sherpa/onnx/android/build-sherpa-onnx.html#download-sherpa-onnx) 这一教程手动编译 so 文件，也可以在  [Releases · k2-fsa/sherpa-onnx](https://github.com/k2-fsa/sherpa-onnx/releases) 下载资源。

-  `sherpa-onnx-v1.12.23-android.tar.bz2` ：标准动态链接版本，包含 `libsherpa-onnx.so` 和其他相关 `.so` 文件。
- `sherpa-onnx-v1.12.23-android-static-link-onnxruntime.tar.bz2`：静态链接 ONNX Runtime 版本，`libsherpa-onnx.so` 且已经静态链接了 ONNX Runtime。
- `sherpa-onnx-v1.12.23-android-rknn.tar.bz2`：针对搭载瑞芯微（Rockchip）处理器的设备优化，使用硬件 NPU 加速推理以提升性能，适用于特定的嵌入式设备或开发板。





### 二、ONNX 模型文件

需要下载说话人识别模型文件（`.onnx` 格式），推荐使用：

- **模型名称**: `3dspeaker_speech_eres2net_base_sv_zh-cn_3dspeaker_16k.onnx`
- **下载地址**: https://github.com/k2-fsa/sherpa-onnx/releases/tag/speaker-recongition-models
- **放置位置**: 
  - 如果从 Assets 加载：放置在 `src/main/assets/` 目录下（不要放在子目录中）
  - 如果从文件系统加载：放置在设备可访问的文件路径

**注意**: 模型文件支持 16kHz 采样率的音频输入。



### 2.1 模型分类

模型结构

zipformer，zipformer2



语言



功能









## 参考资料

[Pre-built APKs — sherpa 1.3 documentation](https://k2-fsa.github.io/sherpa/onnx/android/prebuilt-apk.html)