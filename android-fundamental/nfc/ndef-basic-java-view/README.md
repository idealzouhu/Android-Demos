### 项目概述

本 Demo 面向 NFC 入门开发，将三类常见能力合并到同一工程中：**环境与兼容性检测**、**读卡器模式下读取 NDEF 标签**、**读卡器模式下写入 NDEF 标签**。不涉及具体业务协议扩展，而是打好「设备是否可用 → 前台能否稳定收到标签 → 能否解析/构造 NDEF」这条主线，便于对照官方文档继续深入（如 ReaderMode、HCE 等）。



### 项目结构

```
ndef-basic-java-view/
├── app/                                    
│   ├── build.gradle.kts                    
│   └── src/
│       ├── main/
│       │   ├── AndroidManifest.xml         # 权限声明、Intent过滤器配置
│       │   ├── java/com/example/ndef/basic/
│       │   │   ├── MainActivity.java       # 主界面，协调NFC功能
│       │   │   └── nfc/                    # NFC核心功能包
│       │   │       ├── NfcStateMonitor.java    # NFC状态监听器
│       │   │       ├── NdefReader.java         # NDEF读取解析器
│       │   │       ├── NdefWriter.java         # NDEF写入处理器
│       │   │       └── NdefPayloads.java       # NDEF数据构造工具
│       │   └── res/
│       │       ├── layout/activity_main.xml    
│       │       ├── values/strings.xml           
│       │       ├── values/themes.xml            
│       │       └── xml/nfc_tech_filter.xml      # NFC技术类型过滤器
│       ├── androidTest/                    
│       └── test/                           
├── build.gradle.kts                        
├── settings.gradle.kts                     
├── gradle/libs.versions.toml              
└── README.md                              
```



### 学习目标

通过该项目，你将掌握：

- **NFC 基础状态与权限**：使用 `NfcAdapter.getDefaultAdapter()` 判断硬件支持；监听 `ACTION_ADAPTER_STATE_CHANGED` 响应系统 NFC 开关变化并引导用户开启；正确声明 `android.permission.NFC` 与 `uses-feature`，建立兼容性意识。

- **读标签（NDEF，文本/URL）**：在前台使用前台调度（Foreground Dispatch）优先接收 NFC 事件；通过 Intent 过滤（如 `ACTION_NDEF_DISCOVERED` / `ACTION_TECH_DISCOVERED`）理解标签调度系统；从 `Tag` / Intent Extra 解析 `NdefMessage`、`NdefRecord`，展示文本或 URL。

- **写标签（NDEF）**：判断标签是否可写、是否需要先格式化；构造 `NdefRecord`（如文本型 TNF_WELL_KNOWN、URI 型）并封装为 `NdefMessage`；通过 `Ndef` 连接、`writeNdefMessage()` 完成写入，必要时使用 `NdefFormatable.format()`，并处理标签已锁、写入失败等异常场景。
