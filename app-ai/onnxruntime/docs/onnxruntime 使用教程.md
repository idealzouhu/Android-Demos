

- **onnxruntime-extensions**: ONNX Runtime 的专用预处理和后处理库，支持将预处理和后处理功能内置到模型里面。以图片分类为例：

  ```
  # 你需要自己写：
  1. 加载图片 → resize → 归一化 → 转tensor  # 预处理
  2. 用ONNX Runtime运行模型             # 模型推理
  3. 解析输出 → 取top_k → 映射到标签       # 后处理
  
  
  # 用现成的组件：
  1. 调用扩展库的预处理模块
  2. 用ONNX Runtime运行模型
  3. 调用扩展库的后处理模块
  ```

  







onnxruntime 模型优化后的格式通常是 ort 格式。