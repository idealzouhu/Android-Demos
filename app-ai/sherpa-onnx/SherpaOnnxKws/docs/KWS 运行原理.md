## 一、KWS 运行原理

### 1.1 音频处理流程

#### 1.1.1 主要组件

| 组件            | 输出类型     | 实现位置      | 作用                 |
| --------------- | ------------ | ------------- | -------------------- |
| **Encoder**     | 声学特征向量 | ONNX 模型     | 将音频转换为特征表示 |
| **Decoder**     | 语言状态向量 | ONNX 模型     | 基于历史词元生成状态 |
| **Joiner**      | Logits       | ONNX 模型     | 合并声学和语言信息   |
| **Softmax**     | 概率分布     | ONNX 模型/C++ | 归一化为概率         |
| **tokens.txt**  | 词元映射表   | 模型文件      | Token ID 到词元的映射 |
| **keywords.txt**| 关键词定义   | 模型文件/用户配置 | 定义要检测的关键词 |
| **Beam Search** | 词元序列     | C++ (JNI)     | 从概率中解码出关键词 |



#### 1.1.2 完整处理流程

关键词检测系统从音频输入到关键词输出的完整处理流程如下：

```
┌──────────────────────────────────────────────────────────────┐
│                      完整处理流程                              │
├──────────────────────────────────────────────────────────────┤
│                                                               │
│  1. 音频输入 (PCM 16-bit, 16kHz)                             │
│     ↓                                                         │
│  2. 特征提取 (MFCC/FBank, 80维)                              │
│     ↓                                                         │
│  3. Encoder 推理 (ONNX)                                      │
│     输出: encoder_output [T, encoder_dim]                    │
│     ↓                                                         │
│  4. Decoder 推理 (ONNX) - 基于当前路径状态                    │
│     输出: decoder_output [decoder_dim]                       │
│     ↓                                                         │
│  5. Joiner 推理 (ONNX)                                       │
│     输出: logits [T, vocab_size]                             │
│     ↓                                                         │
│  6. Softmax 归一化                                           │
│     输出: probs [T, vocab_size] ← 模型的最终输出             │
│     ↓                                                         │
│  7. Beam Search 解码 (C++ 实现)                              │
│     输入: probs [T, vocab_size]                              │
│     处理: 构建路径、应用约束、选择 top-K                     │
│     输出: KeywordSpotterResult {                             │
│              keyword: "HELLO WORLD",                         │
│              tokens: ["HELLO", "WORLD"],                     │
│              timestamps: [0.5, 1.2]                          │
│           }                                                   │
│                                                               │
└──────────────────────────────────────────────────────────────┘
```



### 1.2 Zipformer 模型

#### 1.2.1 模型架构

本项目使用的是 **Zipformer** 模型，这是一种基于 **RNN-Transducer** 架构的高效端到端语音模型，包含三个组件：

- **Encoder**：处理音频特征，将音频转换为向量表示
- **Decoder**：处理历史词元序列，生成当前时间步的状态
- **Joiner**：合并 encoder 和 decoder 的输出，生成词元概率分布

```
┌──────────┐      ┌──────────┐      ┌─────────┐
│ Encoder  │ ───→ │  Joiner  │ ───→ │ 输出    │
│          │      │          │      │         │
└──────────┘      └──────────┘      └─────────┘
     ↑                 ↑
     │                 │
     │            ┌──────────┐
     └────────────│ Decoder  │
                  │          │
                  └──────────┘
```



#### 1.2.2 模型输出

对于每个时间步（约10ms），<font color="red">模型输出一个概率分布，表示在当前音频帧和历史词元序列的条件下，下一个词元是各个词元的概率</font>：

```python
# 模型输出格式（伪代码）
probs = [
    0.001,  # P(token_0 | 当前音频帧, 历史词元序列)
    0.002,  # P(token_1 | 当前音频帧, 历史词元序列)
    0.150,  # P(token_2 | 当前音频帧, 历史词元序列)  ← "HELLO" 的概率
    ...
    0.120,  # P(token_999 | 当前音频帧, 历史词元序列)
]
```

**要点：**
- 每个时间步都输出一次概率分布（不是整段音频一次输出）
- 概率分布依赖于当前音频帧和已生成的词元序列
- 不同历史路径会导致不同的概率分布



#### 1.2.3 tokens.txt 词汇表文件

**tokens.txt 是什么？**

`tokens.txt` 是模型的**词汇表文件**，定义了模型能够识别的所有词元（token）及其索引映射关系。

**文件格式：**

```
<blank>
<sos/eos>
▁HE
LL
O
▁WORLD
...
```

- 每行一个词元
- 行号（从 0 开始）就是该词元的索引（ID）
- 例如：第 0 行是 `<blank>`（空白词元），第 3 行是 `▁HE`

**tokens.txt 的作用：**

1. **Token ID 到词元的映射**
   - 模型输出的是 token ID（整数索引），不是文本
   - `tokens.txt` 用于将 ID 转换为实际的词元文本

