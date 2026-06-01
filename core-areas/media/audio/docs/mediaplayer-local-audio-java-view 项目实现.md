## 一、项目概述

本项目基于 Android MediaPlayer 实现了一个完整的本地音频播放器。



### 1.1 核心实现思路

核心思路是通过文件选择器获取用户选择的音频文件 URI，使用 MediaPlayer 进行播放控制，并通过 SeekBar 实现进度调节和音量控制功能。



### 1.2 关键组件

- **MediaPlayer**：核心音频播放组件，负责音频文件的加载、播放、暂停、停止等操作

- **DocumentFile**：处理 SAF 框架返回的文件 URI，获取文件信息

- **SeekBar**：实现播放进度显示和拖动控制

- **ConstraintLayout**：主界面布局，实现响应式 UI

- **ContentResolver**：通过 URI 访问文件内容



### 1.3 项目结构

```
mediaplayer-local-audio-java-view/
├── app/src/main/java/com/example/mediaplayer/localaudio/
│   ├── AudioPlayer.java                     # 封装的音频播放器类
│   └── MainActivity.java                    # 主 Activity，包含UI交互逻辑
├── app/src/main/res/
│   ├── layout/
│   │   └── activity_main.xml                # 音频播放器界面布局
│   └── values/
│       └── strings.xml                      # 字符串资源
├── app/src/main/AndroidManifest.xml         # 应用配置和权限声明
└── build.gradle                             # 项目依赖配置
```







## 二、功能模块详解

### 2.1 导入库

```kotlin
implementation 'androidx.documentfile:documentfile:1.0.1'
```

`DocumentFile` 对象用于**处理 Android 存储框架（Storage Access Framework, SAF）中的文件URI**， 即访问和操作 Android 上的文档文件。它允许应用在不直接持有文件路径的情况下获取文件信息，如名称、大小等，适用于处理外部存储或共享文档。

- **Android 10 (API 29) 之前**：可以直接通过文件路径访问，可以使用 File 对象。

- **Android 10+**：引入了分区存储（Scoped Storage），限制了直接文件路径访问， 只能使用 SAF 和 `ContentResolver`来访问文件



### 2.2 实现逻辑

查看 AudioPlayer 自定义类里面的方法即可。

- 文件选择与加载
- 播放控制逻辑
- 进度控制实现
- 音量控制实现
- MediaPlayer 生命周期管理








## 三、问题

### 应用内音量控制效果不太明显

Android应用中的音量控制与手机系统音量控制确实存在明显区别，主要体现在**控制层级**和**作用范围**上。

| 对比维度     | 应用内音量控制            | 手机系统音量控制     |
| ------------ | ------------------------- | -------------------- |
| **控制层级** | 应用层（软件音量）        | 系统层（硬件音量）   |
| **作用范围** | 仅当前应用                | 全局系统             |
| **实现方式** | `MediaPlayer.setVolume()` | 系统音频管理器       |
| **效果**     | 软件衰减，音质可能受损    | 硬件控制，音质无损   |
| **最大值**   | 0.0-1.0（相对值）         | 0-15（系统音量等级） |



音量的组成方式如下：

```
最终输出音量 = 系统音量 × 应用内音量 × 音频原始音量
            ↑                 ↑
AudioManager控制     MediaPlayer控制
```

当系统音量较小时，即使应用内音量再打，声音依旧很小。