## 专题总览

研究 nfc 的使用方法，从以下三个角度来考虑：

| 模式              | 手机角色            | 典型场景                     | 关键类/技术                        |
| ----------------- | ------------------- | ---------------------------- | ---------------------------------- |
| **读卡器/写入器** | **主动方** (Reader) | 读取公交卡余额、写入智能标签 | `NfcAdapter`, `NdefMessage`, `Tag` |
| **卡模拟 (HCE)**  | **被动方** (Card)   | 手机刷公交、虚拟门禁卡       | `HostApduService`, `AID`           |
| **点对点 (P2P)**  | **对等设备** (Peer) | 手机传文件、分享联系人       | `NdefPush`, `Beam`                 |



### 基础知识

- [NFC 基础知识.md](docs\NFC 基础知识.md) : 重点阐述TAG的获取和使用，以及不同种类的 NFC 通信协议。

- [NFC 读卡器模式开发指南](docs/NFC 读卡器模式开发指南.md)：概括读卡器模式概念、标签调度、NDEF 与前台调度，并梳理常用 Android API，可作系统学习与查阅索引。

- [卡模拟模式开发指南](docs\卡模拟模式开发指南.md): 介绍卡模拟模式的常用 API 和使用案例。



### 实现项目

- [ndef-basic-java-view 项目实现](ndef-basic-java-view/README.md)：NDEF 读写示例工程入口，说明构建方式、模块划分与关键实现。
- [hce-pos-java-view 项目实现](hce-pos-java-view\README.md):  在开放测试 POS 环境下，用 `HostApduService` 与可配置的自定义 APDU/脚本规则模拟虚拟卡，便于联调与学习卡模拟通信流程。





## 参考资料

[近距离无线通信 (NFC) 概览  | Connectivity  | Android Developers](https://developer.android.google.cn/develop/connectivity/nfc?hl=zh-cn)