2. **Beam Search 解码的必需文件**
   - Beam Search 需要知道每个 token ID 对应的词元
   - 用于检查路径是否匹配关键词
   - 用于将最终结果转换为可读文本

3. **解码流程示例：**

```
模型输出: [2, 3, 4, 5]  ← token IDs（概率分布中概率最高的词元索引）
         ↓
tokens.txt 查找:
  ID 2 → "▁HE"
  ID 3 → "LL"
  ID 4 → "O"
  ID 5 → "▁WORLD"
         ↓
解码结果: ["▁HE", "LL", "O", "▁WORLD"] → "HELLO WORLD"
```

**在代码中的使用：**

```kotlin
OnlineModelConfig(
    transducer = OnlineTransducerModelConfig(...),
    tokens = "$modelDir/tokens.txt",  // ← 指定 tokens.txt 路径
    modelType = "zipformer2",
)
```

**与 keywords.txt 的区别：**

| 文件 | 作用 | 内容 | 是否可自定义 |
|------|------|------|------------|
| **tokens.txt** | 词汇表，定义所有可能的词元 | 模型训练时确定的词元列表 | ❌ 不可修改（由模型决定） |
| **keywords.txt** | 关键词文件，定义要检测的关键词 | 用户自定义的关键词（已转换为词元序列） | ✅ 可以自定义 |

**关键理解：**
- `tokens.txt` 是模型的一部分，在模型训练时确定，包含了模型能识别的所有词元
- 关键词必须转换为词元序列，且这些词元必须都在 `tokens.txt` 中
- 没有 `tokens.txt`，系统无法将模型输出的数字索引转换为实际的词元，也就无法进行关键词检测



### 1.3 Beam Search 的作用

**Beam Search 是解码算法，在模型推理之后运行**，负责：

1. 从概率分布中构建词元序列
2. 应用关键词约束（只保留匹配关键词的路径）
3. 通过 boosting score 提升关键词路径的概率
4. 通过 trigger threshold 过滤低概率检测
5. 输出最终的关键词文本

**实现位置：** Beam Search 在 C++ 层实现（`libsherpa-onnx-jni.so`），通过 JNI 接口调用。



## 二、Beam Search 详解

### 2.1 为什么要使用 Beam Search

#### 2.1.1 关键词检测的挑战

1. **搜索空间巨大**：每个时间步可能有数千个词元，穷举搜索计算量呈指数级增长
2. **实时性要求**：需要实时处理，不能有太长延迟
3. **准确性要求**：需要准确识别关键词，同时避免误报
4. **资源限制**：移动设备计算资源有限

#### 2.1.2 为什么不用其他方法？

**Greedy Search（贪心搜索）**：
- 每个时间步只选择概率最高的词元
- 问题：可能错过全局最优解，无法回溯，不适合关键词检测

**穷举搜索**：
- 考虑所有可能的路径
- 问题：计算复杂度 O(V^T)，无法满足实时需求

**Beam Search（束搜索）**：
- 保留多个候选路径（beam width），避免局部最优
- 限制搜索宽度，控制计算复杂度 O(B × V × T)
- 可以回溯修正错误
- 特别适合关键词检测：可以限制搜索空间，只考虑匹配关键词的路径

#### 2.1.3 Beam Search 在 KWS 中的特殊作用

1. **路径约束**：只保留与给定关键词匹配的路径，其他路径被剪枝
2. **动态调整**：通过 boosting score 动态提升关键词路径的分数
3. **阈值过滤**：通过 trigger threshold 过滤掉概率过低的路径
4. **实时处理**：支持流式处理，可以边接收音频边解码

### 2.2 Beam Search 的运行流程

#### 2.2.1 基本概念

**核心参数：**
- **beam width**（束宽度，`maxActivePaths`）：每个时间步保留的候选路径数量

**关键数据结构：**
- **Beam**：当前时间步保留的候选路径集合
- **Path**：一条解码路径，包含词元序列、累积概率、解码器状态

#### 2.2.2 完整运行流程

**阶段 1：初始化**

```python
beam = [Path(tokens=[], prob=1.0, state=initial_state)]
keywords = load_keywords()  # 例如: ["HELLO", "WORLD"]
```

**阶段 2：逐时间步解码**

对于每个时间步 t：

**步骤 1：扩展当前 Beam**

```python
new_candidates = []
for path in beam:
    # 1. Encoder 处理音频特征
    encoder_output = encoder(audio_features[t])
    
    # 2. Decoder 基于当前路径状态生成输出
    decoder_output = decoder(path.state)
    
    # 3. Joiner 合并输出，得到 logits
    logits = joiner(encoder_output, decoder_output)
    
    # 4. Softmax 得到概率分布
    token_probs = softmax(logits)
    
    # 5. 为每个词元创建新路径
    for token_id, prob in enumerate(token_probs):
        new_path = Path(
            tokens=path.tokens + [token],
            prob=path.prob * prob,  # 累积概率
            state=update_state(path.state, token_id)
        )
        new_candidates.append(new_path)
```

**步骤 2：应用关键词约束**

