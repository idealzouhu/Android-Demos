## 专题总览

研究 [sherpa-onnx](https://k2-fsa.github.io/sherpa/onnx/android/index.html) 部署和推理用于不同语音处理场景下AI模型的使用方法



### 基础知识

- [sherpa-onnx 工具概述.md](docs\sherpa-onnx 工具概述.md) 



### 实现项目

本专题主要是整理 [k2-fsa/sherpa-onnx](https://github.com/k2-fsa/sherpa-onnx/tree/master/android) 仓库里面 Android 案例， kotlin 语音版本的 API [sherpa-onnx/sherpa-onnx/kotlin-api](https://github.com/k2-fsa/sherpa-onnx/tree/master/sherpa-onnx/kotlin-api) 。

| Folder                                                       | Pre-built APK                                                | Description                                                  |
| ------------------------------------------------------------ | ------------------------------------------------------------ | ------------------------------------------------------------ |
| [SherpaOnnx](https://github.com/k2-fsa/sherpa-onnx/blob/master/android/SherpaOnnx) | [URL](https://k2-fsa.github.io/sherpa/onnx/android/apk.html) | It uses a streaming ASR model.                               |
| [SherpaOnnx2Pass](https://github.com/k2-fsa/sherpa-onnx/blob/master/android/SherpaOnnx2Pass) | [URL](https://k2-fsa.github.io/sherpa/onnx/android/apk-2pass.html) | It uses a streaming ASR model for the first pass and use a non-streaming ASR model for the second pass |
| [SherpaOnnxKws](https://github.com/k2-fsa/sherpa-onnx/blob/master/android/SherpaOnnxKws) | [URL](https://k2-fsa.github.io/sherpa/onnx/kws/apk.html)     | It demonstrates how to use keyword spotting                  |
| [SherpaOnnxSpeakerIdentification](https://github.com/k2-fsa/sherpa-onnx/blob/master/android/SherpaOnnxSpeakerIdentification) | [URL](https://k2-fsa.github.io/sherpa/onnx/speaker-identification/apk.html) | It demonstrates how to use speaker identification            |

