[TOC]

## 一、TAG 标签：物理与逻辑的桥梁

### 1.1 物理标签：硬件载体

物理标签是具备 NFC 功能的实体硬件设备，作为数据的物理载体，内部包含微芯片和存储区，但**数据不直接暴露**。其常见类型与形态有：

- **卡片类**：银行卡、公交卡、门禁卡、空白 NFC 标签卡。
- **贴纸类**：可粘贴的 NTAG 贴纸（常用于智能海报、设备配对）。
- **特殊形态**：手环、钥匙扣、护照芯片、部分手机（模拟卡模式）。



### 1.2 TAG 对象：Android 的标签表示

在 Android 系统中，TAG 是检测到 NFC 目标时的数据容器对象，表示**物理标签的软件抽象**。它不包含物理标签里面的数据，而是 Android 系统扫描物理标签后生成的元数据对象。

```
物理世界：                    Android 系统：
┌──────────────┐             ┌─────────────────────┐
│  NFC 卡片/标签 │ ←   扫描 →  │ 生成 Tag 对象         │
│  (硬件)       │             │  (软件对象)           │
└──────────────┘             └─────────────────────┘
    (银行卡、门禁卡、标签)       (包含标签元数据的容器)
```

**Tag 对象**包含标签ID、支持的技术列表、服务句柄等内容，其核心结构为：

```java
public final class Tag implements Parcelable {
    private byte[] mId;           // 标签唯一 ID
    private int[] mTechList;      // 支持的技术列表
    private Bundle[] mTechExtras; // 每种技术的附加数据
    private int mServiceHandle;   // NFC 系统服务句柄（关键）
    
    // 关键方法
    public String[] getTechList();  // 获取支持的技术类型
    public byte[] getId();          // 获取标签 UID
}
```

其中，Tag对象包含一个服务句柄（serviceHandle），这个句柄是连接到 NFC 系统服务的桥梁。IsoDep实例通过这个句柄调用底层服务

```
应用层：                   框架层：                  HAL层：
┌─────────────┐         ┌─────────────┐         ┌─────────────┐
│ IsoDep      │         │ NFC Service │         │ NFC 控制器   │
│  对象        │ ↔ Tag ↔ │  (系统服务)   │ ↔ JNI ↔ │  (硬件驱动) │
└─────────────┘         └─────────────┘         └─────────────┘
    ↑                           ↑                       ↑
    └─── mTag (包含服务句柄) ───┘               └── 与物理NFC芯片通信
```



### 1.3 技术检测机制

NFC 控制器扫描物理标签时的过程：

1. 发送 **SENS_REQ**（Type A）或 **SENSB_REQ**（Type B）
2. 接收标签的 **ATQA/ATS** 响应
3. 根据响应判断标签类型
4. Android 将类型转换为对应技术字符串

**响应类型映射**：

- 收到 **ATS**（Answer To Select）→ ISO 14443-4 → `"android.nfc.tech.IsoDep"`
- 收到 **SAK = 0x20** → Type 4A → `"android.nfc.tech.IsoDep" + "android.nfc.tech.NfcA"`



### 1.4 如何获取 TAG 对象

在 Android NFC 开发中，获取 `Tag`对象是进行所有 NFC 操作的前提，主要有以下几种方式：

| **方式**          | **调用时机**                  | **获取 Tag 的方法**                                          |
| :---------------- | :---------------------------- | :----------------------------------------------------------- |
| **前台调度**      | Activity 在前台时             | `onNewIntent(Intent)`中通过 `intent.getParcelableExtra(NfcAdapter.EXTRA_TAG)` |
| **Intent 过滤器** | Activity 被 NFC Intent 启动时 | `onCreate()`或 `onNewIntent()`中从 Intent 获取               |
| **Reader Mode**   | 启用 Reader Mode 后           | `onTagDiscovered(Tag)`回调直接提供 Tag 对象                  |

**(1) 通过前台调度系统（Foreground Dispatch）**

当应用在前台运行时检测到 NFC 标签。