```python
for candidate in new_candidates:
    match_result = check_keyword_match(candidate.tokens, keywords)
    
    if match_result.is_matching:
        # 应用 boosting score
        candidate.prob *= boosting_score
    elif match_result.is_impossible:
        # 剪枝：不可能匹配关键词的路径
        candidate.prob = 0
```

**步骤 3：选择 Top-K 路径**

```python
new_candidates.sort(key=lambda x: x.prob, reverse=True)
beam = new_candidates[:beam_width]
```

**步骤 4：检查是否检测到关键词**

```python
for path in beam:
    if path.tokens == keyword_tokens:
        normalized_prob = path.prob / len(path.tokens)
        
        if normalized_prob >= trigger_threshold:
            trigger_keyword_detection(path)
            beam = [Path(tokens=[], prob=1.0, state=initial_state)]
            break
```

**阶段 3：流式处理**

```python
while has_audio():
    audio_chunk = get_audio_chunk()  # 例如 100ms
    features = extract_features(audio_chunk)
    
    for feature_frame in features:
        beam = beam_search_step(beam, feature_frame)
        
        if check_keyword_detected(beam):
            handle_keyword_detected()
            reset_beam()
```

### 2.3 关键参数

#### 2.3.1 maxActivePaths (Beam Width)

```kotlin
var maxActivePaths: Int = 4
```

- **作用**：控制每个时间步保留的候选路径数量
- **影响**：
  - 值越大：准确性越高，但计算量越大
  - 值越小：速度越快，但可能错过正确的路径
- **推荐值**：通常 4-8 之间

#### 2.3.2 keywordsScore (Boosting Score)

```kotlin
var keywordsScore: Float = 1.5f
```

- **作用**：提升包含关键词的路径的概率
- **工作原理**：`P(path) × keywordsScore`
- **影响**：
  - 值越大：关键词路径更容易被保留，触发率更高，但误报也可能增加
  - 值越小：更依赖声学模型的原始概率

#### 2.3.3 keywordsThreshold (Trigger Threshold)

```kotlin
var keywordsThreshold: Float = 0.25f
```

- **作用**：过滤低概率的检测结果
- **工作原理**：如果 `normalized_prob(path) >= keywordsThreshold`，则触发检测
- **影响**：
  - 值越大：只触发高置信度的检测，误报少，但可能漏检
  - 值越小：更容易触发，检测率高，但误报可能增加

### 2.4 完整示例

假设要检测关键词 "HELLO WORLD"，beam width = 3：

**时间步 1：**
```
输入音频特征: [f1]
Beam 初始状态: [Path(tokens=[], prob=1.0)]

扩展后候选路径:
- Path(tokens=["HELLO"], prob=0.6) ✓ 匹配关键词前缀
- Path(tokens=["HELP"], prob=0.5)  ✗ 不匹配
- Path(tokens=["<blank>"], prob=0.4) 空白词元

应用 boosting (keywordsScore=1.5):
- Path(tokens=["HELLO"], prob=0.6 × 1.5 = 0.9) ✓
- Path(tokens=["HELP"], prob=0.5)
- Path(tokens=["<blank>"], prob=0.4)

Top-3 Beam:
1. Path(tokens=["HELLO"], prob=0.9)
2. Path(tokens=["HELP"], prob=0.5)
3. Path(tokens=["<blank>"], prob=0.4)
```

**时间步 2：**
```
输入音频特征: [f2]
当前 Beam: [Path(["HELLO"]), Path(["HELP"]), Path(["<blank>"])]

扩展 Path(["HELLO"]):
- Path(tokens=["HELLO", "WORLD"], prob=0.9 × 0.7 = 0.63) ✓ 完全匹配！

应用 boosting:
- Path(tokens=["HELLO", "WORLD"], prob=0.63 × 1.5 = 0.945) ✓

检查触发条件:
normalized_prob = 0.945 / 2 = 0.4725
trigger_threshold = 0.25
0.4725 >= 0.25 ✓ 触发检测！

结果: 检测到关键词 "HELLO WORLD"
```

### 2.5 优化技巧

1. **早停（Early Stopping）**：如果 beam 中所有路径的概率都很低，可以提前剪枝
2. **长度惩罚（Length Penalty）**：对长序列应用惩罚，避免生成过长的序列
3. **重复检测抑制**：检测到关键词后，在一段时间内抑制重复检测
4. **动态 Beam Width**：根据音频质量动态调整 beam width
5. **批处理优化**：如果处理多个音频流，可以批处理提高效率



## 三、总结

Beam Search 在关键词检测中起到关键作用：

1. **效率与准确性的平衡**：通过限制搜索宽度，在保证准确性的同时控制计算复杂度
2. **关键词约束**：只保留与给定关键词匹配的路径，实现开放词汇检测
3. **参数可调**：通过 boosting score 和 trigger threshold 灵活调整检测行为
4. **实时处理**：支持流式处理，满足实时应用需求

理解 Beam Search 的工作原理，有助于：
- 正确配置参数（beam width、boosting score、trigger threshold）
- 优化检测性能
- 调试检测问题
- 根据应用场景调整策略
