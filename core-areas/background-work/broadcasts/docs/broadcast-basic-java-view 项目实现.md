## 项目结构

```
broadcast-basic-java-view/
├── app/src/main/java/com/example/broadcast/basic/
│   ├── MainActivity.java                    # 主Activity，演示动态注册和广播发送
│   ├── BootCompleteReceiver.java            # 静态注册接收器 - 开机自启
│   ├── NormalBroadcastReceiver.java         # 普通广播接收器
│   ├── HighPriorityReceiver.java            # 有序广播 - 高优先级接收器
│   └── LowPriorityReceiver.java             # 有序广播 - 低优先级接收器
├── app/src/main/res/
│   ├── layout/
│   │   └── activity_main.xml                # 主界面布局文件
│   ├── menu/
│   │   └── main_menu.xml                    # 菜单资源文件
│   └── values/
│       ├── strings.xml                      # 字符串资源
│       └── styles.xml                       # 样式主题配置
├── app/src/main/AndroidManifest.xml         # 应用配置和静态注册声明
└── app/build.gradle                         # 项目依赖配置
```



## 功能模块详解

### 1. 动态注册 - 系统时间监听

**实现文件**：`MainActivity.java`

- 监听 `Intent.ACTION_TIME_TICK`系统广播（每分钟触发）
- 在 `onCreate()`中注册，`onDestroy()`中注销，确保生命周期安全
- 实时更新UI显示当前时间，演示前台广播处理

### 2. 静态注册 - 开机自启功能

**实现文件**：`BootCompleteReceiver.java`和 `AndroidManifest.xml`

- 接收 `android.intent.action.BOOT_COMPLETED`系统广播
- 在清单文件中声明权限和静态接收器配置
- 设备启动后自动发送通知，展示系统级事件处理

### 3. 普通广播 - 应用内通信

**实现文件**：`NormalBroadcastReceiver.java`和 `MainActivity.java`

- 自定义广播 Action：`com.example.broadcastdemo.ACTION_NORMAL_BROADCAST`
- 应用内部组件间通信，数据安全不泄露给其他应用
- 使用 `sendBroadcast()`发送，异步处理所有接收器

### 4. 有序广播 - 优先级处理

**实现文件**：`HighPriorityReceiver.java`, `LowPriorityReceiver.java`

- 通过 `android:priority`属性设置处理顺序（100 > 50）
- 高优先级接收器可中止广播 (`abortBroadcast()`) 或修改结果数据
- 使用 `sendOrderedBroadcast()`发送，同步顺序处理





## 问题

### 静态注册的广播接收器接收不到广播

#### 问题描述

静态注册的有序广播器，无法被接收到广播消息

```
    <!-- 静态注册：有序广播的接收器（并设置优先级） -->
        <receiver
            android:name=".HighPriorityReceiver"
            android:enabled="true"
            android:exported="false">
            <intent-filter android:priority="100">
                <!-- 优先级最高，最先接收 -->
                <action android:name="com.example.broadcast.basic.ACTION_ORDERED_BROADCAST" />
            </intent-filter>
        </receiver>
        <receiver
            android:name=".LowPriorityReceiver"
            android:enabled="true"
            android:exported="false">
            <intent-filter android:priority="50">
                <!-- 优先级较低，在高优先级之后接收 -->
                <action android:name="com.example.broadcast.basic.ACTION_ORDERED_BROADCAST" />
            </intent-filter>
        </receiver>
```

```
  findViewById(R.id.btn_send_ordered).setOnClickListener(v -> {
            Intent intent = new Intent("com.example.broadcast.basic.ACTION_ORDERED_BROADCAST");
            intent.putExtra("base_message", "请依次处理：");
            sendOrderedBroadcast(intent, null); // 关键方法：发送有序广播
        });
```



#### 原因分析

在 Android 8.0 之前，你代码中的发送方式 `sendOrderedBroadcast(intent, null)`是完全可行的。但自从 Android 8.0 起，系统**对静态注册的接收器接收隐式广播施加了严格限制**。

- **隐式广播**：指那些**没有明确指定目标组件（如 `ComponentName`）或目标包名（`PackageName`）** 的广播。它仅通过 Action 等条件来匹配接收者。你的原始代码 `new Intent("com.example...")`就创建了一个隐式广播。
- **显式广播**：指**明确指定了发送给哪个应用或组件**的广播，例如通过 `setPackage()`或 `setComponent()`方法。

因此，本文自定义的隐式广播将无法被静态注册的接收器接收。



#### 解决方案