```java
// 启用前台调度
@Override
protected void onResume() {
    super.onResume();
    Intent intent = new Intent(this, getClass())
        .addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
    PendingIntent pendingIntent = PendingIntent.getActivity(
        this, 0, intent, PendingIntent.FLAG_MUTABLE);
    
    String[][] techLists = new String[][] {
        {"android.nfc.tech.IsoDep"},
        {"android.nfc.tech.NfcA"},
        {"android.nfc.tech.Ndef"}
    };
    
    nfcAdapter.enableForegroundDispatch(this, pendingIntent, null, techLists);
}

// 在 onNewIntent 中获取 Tag
@Override
protected void onNewIntent(Intent intent) {
    super.onNewIntent(intent);
    if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction())) {
        Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        processTag(tag);
    }
}
```



**(2) 通过 Intent 过滤器**

通过配置 `AndroidManifest.xml` 自动接收 NFC 标签。

```xml
<!-- AndroidManifest.xml -->
<activity android:name=".MainActivity">
    <!-- 处理所有 NFC 标签 -->
    <intent-filter>
        <action android:name="android.nfc.action.TAG_DISCOVERED" />
        <category android:name="android.intent.category.DEFAULT" />
    </intent-filter>
</activity>
```

```java
// 在 Activity 的 onCreate 或 onNewIntent 中获取
@Override
protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    // 处理启动 Activity 的 Intent
    processIntent(getIntent());
}

private void processIntent(Intent intent) {
    if (intent != null && NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction())) {
        Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        processTag(tag);
    }
}
```



**(3) 通过 Reader Mode（Android 4.4+）**

需要更精细控制 NFC 读取过程，支持后台读取。

```java
public class MainActivity extends AppCompatActivity 
        implements NfcAdapter.ReaderCallback {
    
    @Override
    protected void onResume() {
        super.onResume();
        // 启用 Reader Mode
        nfcAdapter.enableReaderMode(this, this, 
            NfcAdapter.FLAG_READER_NFC_A | 
            NfcAdapter.FLAG_READER_SKIP_NDEF_CHECK, null);
    }
    
    // ReaderCallback 回调
    @Override
    public void onTagDiscovered(Tag tag) {
        // 直接获取到 Tag 对象
        processTag(tag);
    }
}
```





## 二、标签通信协议

### 2.1 标签通信协议

NFC 标签硬件在通信时遵循的**底层协议规范**，有较多种技术标准。

| **技术标准**             | **描述**                   | **应用场景**       |
| :----------------------- | :------------------------- | :----------------- |
| **ISO/IEC 14443 Type A** | 最常见的短距离标准，1-4cm  | 门禁卡、公交卡     |
| **ISO/IEC 14443 Type B** | 安全性更好的短距离标准     | 身份证、护照       |
| **ISO/IEC 14443-4**      | 在Type A/B之上的传输层协议 | 银行卡、交通卡     |
| **ISO/IEC 15693**        | 远距离读取，约1米ying      | 资产管理、库存标签 |
| **MIFARE Classic**       | 非标准私有协议             | 旧式门禁卡         |
| **NFC Forum Type 1-4**   | 标准NDEF数据格式           | 智能海报、网址标签 |





### 2.2 通信架构

NFC 卡片在硬件实现上分为两大阵营，通信模型完全不同。

> 注意，对于简单的卡片（如门禁卡），你可以直接操作底层；但对于银行卡等复杂卡片，必须走传输层通道。因为银行卡在物理层（Type A）之上**强制启用了 ISO/IEC 14443-4 协议层**。如果你尝试用 `NfcA`直接发送 APDU 指令，会得到错误响应或超时。

**(1) 智能卡（CPU卡）架构**

**代表**：银行卡、身份证、社保卡、交通联合卡

**核心特征**：卡片内置**微处理器**和**安全芯片**，可执行加密运算和复杂逻辑。

| 层级       | 标准                      | 作用                                           | Android 对应类       |
| ---------- | ------------------------- | ---------------------------------------------- | -------------------- |
| **应用层** | **ISO/IEC 7816-4 (APDU)** | **业务逻辑** 定义指令格式（如读文件、验证PIN） | 由应用解析 APDU 响应 |
| **传输层** | **ISO/IEC 14443-4**       | **可靠传输** 提供可靠的数据块传输通道          | IsoDep               |
| **底层**   | **14443 Type A / Type B** | **物理连接** 负责唤醒卡片、防冲突、基础通信    | NfcA/ NfcB           |

