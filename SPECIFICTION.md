# Android 技术研究项目规范

## 一、命名规范

### 1.1 技术专题命名

**格式**：`技术领域-具体功能`

示例如下：

```
qrcode-scanner          # 二维码扫描技术专题
camera-api              # 相机 API 专题  
network-request         # 网络请求专题
permission-handling     # 权限处理专题
image-loading           # 图片加载专题
theme-system            # 主题系统专题
architecture-pattern    # 架构模式专题
di-framework            # 依赖注入框架专题
```



### 1.2 技术具体项目命名

在每个技术专题下面，有许多个技术具体项目。

**格式**：`功能-技术栈-语言-UI框架`

> 在官方的 Android 案例中，推荐的命名格式是 帕斯卡命名法。但是，为了方便使用，本项目统一采用连字符

**组成部分**：

1. **功能**：`qrcode`, `camera`, `network`等
2. **技术栈**：`zxing`, `mlkit`, `retrofit`, `camerax`等
3. **编程语言**：`kotlin`, `java`等
4. **UI框架**：`compose`, `view`（如无UI可省略）

示例如下：

```
qrcode-zxing-kotlin-compose     # ZXing + Kotlin + Compose
qrcode-mlkit-java-view          # ML Kit + Java + View
camera-camerax-kotlin-compose   # CameraX + Kotlin + Compose
network-retrofit-kotlin         # Retrofit + Kotlin（纯逻辑）
database-room-kotlin            # Room + Kotlin（纯逻辑）

```



## 二、目录结构规范

### 2.1 技术专题目录

```
技术专题/
├── 📱 功能-技术栈-语言-UI框架1/      # 具体实现项目1
├── 📱 功能-技术栈-语言-UI框架2/      # 具体实现项目2
├── 📁 docs/                       # 📍 集中文档目录
│   ├── 📄 README.md               # 技术基础知识与项目索引
│   ├── 📄 ARCHITECTURE.md         # 架构设计分析
│   ├── 📄 COMPARISON.md           # 技术实现对比分析
│   ├── 📄 BENCHMARK.md            # 性能测试数据对比
│   ├── 📄 IMPLEMENTATION.md       # 各实现细节文档
│   └── 📁 screenshots/            # 所有项目截图
│       ├── 项目1截图/
│       └── 项目2截图/
├── 📄 README.md                   # 专题总览
└── 📄 CHOICE_GUIDE.md             # 技术选型指南
```



#### 2.1.1 技术专题级文档（在 `docs/`目录下）

**`README.md`** - 技术基础与项目索引



项目效果截图：

- 建议放在 COMPARSION 文档里面。

- 或者分开放在每个项目的具体实现文档里面。



#### 2.1.2 项目组织原则

1. **独立可运行**：技术专题下的每个项目都是完整的独立案例，包含完整的配置和构建脚本

2. **技术对比导向**：相关技术实现集中在同一专题下，便于横向比较
   - ✅ 正确示例：`networking/`专题包含 Retrofit、Ktor、Volley 等多种实现
   - ❌ 避免：为每个网络库创建单独的技术专题

3. **学习路径清晰**：项目排列体现从基础到进阶的学习顺序，便于循序渐进学习



### 2.2 技术具体项目

将技术具体项目的详细文档放到技术专题下的 docs 目录里面。

```
功能-技术栈-语言-UI框架/
├── 📱 app/                       # Android 应用模块
│   ├── src/main/
│   └── build.gradle.kts
├── 📄 README.md                  # 项目快速开始指南
├── 📄 build.gradle.kts           # 项目构建配置
├── 📄 settings.gradle.kts        # 项目设置
└── 📁 gradle/                    # Gradle 包装器
```



#### 2.2.1 具体项目级文档

**`README.md`** - 项目说明

可以添加一下学习目标。

```
# 项目名称

简单的项目描述。

## 技术栈
- **功能实现**：[技术栈]
- **编程语言**：[语言]
- **UI框架**：[UI框架]
- **目标版本**：Android X+

## 快速开始
1. 克隆项目
2. 使用 Android Studio 打开
3. 运行应用

## 功能特性
- [特性1]
- [特性2]

## 项目结构
[简要说明]
```







## 三、实际案例

在 android studio 创建项目的过程中，

- **Name**: `listview-basic-java-view`
- **Package Name**: `com.example.listview.basic`

- **Save Location**: `F:\Android-Demos\list-view\listview-basic-java-view`