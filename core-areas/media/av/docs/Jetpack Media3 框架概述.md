[TOC]

## Jetpack Media3 框架概述

在 Application Framework 之上，Google 推出了 **Jetpack Media3**，这是一套现代化的媒体开发框架。Jetpack Media3 并非完全独立于系统框架，而是有选择地依赖其底层基础设施：

- **依赖**：MediaCodec（解码）、AudioTrack（音频输出）、Surface（视频渲染）、MediaDrm（DRM 解密）
- **不依赖**：`MediaPlayer`、`MediaExtractor`、`MediaSync`等高层 API，由 ExoPlayer 自身的实现替代。

其核心模块有：

| **模块**               | **说明**                                       |
| :--------------------- | :--------------------------------------------- |
| **media3-exoplayer**   | 播放引擎，即 ExoPlayer，替代系统 MediaPlayer   |
| **media3-ui**          | 播放器 UI 组件（PlayerView）                   |
| **media3-session**     | 媒体会话管理（MediaSession / MediaController） |
| **media3-datasource**  | 数据源层（网络加载、缓存、加密等）             |
| **media3-decoder**     | 解码器扩展                                     |
| **media3-extractor**   | 解封装器（支持 MP4/MKV/FLV 等）                |
| **media3-transformer** | 视频编辑与转码                                 |
| **media3-cast**        | Chromecast 投屏支持                            |

> Media3 没有独立编码器模块，因为编码能力被封装在 `media3-transformer`内部，服务于视频编辑/转码场景。如果你需要纯粹的编码 API（如实时推流），仍需直接使用系统 MediaCodec 或第三方库。



## exoplayer 组件

具体详情查看 [Create a basic media player app using Media3 ExoPlayer  | Android media  | Android Developers](https://developer.android.google.cn/media/implement/playback-app#managing-playback) 和 [Media3 ExoPlayer  | Android media  | Android Developers](https://developer.android.google.cn/media/media3/exoplayer)





### MediaSession 组件

**MediaSession** 是 Jetpack Media3 提供的**媒体会话管理组件**，用来将你的播放器状态（播放/暂停/进度/元数据）暴露给系统和其他应用。





## 参考资料

[Audio and video overview  | Android media  | Android Developers](https://developer.android.google.cn/media/audio-and-video)