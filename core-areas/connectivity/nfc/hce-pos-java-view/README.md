### 项目概述

本 Demo 面向 **卡模拟（HCE）**：在手机端通过 **`HostApduService`**，按 **ICC 脚本（JSON）** 中给出的 **C-APDU / R-APDU** 向量 **顺序、逐字节** 响应读卡器，用于在 **授权、封闭测试环境** 下模拟 **银行卡非接 / EMV 风格交易会话**（应用选择、GPO、读记录、GENERATE AC 等均可写入同一脚本格式）。

脚本格式 **与具体内核品牌无关**：凡导出为 `APDU_CMDS` + `APDU0`/`RPDU0`… 的测试向量（银联、外卡、MIR 等）均可替换 `assets` 中的 JSON 使用。**不涉及真实清算网络**，请勿用于生产或未授权场景。



### 定制脚本与路由

- **脚本文件**：默认读取 `app/src/main/assets/icc_apdu_script.json`（常量见 `IccScriptLoader.ASSET_FILE_NAME`）。替换为你的向量文件时需保持文件名一致，或修改该常量。
- **AID 声明**：系统按 `res/xml/apdu_service.xml` 中的 **`aid-filter`** 将 SELECT（PPSE / 应用）路由到本服务。脚本里出现的 DF Name / AID（十六进制）须在此处一并声明，否则读卡器无法选中你的 HCE 服务。



### 项目结构

```
hce-pos-java-view/
├── app/
│   ├── build.gradle.kts
│   └── src/
│       ├── main/
│       │   ├── AndroidManifest.xml          # NFC/HCE 权限、HostApduService
│       │   ├── assets/
│       │   │   └── icc_apdu_script.json     # 默认示例 ICC 脚本（可替换）
│       │   ├── java/com/example/hce/pos/
│       │   │   ├── MainActivity.java        # HCE 能力检测、脚本状态、付款设置入口
│       │   │   └── iccsim/
│       │   │       ├── ApduHex.java
│       │   │       ├── IccScript.java / IccScriptStep.java
│       │   │       ├── IccScriptLoader.java
│       │   │       ├── IccScriptSession.java / IccProcessResult.java
│       │   │       ├── IccScriptHostApduService.java
│       │   │       └── IccHceDebugState.java
│       │   └── res/
│       │       ├── layout/activity_main.xml
│       │       ├── values/strings.xml
│       │       └── xml/apdu_service.xml     # payment AID，须与脚本 SELECT 一致
│       ├── androidTest/
│       └── test/                            # Loader / Hex / Session 单元测试
├── build.gradle.kts
├── settings.gradle.kts
├── gradle/libs.versions.toml
└── README.md
```



### 学习目标

通过该项目，你将掌握：

- **NFC 与卡模拟相关声明**：判断设备是否支持 **HCE**（如 `FEATURE_NFC_HOST_CARD_EMULATION`）；在 Manifest 中配置 **`HostApduService`**、`android.nfc.card_emulation` 元数据、**`aid_list`**，以及 **`BIND_NFC_SERVICE`** 等权限，理解「系统如何把读卡器 APDU 路由到你的服务」。

- **APDU 收发与响应**：实现 **`processCommandApdu(byte[])`**，解析常见 **CLA / INS / P1 / P2 / Lc / Data / Le** 结构；使用 **`sendResponseApdu(byte[])`** 返回 **R-APDU**（数据 + **SW1 SW2**，如 `9000`）；在 **`onDeactivated()`** 中处理会话结束，建立与 POS 侧命令序列对应的调试意识。

- **脚本化虚拟卡**：用统一 JSON 承载 **完整交互脚本**，在合规测试环境中复现与 POS **字节级一致** 的会话；区分「封闭测试模拟」与「真实发卡/清算」的差异。
