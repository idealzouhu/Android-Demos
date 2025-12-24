### 项目概述

本案例是一个完整的 Android 通知功能演示应用，展示了 Android 系统中各种类型的通知实现方式，包括基础通知、大文本通知、大图片通知、进度通知、带操作按钮的通知以及可回复的通知。


### 项目结构

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

### 学习目标

通过该项目，你将掌握：

- 简单文本通知、大文本通知（支持展开显示更多内容）、大图片通知（支持图片缩放）、进度通知、带操作按钮的通知、可回复的通知
- 通知渠道管理，如创建通知渠道、设置渠道属性
- 取消单个通知或者所有通知
