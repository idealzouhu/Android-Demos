package com.example.videoview.basic;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.VideoView;
import androidx.annotation.NonNull;
import java.util.Locale;
import java.util.Map;

/**
 * 封装的视频播放器控制器
 * 封装VideoView的复杂操作
 */
public class VideoPlayerController {

    // 播放状态回调接口
    public interface PlaybackListener {
        void onPrepared(int duration, int width, int height);
        void onPlaybackStarted();
        void onPlaybackPaused();
        void onPlaybackStopped();
        void onPlaybackCompleted();
        void onBufferingStart();
        void onBufferingEnd();
        void onRenderingStart();
        void onError(String errorMessage);
        void onProgressUpdated(int currentPosition, int duration);
    }

    // 成员变量
    private VideoView videoView;
    private ProgressBar loadingIndicator;
    private Handler progressHandler;
    private PlaybackListener playbackListener;
    private boolean isPrepared = false;
    private boolean isPlaying = false;
    private Uri currentVideoUri;
    private String currentFileName = "";

    // 进度更新Runnable
    private final Runnable updateProgressRunnable = new Runnable() {
        @Override
        public void run() {
            if (videoView != null && videoView.isPlaying()) {
                updateProgress();
                progressHandler.postDelayed(this, 1000);
            }
        }
    };

    /**
     * 构造函数
     */
    public VideoPlayerController(@NonNull VideoView videoView, @NonNull ProgressBar loadingIndicator) {
        this.videoView = videoView;
        this.loadingIndicator = loadingIndicator;
        this.progressHandler = new Handler(Looper.getMainLooper());

        // 初始化监听器
        setupListeners();
    }

    /**
     * 设置播放状态回调
     */
    public void setPlaybackListener(PlaybackListener listener) {
        this.playbackListener = listener;
    }

