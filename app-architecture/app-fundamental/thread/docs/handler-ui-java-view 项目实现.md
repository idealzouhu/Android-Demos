## 一、项目概述

本案例演示 Android 异步消息处理机制的基本使用方法，重点展示如何在子线程中通过 Handler 安全地更新 UI。



### 1.1 核心实现思路

- 在主线程创建 Handler 对象，用于接收和处理来自子线程的消息

- 在工作线程中执行耗时操作，完成后通过 Handler 发送消息到主线程

- 主线程的 Handler 在 `handleMessage()`方法中更新 UI

- 演示多种 Handler 使用方式：Message、Runnable、延时消息、HandlerThread 等



### 1.2 关键组件

- **WorkerThread** -： 自带 Looper 的自定义工作线程
- **MainActivity** ： 应用程序主界面，包含主线程的 Handler
- **MessageWhat** ： 消息类型常量定义类，用于标识不同消息用途



### 1.3 项目结构

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



## 二、功能模块详解

具体细节直接查看 MainActivity 中对应方法的函数即可。





## 三、运行效果

应用启动后，用户可以通过不同按钮测试各种Handler用法：

- 点击"发送Message"：在子线程处理后更新文本
- 点击"发送Runnable"：直接在主线程执行代码
- 点击"延时消息"：测试2秒和3秒的延时执行
- 点击"HandlerThread测试"：演示后台任务和进度更新
- 点击"清除消息"：清理所有未处理的消息








## 四、问题

暂无