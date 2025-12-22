## 一、什么是 VideoView

在 Android 应用开发中，[`VideoView`](https://developer.android.com/reference/android/widget/VideoView#VideoView(android.content.Context))是一个专为视频播放设计的高级视图组件，它通过封装 `MediaPlayer`和 `SurfaceView`，大大简化了视频播放功能的实现流程。

VideoView的核心定位是成为一个开箱即用的视频播放解决方案。它与纯 MediaPlayer的主要区别在于：

| 特性       | VideoView                              | MediaPlayer                    |
| ---------- | -------------------------------------- | ------------------------------ |
| 封装层级   | 高级视图组件（继承自 SurfaceView）     | 低级媒体引擎                   |
| 开发复杂度 | 低（自动处理视频显示与同步）           | 高（需手动处理 SurfaceView等） |
| UI控制     | 内置 MediaController，提供标准控制界面 | 无内置控制器，需完全自定义     |
| 灵活性     | 较低，功能相对固定                     | 高，可实现高度定制化功能       |





## 二、VideoView 的使用方法

### 2.1 常用 API

| 类别       | 方法                                | 说明                     |
| ---------- | ----------------------------------- | ------------------------ |
| 基本控制   | start()                             | 开始或继续播放           |
|            | pause()                             | 暂停播放                 |
|            | stopPlayback()                      | 停止播放并释放资源       |
|            | suspend()                           | 暂停播放并释放播放器资源 |
|            | resume()                            | 重新开始播放             |
| 状态控制   | isPlaying()                         | 是否正在播放             |
|            | canPause()                          | 是否可以暂停             |
|            | canSeekForward()                    | 是否可以快进             |
|            | canSeekBackward()                   | 是否可以快退             |
| 进度控制   | getCurrentPosition()                | 获取当前播放位置(ms)     |
|            | getDuration()                       | 获取视频总时长(ms)       |
|            | seekTo(int msec)                    | 跳转到指定位置           |
| 数据源设置 | setVideoPath(String path)           | 设置本地文件路径         |
|            | setVideoURI(Uri uri)                | 设置URI(支持本地/网络)   |
| UI控制     | setMediaController(MediaController) | 设置媒体控制器           |
|            | getMediaController()                | 获取媒体控制器           |
|            | requestFocus()                      | 请求焦点                 |
| 尺寸控制   | onMeasure()                         | 测量视图尺寸             |
|            | setVideoScaleType(int)              | 设置视频缩放类型         |
| 状态监听   | setOnPreparedListener()             | 准备完成监听             |
|            | setOnCompletionListener()           | 播放完成监听             |
|            | setOnErrorListener()                | 错误监听                 |
|            | setOnInfoListener()                 | 信息监听                 |





### 2.2 工作流程

1. **添加布局**：在 XML 布局文件中添加 `VideoView`组件

   ```xml
   <VideoView
       android:id="@+id/video_view"
       android:layout_width="match_parent"
       android:layout_height="match_parent" />
   ```

2. **设置视频源并播放**：在 Activity 或 Fragment 中，找到该视图并设置视频源，可以是本地文件路径或网络 URL

   ```java
   VideoView videoView = findViewById(R.id.video_view);
   
   // 播放本地视频
   videoView.setVideoPath("/sdcard/Movies/sample.mp4");
   // 或者播放网络视频
   // videoView.setVideoURI(Uri.parse("https://example.com/sample.mp4"));
   
   videoView.start(); // 开始播放
   ```

3. **添加控制栏**：通过 `MediaController`可以为 `VideoView`添加一个标准的播放控制界面（包含播放/暂停、进度条、快进/快退等）

   ```java
   MediaController mediaController = new MediaController(this);
   videoView.setMediaController(mediaController);
   ```

   





## 三、最佳实践

**权限问题**：播放本地存储（如 SD 卡）中的视频需要申请 `READ_EXTERNAL_STORAGE`权限；播放网络视频则需要申请 `INTERNET`权限。

**生命周期管理**：`VideoView`不会在应用进入后台时自动保存播放状态（如播放位置、暂停/播放状态）。开发者需要主动在 `onSaveInstanceState`和 `onRestoreInstanceState`等方法中处理状态的保存与恢复。

**资源释放**：在 Activity 或 Fragment 销毁时（如在 `onDestroy`方法中），应调用 `videoView.suspend()`或确保其被正确销毁，以释放 MediaPlayer 占用的资源







## 参考资料

[VideoView  | API reference  | Android Developers](https://developer.android.com/reference/android/widget/VideoView#VideoView(android.content.Context))