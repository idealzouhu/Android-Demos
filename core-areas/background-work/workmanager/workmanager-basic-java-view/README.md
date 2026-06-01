### 项目概述

本案例演示了 Android 中 WorkManager 的基础使用方法，重点展示了如何实现后台任务的调度、执行和状态监控。

### 项目结构

```
workmanager-basic-java-view/

├── app/src/main/java/com/example/workmanager/basic/
│   ├── MainActivity.java                    # 主 Activity，包含 WorkManager 的配置和任务管理
│   └── UploadWorker.java                    # Worker 类，执行具体的后台任务
│
├── app/src/main/res/
│   ├── layout/
│   │   └── activity_main.xml                # 主界面布局文件，包含任务控制按钮和状态显示
│   └── values/
│       ├── strings.xml                      # 字符串资源
│       ├── colors.xml                       # 颜色资源
│       └── themes.xml                       # 样式主题配置
│
├── app/build.gradle.kts                     # 应用模块依赖配置
├── build.gradle.kts                         # 项目级构建配置
└── gradle/libs.versions.toml                # 依赖版本管理

```

### 学习目标

通过该项目，你将掌握：

- **WorkManager 基础使用**：如何创建 Worker、构建 WorkRequest、提交任务、观察任务状态
- **一次性任务**：使用 `OneTimeWorkRequest` 创建执行一次的任务
- **周期性任务**：使用 `PeriodicWorkRequest` 创建定期执行的任务（最小间隔 15 分钟）
- **任务状态监控**：使用 `LiveData` 实时观察任务状态变化（ENQUEUED、RUNNING、SUCCEEDED 等）
- **任务管理**：如何取消任务、查询任务状态
- **数据持久化**：了解 WorkManager 如何持久化任务信息，确保应用重启后任务状态不丢失