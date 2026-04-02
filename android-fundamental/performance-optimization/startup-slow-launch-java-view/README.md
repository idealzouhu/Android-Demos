### 项目概述

本案例集中演示 **启动阶段在 `Application.onCreate()` 中执行重活** 这一类经典错误写法。由于 `Application` 会在应用进程创建后、首个页面展示前尽早初始化，如果在这里同步执行大量计算、阻塞 I/O、长时间休眠或重量级组件初始化，就会直接拉长冷启动耗时，导致首屏展示延迟、启动白屏时间变长，严重时还可能引发启动阶段卡顿甚至 ANR 风险。

本项目刻意聚焦 **一个最典型、最常见的启动性能问题**，便于结合 Android Studio Profiler、System Trace 或 `adb shell am start -W` 等方式观察启动时间与主线程行为。

---

### 典型错误案例

| 类型 | 错误写法概要 | 为什么会拖慢启动 |
|------|--------------|------------------|
| **`Application.onCreate()` 做重活** | 在 `Application` 启动早期同步执行大循环计算、阻塞磁盘读取、长时间 `sleep`、或初始化多个重量级单例/SDK | 这些工作通常发生在首页绘制前，主线程必须先跑完这段逻辑，导致冷启动时长明显增加，首帧延后 |



### 项目结构

```
startup-slow-launch-java-view/
├── app/src/main/java/com/example/startup/slow/
│   ├── App.java                             # Application：在 onCreate 中故意执行重活
│   └── MainActivity.java                    # 主界面：用于观察启动结果与说明现象
├── app/src/main/res/
│   ├── layout/
│   │   └── activity_main.xml
│   └── values/
│       ├── strings.xml
│       └── themes.xml
├── build.gradle.kts
└── settings.gradle.kts
```

### 学习目标

通过该项目，你将掌握：

- 理解为什么 `Application.onCreate()` 是冷启动关键路径，错误地放入重活会直接拖慢首屏展示
- 学会识别启动阶段不应立即执行的任务，并区分「必须首启完成」与「可延迟初始化」的工作
- 能结合 Profiler / Trace 观察启动耗时，并将启动慢的问题定位到 `Application` 早期初始化阶段
