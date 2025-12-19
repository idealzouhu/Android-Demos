

## 一、什么是 MediaPlayer

[MediaPlayer](https://developer.android.com/reference/android/media/MediaPlayer#developer-guides) 可以用于播放网络、本地以及应用程序安装包中的音频。这三种方式的区别在于**数据源设置、访问权限、缓冲机制、错误处理和性能**等方面。

| 特性           | 网络音频                                                     | 本地音频                                                     | 应用资源音频                                                 |
| ---------------- | ------------------------------------------------------------ | ------------------------------------------------------------ | ------------------------------------------------------------ |
| **数据源设置**   | `setDataSource(String url)`<br>`setDataSource(Context, Uri)`<br>`setDataSource(String url, Map<String, String> headers)` | `setDataSource(String path)`<br>`setDataSource(FileDescriptor)`<br>`setDataSource(Context, Uri)` | `MediaPlayer.create(Context, R.raw.audio)`<br>`setDataSource(AssetFileDescriptor)`<br>`setDataSource(AssetFileDescriptor, long, long)` |
| **访问权限**     | 需网络权限 | 需存储权限（Android 10+）或使用 `MediaStore` API | 无需额外权限                                                 |
| **缓冲机制**     | 需要网络缓冲<br>支持监听缓冲进度                             | 基本无缓冲                                                   | 无缓冲                                                       |
| **准备方法**     | 必须用 `prepareAsync()`                                      | 可用 `prepare()` 或 `prepareAsync()`                         | 可用 `MediaPlayer.create()`<br>或 `prepare()`                |
| **协议支持**     | HTTP/HTTPS, RTSP, HLS, DASH 等                               | 本地文件系统                                                 | 仅应用内资源文件                                             |
| **错误类型**     | 网络超时、连接中断、服务器错误、缓冲不足                     | 文件不存在、权限拒绝、格式不支持                             | 资源ID无效、APK损坏                                          |
| **性能特征**     | 启动慢、依赖网络、耗电高                                     | 启动快、稳定、耗电中                                         | 启动最快、最稳定、增加应用体积                               |
| **适用场景**     | 在线音乐、直播、播客                                         | 本地音乐播放、录音回放                                       | 应用提示音、游戏音效、内置音频                               |
| **生命周期管理** | 需注意暂停/恢复时的网络状态                                  | 需注意文件句柄释放                                           | 随应用生命周期自动管理                                       |





## 二、MediaPlayer 常用 API

![MediaPlayer State diagram](images/mediaplayer_state_diagram.gif)





### 2.1 状态控制方法

```java
// 基础播放控制
void start()                     // 开始播放
void pause()                    // 暂停播放
void stop()                     // 停止播放
void release()                  // 释放所有资源
void reset()                    // 重置到空闲状态
void seekTo(int msec)           // 跳转到指定位置（毫秒）
void setLooping(boolean looping) // 设置循环播放
void setVolume(float leftVolume, float rightVolume) // 设置音量

// 状态查询
boolean isPlaying()             // 是否正在播放
int getCurrentPosition()        // 获取当前播放位置（毫秒）
int getDuration()               // 获取音频总时长（毫秒）
```





### 2.2 数据源设置方法

```
// 网络音频
void setDataSource(String path)  // 从URL或文件路径
void setDataSource(Context context, Uri uri)  // 从Uri
void setDataSource(String path, Map<String, String> headers) // 带请求头

// 本地音频
void setDataSource(FileDescriptor fd)  // 从文件描述符
void setDataSource(FileDescriptor fd, long offset, long length) // 部分文件

// 应用资源
void setDataSource(AssetFileDescriptor afd)  // 从Asset资源
void setDataSource(AssetFileDescriptor afd, long offset, long length)

// 静态创建方法（常用于资源音频）
static MediaPlayer create(Context context, int resid)  // 从资源ID创建
static MediaPlayer create(Context context, Uri uri)    // 从Uri创建
static MediaPlayer create(Context context, Uri uri, SurfaceHolder holder) // 带Surface
```



### 2.3 准备与配置方法

```
void prepare()                  // 同步准备（阻塞UI线程）
void prepareAsync()             // 异步准备（推荐）
void setAudioStreamType(int streamtype)  // 设置音频流类型
void setAudioAttributes(AudioAttributes attributes)  // 设置音频属性（API 21+）
void setWakeMode(Context context, int mode)  // 设置唤醒模式
void setScreenOnWhilePlaying(boolean screenOn)  // 设置播放时保持屏幕亮起
```





### 2.4 监听器接口

```java
// 设置监听器
void setOnPreparedListener(MediaPlayer.OnPreparedListener listener)
void setOnCompletionListener(MediaPlayer.OnCompletionListener listener)
void setOnErrorListener(MediaPlayer.OnErrorListener listener)
void setOnInfoListener(MediaPlayer.OnInfoListener listener)
void setOnBufferingUpdateListener(MediaPlayer.OnBufferingUpdateListener listener)
void setOnSeekCompleteListener(MediaPlayer.OnSeekCompleteListener listener)

// 回调接口定义
interface OnPreparedListener {
    void onPrepared(MediaPlayer mp)  // 准备完成
}

interface OnCompletionListener {
    void onCompletion(MediaPlayer mp)  // 播放完成
}

interface OnErrorListener {
    boolean onError(MediaPlayer mp, int what, int extra)  // 错误回调
}

interface OnInfoListener {
    boolean onInfo(MediaPlayer mp, int what, int extra)  // 信息回调
}

interface OnBufferingUpdateListener {
    void onBufferingUpdate(MediaPlayer mp, int percent)  // 缓冲进度更新
}
```



### 2.5 重要常量/错误码

```java
// 错误码（what参数）
int MEDIA_ERROR_UNKNOWN = 1;           // 未知错误
int MEDIA_ERROR_SERVER_DIED = 100;     // 服务器失效
int MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK = 200; // 渐进播放无效
int MEDIA_ERROR_IO = -1004;           // I/O错误
int MEDIA_ERROR_MALFORMED = -1007;     // 格式错误
int MEDIA_ERROR_UNSUPPORTED = -1010;   // 不支持格式
int MEDIA_ERROR_TIMED_OUT = -110;      // 超时

// 信息码（what参数）
int MEDIA_INFO_UNKNOWN = 1;            // 未知信息
int MEDIA_INFO_VIDEO_TRACK_LAGGING = 700; // 视频轨道延迟
int MEDIA_INFO_BUFFERING_START = 701;  // 缓冲开始
int MEDIA_INFO_BUFFERING_END = 702;    // 缓冲结束
int MEDIA_INFO_NETWORK_BANDWIDTH = 703; // 网络带宽
int MEDIA_INFO_BAD_INTERLEAVING = 800;  // 交错错误
int MEDIA_INFO_NOT_SEEKABLE = 801;     // 不可跳转
int MEDIA_INFO_METADATA_UPDATE = 802;  // 元数据更新
```







## 参考资料

[MediaPlayer  | API reference  | Android Developers](https://developer.android.com/reference/android/media/MediaPlayer#developer-guides)

[About MediaPlayer  | Android media  | Android Developers](https://developer.android.google.cn/media/platform/mediaplayer)