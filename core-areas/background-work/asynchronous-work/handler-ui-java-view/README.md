### 项目概述

本案例演示 Android 异步消息处理机制的基本使用方法，重点展示如何在子线程中通过 Handler 安全地更新 UI。


### 项目结构

```
handler-ui-java-view/
├── app/src/main/java/com/example/handler/ui/
│   ├── MainActivity.java                    # 主 Activity，演示各种 Handler 用法
│   ├── WorkerThread.java                     # 自定义工作线程
│   └── MessageWhat.java                      # 消息类型常量定义
├── app/src/main/res/
│   ├── layout/
│   │   └── activity_main.xml                 # 主界面布局文件
│   └── values/
│       ├── colors.xml                        # 颜色资源
│       ├── strings.xml                       # 字符串资源
│       ├── dimens.xml                        # 尺寸资源
│       └── styles.xml                        # 样式主题配置
└── app/build.gradle.kts                      # 项目依赖配置
```

### 学习目标

通过该项目，你将掌握：

- Handler 的基本创建和使用方法
- 子线程与主线程（UI线程）间的安全通信
- 多种消息发送方式：Message、Runnable、延时消息