添加 `intent.setPackage(getPackageName()); ` ，使其变为显式广播。

```
        // 发送有序广播
        findViewById(R.id.btn_send_ordered).setOnClickListener(v -> {
            Intent intent = new Intent("com.example.broadcast.basic.ACTION_ORDERED_BROADCAST");
            intent.putExtra("base_message", "请依次处理：");
            intent.setPackage(getPackageName());
            sendOrderedBroadcast(intent, null); // 关键方法：发送有序广播
        });
```





### 优先级低的广播接收器先收到消息

#### 问题描述

优先级低的广播接收器 LowPriorityReceiver 先收到消息，然后 HighPriorityReceiver 再收到消息

```
   <!-- 静态注册：有序广播的接收器（并设置优先级） -->
        <receiver
            android:name=".HighPriorityReceiver"
            android:enabled="true"
            android:exported="false">
            <intent-filter android:priority="100">
                <!-- 优先级最高，最先接收 -->
                <action android:name="com.example.broadcast.basic.ACTION_ORDERED_BROADCAST" />
            </intent-filter>
        </receiver>
        <receiver
            android:name=".LowPriorityReceiver"
            android:enabled="true"
            android:exported="false">
            <intent-filter android:priority="50">
                <!-- 优先级较低，在高优先级之后接收 -->
                <action android:name="com.example.broadcast.basic.ACTION_ORDERED_BROADCAST" />
            </intent-filter>
        </receiver>
```

```
        // 发送有序广播
        findViewById(R.id.btn_send_ordered).setOnClickListener(v -> {
            Intent intent = new Intent("com.example.broadcast.basic.ACTION_ORDERED_BROADCAST");
            intent.putExtra("base_message", "请依次处理：");
            intent.setPackage(getPackageName());
            sendOrderedBroadcast(intent, null); // 关键方法：发送有序广播
        });
```





#### 原因分析

(1) 更换注册方式

在 OPPO PDCM00 上， 按照如下方式运行：

```
    <!-- 静态注册：有序广播的接收器（并设置优先级） -->
        <receiver
            android:name=".LowPriorityReceiver"
            android:enabled="true"
            android:exported="false">
            <intent-filter android:priority="100">
                <!-- 优先级较低，在高优先级之后接收 -->
                <action android:name="com.example.broadcast.basic.ACTION_ORDERED_BROADCAST" />
            </intent-filter>
        </receiver>
        <receiver
            android:name=".HighPriorityReceiver"
            android:enabled="true"
            android:exported="false">
            <intent-filter android:priority="50">
                <!-- 优先级最高，最先接收 -->
                <action android:name="com.example.broadcast.basic.ACTION_ORDERED_BROADCAST" />
            </intent-filter>
        </receiver>
```

结果确实 HighPriorityReceiver 先接收到消息，，然后 LowPriorityReceiver再收到消息

**android:priority 似乎完全没作用，后注册的 Receiver 必然先运行。**





**(2) 使用动态注册**

在 OPPO PDCM00 上

```
   @SuppressLint("UnspecifiedRegisterReceiverFlag")
    private void registerOrderedBroadcastReceivers() {
        // 创建高优先级接收器实例
        highPriorityReceiver = new HighPriorityReceiver();
        // 创建低优先级接收器实例
        lowPriorityReceiver = new LowPriorityReceiver();

        // 创建 IntentFilter 并设置优先级
        IntentFilter highPriorityFilter = new IntentFilter();
        highPriorityFilter.addAction("com.example.broadcast.basic.ACTION_ORDERED_BROADCAST");
        highPriorityFilter.setPriority(100); // 设置高优先级

        IntentFilter lowPriorityFilter = new IntentFilter();
        lowPriorityFilter.addAction("com.example.broadcast.basic.ACTION_ORDERED_BROADCAST");
        lowPriorityFilter.setPriority(50); // 设置低优先级

        // 动态注册接收器
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(highPriorityReceiver, highPriorityFilter, Context.RECEIVER_EXPORTED);
            registerReceiver(lowPriorityReceiver, lowPriorityFilter, Context.RECEIVER_EXPORTED);
        } else {
            registerReceiver(highPriorityReceiver, highPriorityFilter);
            registerReceiver(lowPriorityReceiver, lowPriorityFilter);
        }
    }
```

低优先级的先运行



(3) 更换手机

同样的项目

- 在 Medium Phone API 36.0 上，高优先级的接收器先收到广播
- 在 OPPO PDCM00 上，低优先级的接收器先收到广播



#### 解决方案

使用 Medium Phone API 36.0 手机进行测试即可。