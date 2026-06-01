### 项目概述

本案例集中演示**主线程（UI 线程）被长时间占用或无法及时处理输入**时，容易触发的 **ANR（Application Not Responding）** 相关典型写法。实际工程中应将这些操作移到后台线程，并避免主线程参与死锁或长时间锁等待。

ANR 常见类型包括：**输入分发超时**（用户触摸后约 5s 内未处理完）、**广播接收超时**（`onReceive` 内耗时过长）等。本项目侧重与「主线程阻塞」强相关的经典情形，便于配合 Android Studio Profiler、系统 Trace 等工具对照分析。

---

### 常见引发 ANR 的典型错误情况（节选）

以下只列举**部分最常见、教学意义大**的几类，并非全部可能原因。

| 类型 | 错误写法概要 | 为何可能 ANR |
|------|----------------|--------------|
| **主线程休眠或长时间空转** | 在 `Activity`/`Fragment` 生命周期、`onClick`、`Choreographer` 回调等主线程路径上调用 `Thread.sleep(...)`，或长时间 `for` 循环做密集计算 | 主线程无法在规定时间内处理输入事件或完成帧回调，易触发 **Input dispatching timed out** |
| **主线程同步 I/O** | 在主线程读写大文件、使用**同步**数据库查询、或使用已废弃的 `SharedPreferences.Editor.commit()` 等可能阻塞磁盘的路径 | 磁盘或数据库较慢时，主线程被阻塞数秒即可 ANR |
| **主线程同步跨进程调用** | 在主线程直接调用可能耗时的 Binder 接口，例如未加限制的 `ContentResolver.query`、同步的 AIDL/服务调用 | 对端进程慢或系统负载高时，主线程在 Binder 上长时间等待，同样表现为无响应 |
| **死锁或锁顺序不当** | 主线程持锁 A 等待 B，后台线程持 B 又 `runOnUiThread` 等待 A；或主线程 `join()` 工作线程而该线程再等待主线程处理 | 主线程永久或长时间无法执行，输入与绘制停滞，表现为 ANR |
| **在 `BroadcastReceiver.onReceive` 中做重活** | 在 `onReceive` 主线程路径里做网络、大量磁盘或复杂计算（未 `goAsync()` / 未尽快交给后台） | 易触发 **BroadcastQueue Timeout** 类 ANR（与「输入超时」日志不同，但同属应用未按时完成工作） |

**说明：** 是否一定出现 ANR 与设备性能、数据量、系统版本有关；本仓库用于**故意复现与对照学习**，请勿将「演示用阻塞代码」照搬到正式产品。


### 项目结构

```
anr-main-blocking-java-view/
├── app/src/main/java/com/example/anr/block/
│   ├── MainActivity.java                    # 主界面：各按钮在主线程触发对应危险逻辑
│   └── HeavyBroadcastReceiver.java          # 广播 onReceive 内长时间阻塞（广播超时类）
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

- 区分「主线程被占满」与「跨进程等待」「I/O 阻塞」等常见 ANR 诱因在表现与排查上的差异
- 建立「耗时工作不进主线程」的习惯，并了解广播等短回调内的时限要求
- 结合官方文档与系统日志（如 `am_anr`、`Input dispatching timed out`）理解 ANR 触发条件的大致时间窗口