**通信流程**：`APDU → 14443-4 → Type A/B → 物理卡片`



**(2) 存储卡（逻辑加密卡）架构**

**代表**：MIFARE Classic、MIFARE Ultralight、简单门禁卡

**核心特征**：卡片本质是**带简单逻辑的存储芯片**，无独立 CPU，安全性低。

| 层级           | 标准               | 作用                       | Android 对应类    |
| -------------- | ------------------ | -------------------------- | ----------------- |
| **私有协议层** | **厂商自定义指令** | 厂商自定义的读写、认证指令 | `MifareClassic`等 |
| **物理层**     | **14443 Type A**   | 建立基础射频连接           | `NfcA`            |

**通信流程**：`厂商私有指令 → Type A → 物理卡片`

**无传输层和应用层**，指令与响应都是简单的字节流，**不走 APDU 格式**。





## 三、Android 标签技术类

### 3.1  标签技术类概览

Android 通过  [`android.nfc.tech`](https://developer.android.google.cn/reference/android/nfc/tech/package-summary?hl=zh-cn) 包提供了一套标准化的接口，用于与不同类型的 NFC 标签进行通信。每个类对应一种特定的 [NFC 通信协议](https://developer.android.google.cn/develop/connectivity/nfc/advanced-nfc?hl=zh-cn#tag-tech)或技术标准。

Android 支持的标签技术：

| 类                                                           | 说明                                                         |
| :----------------------------------------------------------- | :----------------------------------------------------------- |
| [`TagTechnology`](https://developer.android.google.cn/reference/android/nfc/tech/TagTechnology?hl=zh-cn) | 这是所有标签技术类都必须实现的接口。                         |
| [`NfcA`](https://developer.android.google.cn/reference/android/nfc/tech/NfcA?hl=zh-cn) | 提供对 NFC-A (ISO 14443-3A) 属性和 I/O 操作的访问权限。      |
| [`NfcB`](https://developer.android.google.cn/reference/android/nfc/tech/NfcB) | 提供对 NFC-B (ISO 14443-3B) 属性和 I/O 操作的访问权限。      |
| [`NfcF`](https://developer.android.google.cn/reference/android/nfc/tech/NfcF) | 提供对 NFC-F (JIS 6319-4) 属性和 I/O 操作的访问权限。        |
| [`NfcV`](https://developer.android.google.cn/reference/android/nfc/tech/NfcV) | 提供对 NFC-V (ISO 15693) 属性和 I/O 操作的访问权限。         |
| [`IsoDep`](https://developer.android.google.cn/reference/android/nfc/tech/IsoDep) | 提供对 ISO-DEP (ISO 14443-4) 属性和 I/O 操作的访问权限。     |
| [`Ndef`](https://developer.android.google.cn/reference/android/nfc/tech/Ndef) | 提供对以下格式的 NFC 标签上 NDEF 数据和操作的访问权限： NDEF。 |
| [`NdefFormatable`](https://developer.android.google.cn/reference/android/nfc/tech/NdefFormatable) | 为可设置为 NDEF 格式的标签提供格式化操作。                   |

Android 设备可选择支持的标签技术。

| 类                                                           | 说明                                                         |
| :----------------------------------------------------------- | :----------------------------------------------------------- |
| [`MifareClassic`](https://developer.android.google.cn/reference/android/nfc/tech/MifareClassic?hl=zh-cn) | 提供对 MIFARE Classic 属性和 I/O 操作的访问权限（如果此 Android 设备支持 MIFARE）。 |
| [`MifareUltralight`](https://developer.android.google.cn/reference/android/nfc/tech/MifareUltralight?hl=zh-cn) | 提供对 MIFARE Ultralight 属性和 I/O 操作的访问权限（如果此 Android 设备支持 MIFARE。 |



### 3.2 标签技术类的使用方式

处理NFC标签时，首先通过 `Tag`对象的 [`getTechList()`](https://developer.android.google.cn/reference/android/nfc/Tag?hl=zh-cn#getTechList()) 方法获取其支持的技术类型列表，然后创建对应的  [`TagTechnology`](https://developer.android.google.cn/reference/android/nfc/tech/TagTechnology?hl=zh-cn) 对象（例如 `IsoDep`, `NfcA`等）进行具体操作。对于银行卡等复杂卡片，**应优先使用 `IsoDep`** 技术。

```java
// 获取标签支持的技术列表
Tag tag = getTagFromIntent(intent);
String[] techList = tag.getTechList();  // 例如: ["android.nfc.tech.NfcA", "android.nfc.tech.IsoDep"]

// 根据技术类型创建对应的技术对象
if (Arrays.asList(techList).contains("android.nfc.tech.IsoDep")) {
    IsoDep isoDep = IsoDep.get(tag);
    // 处理银行卡等复杂卡片
} else if (Arrays.asList(techList).contains("android.nfc.tech.NfcA")) {
    NfcA nfcA = NfcA.get(tag);
    // 处理门禁卡等简单卡片
}
```

注意，一个 `Tag`对象在同一时间只能被一个 `TagTechnology`实例占用。如果你用 `NfcA`连接了银行卡，会导致后续无法切换到 `IsoDep`。





### 3.2 NFCA 标签技术

NfcA用于与不实现 ISO/IEC 14443-4 传输层的简单 NFC 标签通信。这类标签通常功能单一，使用厂商特定的私有指令（如 MIFARE 的 `0x30`读块指令）。

```java
// 示例：直接操作 MIFARE Classic（非 IsoDep 卡）
NfcA nfcA = NfcA.get(tag);
nfcA.connect();
byte[] cmd = new byte[] {(byte)0x30, (byte)0x00}; // 读块0的私有指令
byte[] response = nfcA.transceive(cmd); // 直接收发原始字节
```



### 3.2 IsoDep 标签技术

在 Android NFC 开发中，[`IsoDep`](https://developer.android.google.cn/reference/android/nfc/tech/IsoDep?hl=zh-cn)(ISO/IEC 14443-4 Data Exchange Protocol )是一个技术类，代表支持 ISO/IEC 14443-4 传输层协议的标签。它本质上是基于 Type A 或 Type B 底层通信，但提供了更高级的、面向数据块（Block）的通信能力，专门用于传输 APDU 指令。

IsoDep 是一种通信协议， 既用于**读卡器模式**，也用于**卡模拟模式**。

- 当你开发 HCE 应用时（手机当卡刷），你写的是一个 `HostApduService`（HCE 服务）。当读卡器靠近时，数据会通过 **IsoDep 协议** 传输到你的服务。
- 当你开发读卡应用时（手机当读卡器），通过 `NfcAdapter`的标签发现回调获取到 `Tag`对象，接着通过 `IsoDep.get(tag)`获取该标签的 IsoDep 实例，**调用 `connect()`方法建立连接后**，即可通过 `transceive()`方法发送 APDU 指令（基于 Iso Dep 协议）与卡片进行数据交换。

```
┌─────────────────────────────────────────────────────────────┐
│                  IsoDep 协议双向通信                           │
├─────────────────────┬───────────────────────────────────────┤
│   手机作为读卡器       │        手机作为卡片 (HCE)               │
├─────────────────────┼───────────────────────────────────────┤
│ 读取外部智能卡         │ 模拟智能卡被外部读卡器读取                 │
│                     │                                       │
│ 实现：               │ 实现：                                 │
│ 1. IsoDep.get(tag)  │ 1. 继承 HostApduService                │
│ 2. transceive(apdu) │ 2. 实现 processCommandApdu()           │
│                     │                                        │
│ 应用场景：            │ 应用场景：                              │
│ • 银行卡读取          │ • 手机支付                              │
│ • 身份证验证          │ • 门禁卡模拟                            │
│ • 公交卡查询          │ • 交通卡模拟                            │
└─────────────────────┴───────────────────────────────────────┘
```

使用案例如下：

```java
// 必须通过 IsoDep 通道
IsoDep isoDep = IsoDep.get(tag);
isoDep.connect();

// 发送标准的 SELECT 或 READ BINARY APDU
byte[] apdu = Hex.decode("00A4040008A0000000031010");
byte[] result = isoDep.transceive(apdu);
```

注意，`connect` 和 `transceive` 必须在同一线程调用，否则可能会出现问题。



## 参考资料

[高级 NFC 概览  | Connectivity  | Android Developers](https://developer.android.google.cn/develop/connectivity/nfc/advanced-nfc?hl=zh-cn#read-write)

