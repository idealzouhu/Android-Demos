## 什么是 sherpa-onnx

**sherpa-onnx**项目是一个基于**Next-gen Kaldi**和**ONNX Runtime**的开源语音处理框架，专门设计用于**完全离线**的语音与音频处理场景。

**sherpa-onnx 项目本身不提供完整的模型训练功能，仅专注于模型推理和部署**。该项目主要提供预训练模型的推理接口、模型转换工具和部署方案，但训练模型需要依赖其他框架（如新一代Kaldi、ESPnet等）完成，训练好的模型再通过sherpa-onnx提供的工具转换为ONNX格式进行部署。





### **语音处理核心能力**

- **Speech-to-text (STT/ASR)**：语音转文本，支持流式和非流式识别
- **Text-to-speech (TTS)**：文本转语音合成
- **Speaker Diarization**：说话人分离（识别多说话人场景中"谁在什么时候说话"）
- **Speech Enhancement**：语音增强（降噪、去混响等）
- **Source Separation**：源分离（分离混合音频中的不同声源）
- **VAD (Voice Activity Detection)**：语音活动检测（检测语音片段起止点）







### 说话人识别 （Speaker Recognition）

说话人识别 （Speaker Recognition）包含了识别，验证，分离等好几个小方向，根据不同的任务场景有不同的关注点，进而衍生出了对应的细节处理方法。

- 说话人验证（speaker verification）
- 说话人辨认（speaker identification）
- 说话人分离（ diarization）
- 鲁棒的说话人识别系统构建（robust speaker recognition）





[基于深度学习的声纹识别概述（Speaker Recognition Based on Deep Learning: An Overview） - 知乎](https://zhuanlan.zhihu.com/p/381526743)







### 如何查看 API

注意，master 分支上的 API 并一定是 so 文件里面所对应的 API。

我们要在跟so文件版本对上的分支里面去找 API。











## 参考资料

[Pre-trained models — sherpa 1.3 documentation](https://k2-fsa.github.io/sherpa/onnx/pretrained_models/index.html)

