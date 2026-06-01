### 项目概述

本案例演示了 Android 中使用 MediaPlayer 播放本地音频文件的完整实现，重点展示了音频播放的核心功能和控制逻辑。


### 项目结构

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

### 学习目标

通过该项目，你将掌握：

- MediaPlayer 的完整生命周期管理
- 音频播放进度实时显示与拖动控制
- 音频播放状态的回调处理
- 本地音频文件的加载和播放控制
