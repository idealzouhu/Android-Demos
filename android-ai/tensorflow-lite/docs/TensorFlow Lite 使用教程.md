## 一、什么是 TensorFlow  和 TensorFlow Lite

**TensorFlow** 是**全功能的深度学习框架**，主要用于模型训练和服务器端部署。

[TensorFlow Lite](https://tensorflow.google.cn/lite/guide?hl=zh-cn) 是 TensorFlow 的**轻量级移动端版本**，专为手机和嵌入式设备的实时推理优化。主要特性

- 通过解决以下 5 项约束条件，针对设备端机器学习进行了优化：延时（数据无需往返服务器）、隐私（没有任何个人数据离开设备）、连接性（无需连接互联网）、大小（缩减了模型和二进制文件的大小）和功耗（高效推断，且无需网络连接）。
- 支持多种平台，涵盖 [Android](https://tensorflow.google.cn/lite/guide/android?hl=zh-cn) 和 [iOS](https://tensorflow.google.cn/lite/guide/ios?hl=zh-cn) 设备、[嵌入式 Linux](https://tensorflow.google.cn/lite/guide/python?hl=zh-cn) 和[微控制器](https://tensorflow.google.cn/lite/microcontrollers?hl=zh-cn)。
- 支持多种语言，包括 Java、Swift、Objective-C、C++ 和 Python。
- 高性能，支持[硬件加速](https://tensorflow.google.cn/lite/performance/delegates?hl=zh-cn)和[模型优化](https://tensorflow.google.cn/lite/performance/model_optimization?hl=zh-cn)。
- 提供多种平台上的常见机器学习任务的端到端[示例](https://tensorflow.google.cn/lite/examples?hl=zh-cn)，例如图像分类、对象检测、姿势估计、问题回答、文本分类等。





## 二、开发工作流程

### 2.1 创建 Tensorflow Lite 模型

TensorFlow Lite 模型以名为 [FlatBuffer](https://google.github.io/flatbuffers/) 的专用高效可移植格式（由“.tflite”文件扩展名标识）表示。与 TensorFlow 的协议缓冲区模型格式相比，这种格式具有多种优势，例如可缩减大小（代码占用的空间较小）以及提高推断速度（可直接访问数据，无需执行额外的解析/解压缩步骤），这样一来，TensorFlow Lite 即可在计算和内存资源有限的设备上高效地运行。

TensorFlow Lite 模型可以选择包含元数据，并在元数据中添加人类可读的模型说明和机器可读的数据，以便在设备推断过程中自动生成处理前和处理后流水线。如需了解详情，请参阅[添加元数据](https://tensorflow.google.cn/lite/convert/metadata?hl=zh-cn)。

您可以通过以下方式生成 TensorFlow Lite 模型：

- **使用现有的 TensorFlow Lite 模型**：若要选择现有模型，请参阅 [TensorFlow Lite 示例](https://tensorflow.google.cn/lite/examples?hl=zh-cn)。模型可能包含元数据，也可能不含元数据。
- **创建 TensorFlow Lite 模型**：使用 [TensorFlow Lite Model Maker](https://tensorflow.google.cn/lite/guide/model_maker?hl=zh-cn)，利用您自己的自定义数据集创建模型。默认情况下，所有模型都包含元数据。
- **将 TensorFlow 模型转换为 TensorFlow Lite 模型**：使用 [TensorFlow Lite Converter](https://tensorflow.google.cn/lite/convert/index?hl=zh-cn) 将 TensorFlow 模型转换为 TensorFlow Lite 模型。在转换过程中，您可以应用[量化](https://tensorflow.google.cn/lite/performance/post_training_quantization?hl=zh-cn)等[优化](https://tensorflow.google.cn/lite/performance/model_optimization?hl=zh-cn)措施，以缩减模型大小和缩短延时，并最大限度降低或完全避免准确率损失。默认情况下，所有模型都不含元数据。



### 2.2 运行推断

推断是指在设备上执行 TensorFlow Lite 模型，以便根据输入数据进行预测的过程。您可以通过以下方式运行推断，具体取决于模型类型：

- **不含元数据的模型**：使用 [TensorFlow Lite Interpreter](https://tensorflow.google.cn/lite/guide/inference?hl=zh-cn) API。在多种平台和语言（如 Java、Swift、C++、Objective-C 和 Python）中均受支持。
- **包含元数据的模型**：您可以使用 [TensorFlow Lite Task 库](https://tensorflow.google.cn/lite/inference_with_metadata/task_library/overview?hl=zh-cn)以利用开箱即用的 API，也可以使用 [TensorFlow Lite Support 库](https://tensorflow.google.cn/lite/inference_with_metadata/lite_support?hl=zh-cn)构建自定义的推断流水线。在 Android 设备上，用户可以使用 [Android Studio ML Model Binding](https://tensorflow.google.cn/lite/inference_with_metadata/codegen?hl=zh-cn#mlbinding) 或 [TensorFlow Lite Code Generator](https://tensorflow.google.cn/lite/inference_with_metadata/codegen?hl=zh-cn#codegen) 自动生成代码封装容器。仅在 Java (Android) 中受支持，我们正在努力使其在 Swift (iOS) 和 C++ 中受支持。

在 Android 和 iOS 设备上，您可以使用硬件加速来提升性能。在任何一个平台上，您都可以使用 [GPU 代理](https://tensorflow.google.cn/lite/performance/gpu?hl=zh-cn)：在 Android 上，您可以使用 [NNAPI 代理](https://tensorflow.google.cn/lite/performance/nnapi?hl=zh-cn)（适用于新款设备）或 [Hexagon 代理](https://tensorflow.google.cn/lite/performance/hexagon_delegate?hl=zh-cn)（适用于旧款设备）；在 iOS 上，您可以使用 [Core ML 代理](https://tensorflow.google.cn/lite/performance/coreml_delegate?hl=zh-cn)。如需添加对新的硬件加速器的支持，您可以[定义自己的代理](https://tensorflow.google.cn/lite/performance/implementing_delegate?hl=zh-cn)。









### 参考资料

[为 Android 构建 TensorFlow Lite 库](https://tensorflow.google.cn/lite/android/lite_build?hl=zh-cn)