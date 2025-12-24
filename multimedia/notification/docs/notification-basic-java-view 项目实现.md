## 一、项目概述

本案例是一个完整的 Android 通知功能演示应用，展示了 Android 系统中各种类型的通知实现方式，包括基础通知、大文本通知、大图片通知、进度通知、带操作按钮的通知以及可回复的通知。

### 1.1 核心实现思路

本项目采用**分层架构**和**工具类封装**的设计思路，旨在演示 Android 通知系统的完整功能。核心实现包括：

1. **工具类封装**：将通知创建、发送、管理逻辑封装到 `NotificationUtils`中，实现代码复用
2. **配置驱动**：通过 `NotificationConfig`类统一管理通知参数，支持 Builder 模式配置
3. **渠道管理**：自动创建和管理 Android 8.0+ 的通知渠道
4. **事件驱动**：通过广播接收器处理通知操作和回复事件



### 1.2 关键组件

| 组件名称                       | 职责说明                         | 核心方法                                                     |
| ------------------------------ | -------------------------------- | ------------------------------------------------------------ |
| **MainActivity**               | 主界面，处理用户交互             | `onCreate()`, `setupListeners()`, `sendSimpleNotification()` |
| **NotificationUtils**          | 通知工具类，封装所有通知相关操作 | `sendNotification()`, `sendSimpleNotification()`, `cancelNotification()` |
| **NotificationConfig**         | 通知配置类，使用 Builder 模式    | `Builder()`, `setTitle()`, `setContent()`, `build()`         |
| **NotificationChannelManager** | 通知渠道管理器                   | `createAllChannels()`, `channelExists()`, `deleteChannel()`  |
| **NotificationActionReceiver** | 处理通知操作按钮点击             | `onReceive()`, `handleAction1()`, `handleAction2()`          |
| **NotificationReplyReceiver**  | 处理通知回复内容                 | `onReceive()`, `handleReply()`, `sendReplySentNotification()` |
| **activity_main.xml**          | 主界面布局文件                   | 采用三层结构：固定标题栏、可滚动内容区、固定状态栏           |



### 1.3 项目结构

```
notification-basic-java-view/
├── app/src/main/java/com/example/notification/basic/
│   ├── MainActivity.java                           # 主 Activity
│   ├── NotificationReplyReceiver.java              # 回复通知广播接收器
│   ├── NotificationActionReceiver.java             # 操作按钮广播接收器
│   └── utils/
│       ├── NotificationUtils.java                  # 通知工具类
│       ├── NotificationConfig.java                 # 通知配置类
│       └── NotificationChannelManager.java         # 通知渠道管理器
├── app/src/main/res/
│   ├── layout/
│   │   └── activity_main.xml
│   ├── drawable/
│   │   ├── ic_notification.xml                     # 通知图标
│   │   └── ic_action_play.xml                      # 播放图标
│   ├── values/
│   │   ├── strings.xml
│   │   └── styles.xml
│   └── menu/
│       └── main_menu.xml
├── app/src/main/AndroidManifest.xml
└── build.gradle
```



## 二、功能模块详解

发送通知的代码具体细节查看 `NotificationUtils` 。






## 三、问题

### 3.1 FLAG_MUTABLE 使用限制

#### 3.1.1 问题描述

```
FATAL EXCEPTION: main
Process: com.example.notification.basic, PID: 4042
java.lang.IllegalArgumentException: com.example.notification.basic: Targeting U+ (version 34 and above) disallows creating or retrieving a PendingIntent with FLAG_MUTABLE, an implicit Intent within and without FLAG_NO_CREATE and FLAG_ALLOW_UNSAFE_IMPLICIT_INTENT for security reasons. To retrieve an already existing PendingIntent, use FLAG_NO_CREATE, however, to create a new PendingIntent with an implicit Intent use FLAG_IMMUTABLE.
	at android.os.Parcel.createExceptionOrNull(Parcel.java:3344)
	at android.os.Parcel.createException(Parcel.java:3324)
	at android.os.Parcel.readException(Parcel.java:3307)
	at android.os.Parcel.readException(Parcel.java:3249)
	at android.app.IActivityManager$Stub$Proxy.getIntentSenderWithFeature(IActivityManager.java:7082)
	at android.app.PendingIntent.getBroadcastAsUser(PendingIntent.java:765)
	at android.app.PendingIntent.getBroadcast(PendingIntent.java:748)
	at com.example.notification.basic.utils.NotificationUtils.createReplyPendingIntent(NotificationUtils.java:219)
	at com.example.notification.basic.MainActivity.sendReplyNotification(MainActivity.java:350)
	at com.example.notification.basic.MainActivity.lambda$setupListeners$7$com-example-notification-basic-MainActivity(MainActivity.java:192)
	at com.example.notification.basic.MainActivity$$ExternalSyntheticLambda6.onClick(D8$$SyntheticClass:0)
	at android.view.View.performClick(View.java:8083)
	at com.google.android.material.button.MaterialButton.performClick(MaterialButton.java:1345)
	at android.view.View.performClickInternal(View.java:8060)
	at android.view.View.-$$Nest$mperformClickInternal(Unknown Source:0)
	at android.view.View$PerformClick.run(View.java:31549)
	at android.os.Handler.handleCallback(Handler.java:995)
	at android.os.Handler.dispatchMessage(Handler.java:103)
	at android.os.Looper.loopOnce(Looper.java:248)
	at android.os.Looper.loop(Looper.java:338)
	at android.app.ActivityThread.main(ActivityThread.java:9067)
	at java.lang.reflect.Method.invoke(Native Method)
	at com.android.internal.os.RuntimeInit$MethodAndArgsCaller.run(RuntimeInit.java:593)
	at com.android.internal.os.ZygoteInit.main(ZygoteInit.java:932)
Caused by: android.os.RemoteException: Remote stack trace:
	at com.android.server.am.ActivityManagerService.getIntentSenderWithFeatureAsApp(ActivityManagerService.java:5530)
	at com.android.server.am.ActivityManagerService.getIntentSenderWithFeature(ActivityManagerService.java:5472)
	at android.app.IActivityManager$Stub.onTransact(IActivityManager.java:3508)
	at com.android.server.am.ActivityManagerService.onTransact(ActivityManagerService.java:2735)
	at android.os.Binder.execTransactInternal(Binder.java:1421)
```





#### 3.1.2 原因分析

Android 14 (API 34) 安全限制：目标 SDK 34 不允许使用 FLAG_MUTABLE 创建 PendingIntent





### 3.2 回复通知卡在"加载中"状态

#### 3.2.1 问题描述

当你点击回复按钮后，系统会一直显示加载动画（转圈圈）。

然后，NotificationReplyReceiver 注册了，但是没有收到相应的广播。

```xml
        <!-- 通知回复接收器 -->
        <receiver
            android:name=".NotificationReplyReceiver"
            android:exported="false">
            <intent-filter>
                <action android:name="com.example.notification.ACTION_REPLY" />
            </intent-filter>
        </receiver>
```





#### 3.2.2 原因分析

这是可回复通知的**常见问题**。

当你点击回复按钮时，Android 系统会：

1. 暂停通知的加载动画
2. 等待你的应用处理回复
3. 你的应用**必须告诉系统处理已完成**





