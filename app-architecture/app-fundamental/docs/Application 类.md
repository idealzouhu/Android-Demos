[TOC]

## 一、Application 概述

### 1.1 Application 是什么？

Application类是一个全局单例级的基础组件，用于维护全局应用程序状态。其代表整个 App 进程的生命周期，在 App 进程创建时最早被实例化。

可以理解为：

> **Application = 整个 App 的“全局上下文 + 入口管理器”**





### 1.2 Application 的生命周期

| 方法                              | 调用时机             | 典型用途                         |
| --------------------------------- | -------------------- | -------------------------------- |
| `attachBaseContext(Context base)` | **最早执行**         | 初始化 MultiDex、插件化、热修复  |
| `onCreate()`                      | Application 创建完成 | 初始化 SDK、全局配置             |
| `onTerminate()`                   | 理论上进程被杀死前   | **几乎不会被调用（不推荐依赖）** |
| `onLowMemory()`                   | 系统内存不足         | 释放缓存                         |
| `onTrimMemory(int level)`         | 内存紧张             | 优化内存占用                     |



### 1.3 Application 的应用场景

- 初始化全局资源: 极少量、必须最早执行的初始化，不建议初始化第三方SDK、非必须的同步初始化。
- 提供全局 Context
- 管理全局状态（慎用）
- 监听 App 前后台状态（重要）：埋点，登录态校验，防截屏，自动登出





## 二、最佳实践

- 不要在 Application 中做耗时操作：会拖慢 App 启动，建议延迟初始化 / 按需初始化
- 不要存大对象
- 使用 `ContentProvider`或 `App Startup`：拆分初始化逻辑，避免 Application 臃肿。





## 参考资料

[Application  | API reference  | Android Developers](https://developer.android.google.cn/reference/android/app/Application)

[ `<application>`| App architecture  | Android Developers](https://developer.android.google.cn/guide/topics/manifest/application-element.html)