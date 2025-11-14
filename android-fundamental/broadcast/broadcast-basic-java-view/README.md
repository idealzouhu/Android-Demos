### 项目概述

本案例完整演示了 Android 广播机制的核心用法，涵盖动态注册监听系统事件、静态注册实现开机自启、应用内普通广播通信以及有序广播的优先级处理。通过四个典型场景，帮助开发者全面掌握广播的使用方法和最佳实践
。


### 项目结构

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

### 学习目标

通过该项目，你将掌握：

1. 广播机制核心概念
   广播类型：全局广播 vs 本地广播，隐式广播 vs 显式广播的区别和应用场景
   注册方式：动态注册与静态注册的适用场景及生命周期管理
   系统限制：Android 8.0+ 对静态注册和后台执行的限制与适配方案
2. 四种广播模式实现
   动态注册：监听系统时间变化 (ACTION_TIME_TICK)，掌握组件生命周期内的广播监听
   静态注册：实现开机自启 (BOOT_COMPLETED)，了解系统事件监听的特殊配置
   普通广播：应用内组件间通信，实现松耦合架构设计
   有序广播：优先级处理机制，掌握广播中止和结果传递