    /**
     * 初始化监听器
     */
    private void setupListeners() {
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                isPrepared = true;
                setLoading(false);

                int duration = videoView.getDuration();
                int width = mp.getVideoWidth();
                int height = mp.getVideoHeight();

                if (playbackListener != null) {
                    playbackListener.onPrepared(duration, width, height);
                }
            }
        });

        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                isPlaying = false;
                stopProgressUpdates();
                if (playbackListener != null) {
                    playbackListener.onPlaybackCompleted();
                }
            }
        });

        videoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                setLoading(false);
                isPrepared = false;
                isPlaying = false;
                stopProgressUpdates();

                if (playbackListener != null) {
                    playbackListener.onError(getErrorDescription(what));
                }
                return true;
            }
        });

        videoView.setOnInfoListener(new MediaPlayer.OnInfoListener() {
            @Override
            public boolean onInfo(MediaPlayer mp, int what, int extra) {
                if (playbackListener != null) {
                    switch (what) {
                        case MediaPlayer.MEDIA_INFO_BUFFERING_START:
                            setLoading(true);
                            playbackListener.onBufferingStart();
                            return true;
                        case MediaPlayer.MEDIA_INFO_BUFFERING_END:
                            setLoading(false);
                            playbackListener.onBufferingEnd();
                            return true;
                        case MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START:
                            playbackListener.onRenderingStart();
                            return true;
                    }
                }
                return false;
            }
        });
    }

    /**
     * 加载视频
     */
    public void loadVideo(Context context, Uri videoUri, String fileName) {
        loadVideo(context, videoUri, fileName, null);
    }

    /**
     * 加载视频（带请求头）
     */
    public void loadVideo(Context context, Uri videoUri, String fileName, Map<String, String> headers) {
        try {
            // 重置状态
            reset();

            // 保存信息
            currentVideoUri = videoUri;
            currentFileName = fileName != null ? fileName : "未知文件";

            // 显示加载状态
            setLoading(true);

            // 设置视频源，VideoView 组件内部会自动处理网络连接和视频流的下载
            if (headers != null && !headers.isEmpty()) {
                videoView.setVideoURI(videoUri, headers);
            } else {
                videoView.setVideoURI(videoUri);
            }

            // 开始准备
            videoView.start();
            videoView.pause(); // 立即暂停，等待用户点击播放

        } catch (Exception e) {
            setLoading(false);
            if (playbackListener != null) {
                playbackListener.onError("加载失败: " + e.getMessage());
            }
        }
    }

    /**
     * 开始播放
     */
    public void play() {
        if (videoView != null && isPrepared && !isPlaying) {
            videoView.start();
            isPlaying = true;
            startProgressUpdates();

            if (playbackListener != null) {
                playbackListener.onPlaybackStarted();
            }
        }
    }

    /**
     * 暂停播放
     */
    public void pause() {
        if (videoView != null && isPlaying) {
            videoView.pause();
            isPlaying = false;
            stopProgressUpdates();

            if (playbackListener != null) {
                playbackListener.onPlaybackPaused();
            }
        }
    }

    /**
     * 停止播放
     */
    public void stop() {
        if (videoView != null) {
            videoView.stopPlayback();
            isPlaying = false;
            isPrepared = false;
            stopProgressUpdates();

            if (playbackListener != null) {
                playbackListener.onPlaybackStopped();
            }
        }
    }

    /**
     * 跳转到指定位置
     */
    public void seekTo(int position) {
        if (videoView != null && isPrepared) {
            videoView.seekTo(position);
        }
    }

    /**
     * 前进指定毫秒
     */
    public void fastForward(int milliseconds) {
        if (videoView != null && isPrepared) {
            int currentPosition = videoView.getCurrentPosition();
            int duration = videoView.getDuration();
            int newPosition = Math.min(currentPosition + milliseconds, duration);
            videoView.seekTo(newPosition);
        }
    }

    /**
     * 后退指定毫秒
     */
    public void rewind(int milliseconds) {
        if (videoView != null && isPrepared) {
            int currentPosition = videoView.getCurrentPosition();
            int newPosition = Math.max(currentPosition - milliseconds, 0);
            videoView.seekTo(newPosition);
        }
    }

    /**
     * 获取当前播放位置
     */
    public int getCurrentPosition() {
        if (videoView != null && isPrepared) {
            return videoView.getCurrentPosition();
        }
        return 0;
    }

    /**
     * 获取视频总时长
     */
    public int getDuration() {
        if (videoView != null && isPrepared) {
            return videoView.getDuration();
        }
        return 0;
    }

    /**
     * 判断是否正在播放
     */
    public boolean isPlaying() {
        return isPlaying;
    }

    /**
     * 判断是否已准备就绪
     */
    public boolean isPrepared() {
        return isPrepared;
    }

    /**
     * 获取当前视频URI
     */
    public Uri getCurrentVideoUri() {
        return currentVideoUri;
    }

    /**
     * 获取当前文件名
     */
    public String getCurrentFileName() {
        return currentFileName;
    }

    /**
     * 获取缓冲百分比
     */
    public int getBufferPercentage() {
        if (videoView != null) {
            return videoView.getBufferPercentage();
        }
        return 0;
    }

    /**
     * 格式化时间（毫秒 -> MM:SS）
     */
    public static String formatTime(int milliseconds) {
        int seconds = milliseconds / 1000;
        int minutes = seconds / 60;
        seconds = seconds % 60;
        return String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
    }

    /**
     * 重置播放器
     */
    public void reset() {
        if (videoView != null) {
            videoView.stopPlayback();
            isPrepared = false;
            isPlaying = false;
            stopProgressUpdates();
        }
    }

    /**
     * 释放资源
     */
    public void release() {
        if (videoView != null) {
            videoView.stopPlayback();
            videoView = null;
        }
        stopProgressUpdates();
        isPrepared = false;
        isPlaying = false;
    }

    /**
     * 暂停播放（用于Activity生命周期）
     */
    public void suspend() {
        if (videoView != null) {
            videoView.suspend();
        }
    }

    /**
     * 恢复播放（用于Activity生命周期）
     */
    public void resume() {
        if (videoView != null) {
            videoView.resume();
        }
    }

    /**
     * 设置加载状态
     */
    private void setLoading(boolean loading) {
        if (loadingIndicator != null) {
            loadingIndicator.setVisibility(loading ? View.VISIBLE : View.GONE);
        }
    }

    /**
     * 开始更新进度
     */
    private void startProgressUpdates() {
        progressHandler.post(updateProgressRunnable);
    }

    /**
     * 停止更新进度
     */
    private void stopProgressUpdates() {
        progressHandler.removeCallbacks(updateProgressRunnable);
    }

    /**
     * 更新进度
     */
    private void updateProgress() {
        if (videoView != null && isPlaying && playbackListener != null) {
            int currentPosition = videoView.getCurrentPosition();
            int duration = videoView.getDuration();
            playbackListener.onProgressUpdated(currentPosition, duration);
        }
    }

    /**
     * 获取错误描述
     */
    private String getErrorDescription(int what) {
        switch (what) {
            case MediaPlayer.MEDIA_ERROR_UNKNOWN:
                return "未知错误";
            case MediaPlayer.MEDIA_ERROR_SERVER_DIED:
                return "服务器挂掉";
            case MediaPlayer.MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK:
                return "不支持渐进式播放";
            case MediaPlayer.MEDIA_ERROR_IO:
                return "IO错误";
            case MediaPlayer.MEDIA_ERROR_MALFORMED:
                return "格式错误";
            case MediaPlayer.MEDIA_ERROR_UNSUPPORTED:
                return "不支持";
            case MediaPlayer.MEDIA_ERROR_TIMED_OUT:
                return "超时";
            default:
                return "错误码: " + what;
        }
    }
}