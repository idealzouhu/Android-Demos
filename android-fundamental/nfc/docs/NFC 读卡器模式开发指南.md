[TOC]

## 一、基本知识

### 1.1 什么是读卡器模式

在 NFC 语境里，手机可以作为 **Initiator / Reader**，射频场由设备发起或维持，去探测、访问贴近的 **标签（Tag）**，读取或写入其存储的数据。

**设备充当读卡器，标签是被访问的从设备**。

### 1.2 标签调度机制（Tag Dispatch）

Android 检测到 NFC 标签后，按以下优先级派发 Intent：

| 优先级 | Intent 类型              | 触发条件           | 典型场景                     |
| ------ | ------------------------ | ------------------ | ---------------------------- |
| 1      | `ACTION_NDEF_DISCOVERED` | 标签包含 NDEF 数据 | 读取 URI、文本等标准格式数据 |
| 2      | `ACTION_TECH_DISCOVERED` | 支持特定标签技术   | 读写非标准标签               |
| 3      | `ACTION_TAG_DISCOVERED`  | 通用标签发现       | 兜底处理，获取原始标签对象   |

**调度流程**：系统解析标签 → 按注册的 Intent Filter 匹配 → 启动相应 Activity 或通知前台应用



### 1.3 NDEF 数据格式

NDEF（NFC Data Exchange Format）​ 是 NFC 数据交换的标准格式：

```
NdefMessage
├── NdefRecord
│   ├── TNF（类型名称格式）：指示数据类型
│   ├── Type：具体类型标识
│   ├── ID：记录标识符（可选）
│   └── Payload：实际数据载荷
└── NdefRecord...
```
**常用 NdefRecord 创建**：

```java
// 创建文本记录
NdefRecord.createTextRecord("en", "Hello World");

// 创建 URI 记录
NdefRecord.createUri("https://example.com");

// 创建应用启动记录
NdefRecord.createApplicationRecord("com.example.app");
```



### 1.4 前台优先调度机制

**问题**：多个应用可处理相同标签时，系统弹出选择器

**解决方案**：前台调度（Foreground Dispatch）

**原理**：当前 Activity 注册为优先接收标签事件

**使用场景**：APP 在前台时直接处理标签，无需用户选择



### 1.5 写入相对读取的额外考量

写入不仅要构造合法 **NdefMessage**，还要判断标签 **是否支持 NDEF、是否已格式化、是否可写、容量是否足够**，并在连接后调用写入 API；失败时需处理 **标签锁定、IO 异常** 等情况。



## 二、核心 API 详解

### 2.1 `NfcAdapter`：NFC 开关与硬件

| 用途 | 典型 API / 常量 |
|------|------------------|
| 是否存在 NFC 硬件、能否使用 | `NfcAdapter.getDefaultAdapter(Context)`，`null` 表示不支持 |
| 开关状态 | `isEnabled()` |
| 引导用户打开 NFC | `ACTION_NFC_SETTINGS` |
| 监听系统 NFC 开关变化 | 广播 `NfcAdapter.ACTION_ADAPTER_STATE_CHANGED`，Extras 中带 `NfcAdapter.EXTRA_ADAPTER_STATE` |

```java
// 获取 NFC 适配器
NfcAdapter nfcAdapter = NfcAdapter.getDefaultAdapter(context);

if (nfcAdapter == null) {
    // 设备不支持 NFC
    return;
}

if (!nfcAdapter.isEnabled()) {
    // NFC 未开启，引导用户打开
    Intent intent = new Intent(Settings.ACTION_NFC_SETTINGS);
    startActivity(intent);
}

// 监听 NFC 开关变化
BroadcastReceiver receiver = new BroadcastReceiver() {
    @Override
    public void onReceive(Context context, Intent intent) {
        int state = intent.getIntExtra(NfcAdapter.EXTRA_ADAPTER_STATE, 
                                      NfcAdapter.STATE_OFF);
        switch (state) {
            case NfcAdapter.STATE_ON:
                // NFC 已开启
                break;
            case NfcAdapter.STATE_OFF:
                // NFC 已关闭
                break;
        }
    }
};
IntentFilter filter = new IntentFilter(NfcAdapter.ACTION_ADAPTER_STATE_CHANGED);
registerReceiver(receiver, filter);
```



### 2.2 前台调度：`NfcAdapter` + `PendingIntent`

| 用途 | 典型 API |
|------|---------|
| 注册前台优先 | `enableForegroundDispatch(Activity, PendingIntent, IntentFilter[], techLists)` |
| 暂停或离开时注销 | `disableForegroundDispatch(Activity)` |
| 收到标签时多半通过 **PendingIntent 携带的 Intent** 交付 | Intent 中可取 `Tag`：`intent.getParcelableExtra(NfcAdapter.EXTRA_TAG)` |



### 2.3 标签实体：`android.nfc.Tag`

从 Intent 取得 **`Tag`** 后，表示一次射频会话中的标签句柄；后续按技术类型选用 **`Ndef`**、**`NdefFormatable`** 等封装类。



### 2.4 NDEF 标签读写封装：`Ndef`、`NdefFormatable`

| 场景 | 典型 API |
|------|---------|
| 标准 NDEF 标签读写 | `Ndef.get(Tag)` → `connect()` → `getNdefMessage()` / `writeNdefMessage(NdefMessage)` → `close()` |
| 标签支持 NDEF 但未格式化 | `NdefFormatable.get(Tag)` → `connect()` → `format(NdefMessage)`（视标签而定） |
| 是否可写、容量 | `Ndef`：`isWritable()`，`getMaxSize()` |

### 2.5 消息与记录：`NdefMessage`、`NdefRecord`

| 用途 | 典型 API |
|------|---------|
| 一条完整消息 | `new NdefMessage(NdefRecord[])` |
| 文本（Well-Known） | `NdefRecord.createTextRecord(locale, text)` |
| URI | `NdefRecord.createUri(String)` 或 `createUri(Uri)` |
| TNF / 载荷 | `getTnf()`，`getType()`，`getPayload()`；Well-Known 文本需注意语言编码前缀 |



## 三、配置清单

### 3.1 AndroidManifest.xml 配置

- **权限**：一般声明 `android.permission.NFC`（普通权限，安装时即可）。
- **硬件特性**：`<uses-feature android:name="android.hardware.nfc" android:required="true|false" />`，用于商店筛选与兼容性声明。
- xml/nfc_tech_filter.xml      # NFC技术类型过滤器