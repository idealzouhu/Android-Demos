### 项目概述

本工程演示 Android 中多类**典型内存泄漏**写法：静态引用、单例保存 `Activity`、`Handler` 延迟消息、后台线程闭包、静态 `View`、静态持有匿名 `Runnable` 等。**仅用于学习**，请勿照搬到正式代码。

**Debug** 依赖 [LeakCanary](https://github.com/square/leakcanary)，安装 Debug 包后无需额外初始化即可在泄漏疑似发生时给出通知（Release 不包含）。

### 经典场景与对应类

| 场景 | 说明 | 代码入口 |
|------|------|-----------|
| 静态 Activity | `static Activity` 在进程内一直可达 | `LeakStaticActivityRef` |
| 单例 Context | 单例保存 `Activity` 而非 `Application` | `LeakSingletonContext` |
| Handler 延迟 | `postDelayed` 的 `Runnable` 捕获 `Activity` | `LeakHandlerDelayed` |
| 后台线程 | `Thread` 的 `run` 捕获 `Activity`，睡眠期间无法回收 | `LeakThreadHold` |
| 静态 View | `View` 使用 Activity `Context` 创建，再被静态引用 | `LeakStaticView` |
| 静态 Runnable | 匿名内部类 `Runnable` 隐式持有外部 `MainActivity` | `LeakStaticRunnableRef` + `MainActivity` |

### 使用方式建议

1. 使用 **Debug** 构建安装到设备或模拟器。
2. 进入主界面，**点击某一泄漏场景**，再按**返回**离开 `MainActivity`（或进入最近任务划掉）。
3. 等待 LeakCanary 完成堆分析后查看通知中的引用链。
4. 实验结束点击 **「清除全部引用」**，或卸载应用，避免静态状态影响下一次实验。

### 项目结构（核心）

```
memory-leak-static-holder-java-view/
├── app/src/main/java/com/example/memory/leak/
│   ├── MainActivity.java
│   ├── LeakStaticActivityRef.java
│   ├── LeakSingletonContext.java
│   ├── LeakHandlerDelayed.java
│   ├── LeakThreadHold.java
│   ├── LeakStaticView.java
│   └── LeakStaticRunnableRef.java
└── app/build.gradle.kts   # debugImplementation leakcanary-android
```

### 学习目标

- 理解「长生命周期对象持有短生命周期对象」的常见路径
- 对照 LeakCanary 引用链，将抽象概念与具体字段/类名对应起来
- 掌握正确方向：弱引用、`ApplicationContext`、取消回调、`removeCallbacks`、生命周期感知组件等
