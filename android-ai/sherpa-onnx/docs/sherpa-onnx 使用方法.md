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







## 三、工程优化技术

工程优化技术是指在模型能力固定的前提下，通过规则、策略或外部数据来**弥补模型在特定场景下的短板**



### 3.1 hotwords

在 ASR（自动语音识别）系统中，[**Hotwords**（热词）](https://k2-fsa.github.io/sherpa/onnx/hotwords/index.html) 是一种**词汇增强技术**，它的核心作用是**在解码过程中临时提升特定词汇（罕见词/专有名词/个性化信息）的识别权重**，从而让系统更倾向于识别出这些词，而不是发音相似的其他词。

> 注意，只有 [Offline transducer models](https://k2-fsa.github.io/sherpa/onnx/pretrained_models/offline-transducer/index.html#sherpa-onnx-offline-transducer-models) 和  [Online transducer models](https://k2-fsa.github.io/sherpa/onnx/pretrained_models/online-transducer/index.html#onnx-online-transducer-models) 这两类模型支持这个 hotwords

hotwords 的核心实现原理是 **Aho-Corasick 算法**（简称 AC 算法）。这个是一种**多模式匹配算法**，专门用于在**一个长文本中同时查找多个关键词**。



ASR 模型通常使用**汉字**或**BPE**作为建模单元。因此，**ASR 的 hotwords 文件里直接写汉字或英文单词即可**，系统内部会自动帮你分词。

>  KWS模型（如Zipformer）通常采用**音素（Phoneme）**作为建模单元, keywords 文件里面通常都是利用 text2token工具 来转换好的。







## 四、问题

### KWS 所使用text2token工具和 ASR 的分词器区别

text2token和 ASR 的分词器虽然都涉及“分割”，但它们处理的对象、目的和输出的结果在本质上是完全不同的。

- text2token是 “**文字转声音**”。它回答的问题是：“这个词应该**怎么读**？”
- ASR 分词器是 **“句子分零件”**。它回答的问题是：“这个句子由哪些**有意义的语言单元**组成？”

| 工具/模块                    | 处理对象 (输入)                           | 建模单元 (输出)                                              | 本质                                                         |
| :--------------------------- | :---------------------------------------- | :----------------------------------------------------------- | :----------------------------------------------------------- |
| **KWS 的 text2token 工具**   | **汉字文本** (如“小爱同学”)               | **音素序列 / 拼音** (如 `x iǎo ài t óng xué`)                | **转音**。将文字的“形”转换为其对应的标准“音”，供声学模型在**声学特征层面**进行匹配。 |
| **ASR 的分词器 (Tokenizer)** | **自然语言文本** (如“speech recognition”) | **语言学单元** (如 BPE 子词 `[“spe”, “ech”, “ re”, “cog”, “nition”]` 或 汉字 `[“语”, “音”, “识”, “别”]`) | **分词**。将自然语言序列切分为模型训练时使用的基本语义单元，用于**语言理解**层面的概率计算。 |







## 参考资料

[Pre-built APKs — sherpa 1.3 documentation](https://k2-fsa.github.io/sherpa/onnx/android/prebuilt-apk.html)