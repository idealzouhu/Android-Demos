package com.example.mediaplayer.localaudio;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import java.io.IOException;
import java.util.Locale;

/**
 * 封装的音频播放器类
 * 负责处理MediaPlayer的所有操作
 */
public class AudioPlayer {

    // 播放状态回调接口
    public interface PlaybackListener {
        void onPrepared();
        void onPlaybackStarted();
        void onPlaybackPaused();
        void onPlaybackStopped();
        void onPlaybackCompleted();
        void onError(String errorMessage);
        void onProgressUpdated(int currentPosition, int duration);
    }

    // 成员变量
    private MediaPlayer mediaPlayer;
    private Handler progressHandler;
    private PlaybackListener playbackListener;
    private boolean isPrepared = false;
    private boolean isPlaying = false;
    private Uri currentAudioUri;

    // 进度更新Runnable
    private final Runnable updateProgressRunnable = new Runnable() {
        @Override
        public void run() {
            if (mediaPlayer != null && isPlaying) {
                updateProgress();

                // 将updateProgressRunnable任务添加到Handler中，实现周期性地调用run方法
                progressHandler.postDelayed(this, 1000);
            }
        }
    };

    /**
     * 构造函数
     */
    public AudioPlayer() {
        progressHandler = new Handler();
    }

    /**
     * 设置播放状态回调
     */
    public void setPlaybackListener(PlaybackListener listener) {
        this.playbackListener = listener;
    }

    /**
     * 初始化MediaPlayer
     */
    public void initialize(Context context) {
        mediaPlayer = new MediaPlayer();

        // 设置监听器
        mediaPlayer.setOnPreparedListener(mp -> {
            isPrepared = true;
            if (playbackListener != null) {
                playbackListener.onPrepared();
            }
        });

        mediaPlayer.setOnCompletionListener(mp -> {
            isPlaying = false;
            if (playbackListener != null) {
                playbackListener.onPlaybackCompleted();
            }
            stopProgressUpdates();
        });

        mediaPlayer.setOnErrorListener((mp, what, extra) -> {
            String errorMsg = getErrorDescription(what);
            isPlaying = false;
            isPrepared = false;

            if (playbackListener != null) {
                playbackListener.onError(errorMsg);
            }
            stopProgressUpdates();
            return true;
        });

        // 默认音量
        setVolume(0.5f);
    }

    /**
     * 加载音频文件
     */
    public void loadAudio(Context context, Uri audioUri) throws IOException {
        if (mediaPlayer == null) {
            initialize(context);
        }

        // 重置播放器
        reset();

        // 设置新的音频源
        currentAudioUri = audioUri;
        mediaPlayer.setDataSource(context, audioUri);
        mediaPlayer.prepareAsync();
    }

    /**
     * 开始播放
     */
    public void play() {
        if (mediaPlayer != null && isPrepared && !isPlaying) {
            mediaPlayer.start();
            isPlaying = true;

            if (playbackListener != null) {
                playbackListener.onPlaybackStarted();
            }

            startProgressUpdates();
        }
    }

    /**
     * 暂停播放
     */
    public void pause() {
        if (mediaPlayer != null && isPlaying) {
            mediaPlayer.pause();
            isPlaying = false;

            if (playbackListener != null) {
                playbackListener.onPlaybackPaused();
            }

            stopProgressUpdates();
        }
    }

    /**
     * 停止播放
     */
    public void stop() {
        if (mediaPlayer != null) {
            mediaPlayer.pause();
            mediaPlayer.seekTo(0);
            isPlaying = false;

            if (playbackListener != null) {
                playbackListener.onPlaybackStopped();
            }

            stopProgressUpdates();
        }
    }

    /**
     * 跳转到指定位置
     */
    public void seekTo(int position) {
        if (mediaPlayer != null && isPrepared) {
            mediaPlayer.seekTo(position);
        }
    }

    /**
     * 设置音量
     */
    public void setVolume(float volume) {
        if (mediaPlayer != null) {
            mediaPlayer.setVolume(volume, volume);
        }
    }

    /**
     * 获取当前播放位置
     */
    public int getCurrentPosition() {
        if (mediaPlayer != null && isPrepared) {
            return mediaPlayer.getCurrentPosition();
        }
        return 0;
    }

    /**
     * 获取音频总时长
     */
    public int getDuration() {
        if (mediaPlayer != null && isPrepared) {
            return mediaPlayer.getDuration();
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
     * 获取当前音频URI
     */
    public Uri getCurrentAudioUri() {
        return currentAudioUri;
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
        if (mediaPlayer != null) {
            mediaPlayer.reset();
            isPrepared = false;
            isPlaying = false;
            stopProgressUpdates();
        }
    }

    /**
     * 释放资源
     */
    public void release() {
        if (mediaPlayer != null) {
            if (isPlaying) {
                mediaPlayer.stop();
            }
            mediaPlayer.release();
            mediaPlayer = null;
        }
        progressHandler.removeCallbacks(updateProgressRunnable);
        isPrepared = false;
        isPlaying = false;
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
        if (mediaPlayer != null && isPlaying && playbackListener != null) {
            int currentPosition = mediaPlayer.getCurrentPosition();
            int duration = mediaPlayer.getDuration();
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