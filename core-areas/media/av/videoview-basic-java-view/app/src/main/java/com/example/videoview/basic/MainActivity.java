package com.example.videoview.basic;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.documentfile.provider.DocumentFile;

import java.util.Locale;

public class MainActivity extends AppCompatActivity
        implements VideoPlayerController.PlaybackListener {

    // UI组件
    private VideoView videoView;
    private ProgressBar loadingIndicator;
    private Button btnSelectLocal;
    private Button btnSelectNetwork;
    private Button btnPlayPause;
    private Button btnStop;
    private Button btnForward;
    private SeekBar seekBar;
    private TextView tvFileInfo;
    private TextView tvProgress;
    private TextView tvStatus;
    private TextView tvVideoDetails;

    // 视频播放器控制器
    private VideoPlayerController videoPlayerController;

    // 常量
    private static final int REQUEST_STORAGE_PERMISSION = 100;
    private static final int REQUEST_VIDEO_FILE = 101;
    private static final int REQUEST_INTERNET_PERMISSION = 102;

    // 示例网络视频URL
    private static final String SAMPLE_VIDEO_URL = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // 初始化UI组件
        initViews();

        // 初始化视频播放器控制器
        initVideoPlayerController();

        // 设置事件监听
        setupListeners();
    }

    private void initViews() {
        videoView = findViewById(R.id.video_view);
        loadingIndicator = findViewById(R.id.loading_indicator);
        btnSelectLocal = findViewById(R.id.btn_select_local);
        btnSelectNetwork = findViewById(R.id.btn_select_network);
        btnPlayPause = findViewById(R.id.btn_play_pause);
        btnStop = findViewById(R.id.btn_stop);
        btnForward = findViewById(R.id.btn_forward);
        seekBar = findViewById(R.id.seek_bar);
        tvFileInfo = findViewById(R.id.tv_file_info);
        tvProgress = findViewById(R.id.tv_progress);
        tvStatus = findViewById(R.id.tv_status);
        tvVideoDetails = findViewById(R.id.tv_video_details);

        // 初始化按钮状态
        updateControlButtons(false);
    }

    private void initVideoPlayerController() {
        videoPlayerController = new VideoPlayerController(videoView, loadingIndicator);
        videoPlayerController.setPlaybackListener(this);
    }

    private void setupListeners() {
        // 选择本地视频按钮
        btnSelectLocal.setOnClickListener(v -> selectLocalVideo());

        // 播放网络视频按钮
        btnSelectNetwork.setOnClickListener(v -> playNetworkVideo());

        // 播放/暂停按钮
        btnPlayPause.setOnClickListener(v -> togglePlayPause());

        // 停止按钮
        btnStop.setOnClickListener(v -> stopPlayback());

        // 前进10秒按钮
        btnForward.setOnClickListener(v -> fastForward());

        // 进度条
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser && videoPlayerController.isPrepared()) {
                    videoPlayerController.seekTo(progress);
                    updateCurrentTime(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // 开始拖动时暂停进度更新
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // 停止拖动后恢复进度更新
            }
        });
    }

    private void selectLocalVideo() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    REQUEST_STORAGE_PERMISSION);
            return;
        }

        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("video/*");
        startActivityForResult(intent, REQUEST_VIDEO_FILE);
    }

    private void playNetworkVideo() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.INTERNET},
                    REQUEST_INTERNET_PERMISSION);
            return;
        }

        loadVideoFromUri(Uri.parse(SAMPLE_VIDEO_URL), "网络视频示例");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_VIDEO_FILE && resultCode == RESULT_OK && data != null) {
            Uri videoUri = data.getData();
            if (videoUri != null) {
                String fileName = getFileNameFromUri(videoUri);
                loadVideoFromUri(videoUri, fileName);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_STORAGE_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                selectLocalVideo();
            } else {
                showToast("需要存储权限才能选择视频文件");
            }
        } else if (requestCode == REQUEST_INTERNET_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                playNetworkVideo();
            } else {
                showToast("需要网络权限才能播放网络视频");
            }
        }
    }

    private void loadVideoFromUri(Uri videoUri, String fileName) {
        videoPlayerController.loadVideo(this, videoUri, fileName);
        tvFileInfo.setText(fileName);
        tvVideoDetails.setText("加载中...");
        tvProgress.setText("00:00 / 00:00");
        seekBar.setProgress(0);
        tvStatus.setText("正在加载视频...");
    }

    private void togglePlayPause() {
        if (!videoPlayerController.isPrepared()) {
            showToast("视频尚未准备好");
            return;
        }

        if (!videoPlayerController.isPlaying()) {
            videoPlayerController.play();
        } else {
            videoPlayerController.pause();
        }
    }

    private void stopPlayback() {
        videoPlayerController.stop();
    }

    private void fastForward() {
        videoPlayerController.fastForward(10000);
    }

    private void updateCurrentTime(int milliseconds) {
        if (videoPlayerController.isPrepared()) {
            String currentTime = VideoPlayerController.formatTime(milliseconds);
            String totalTime = VideoPlayerController.formatTime(videoPlayerController.getDuration());
            tvProgress.setText(currentTime + " / " + totalTime);
        }
    }

    private void updateTotalDuration() {
        if (videoPlayerController.isPrepared()) {
            String totalTime = VideoPlayerController.formatTime(videoPlayerController.getDuration());
            tvProgress.setText("00:00 / " + totalTime);
        }
    }

    private void updateControlButtons(boolean enabled) {
        btnPlayPause.setEnabled(enabled);
        btnStop.setEnabled(enabled);
        btnForward.setEnabled(enabled);
        seekBar.setEnabled(enabled);
    }

    private String getFileNameFromUri(Uri uri) {
        String displayName = "未知文件";
        try {
            DocumentFile documentFile = DocumentFile.fromSingleUri(this, uri);
            if (documentFile != null && documentFile.getName() != null) {
                displayName = documentFile.getName();
            }
        } catch (Exception e) {
            String path = uri.getPath();
            if (path != null) {
                int lastSlash = path.lastIndexOf('/');
                if (lastSlash != -1 && lastSlash < path.length() - 1) {
                    displayName = path.substring(lastSlash + 1);
                }
            }
        }
        return displayName;
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    // ========== 播放器回调接口实现 ==========

    @Override
    public void onPrepared(int duration, int width, int height) {
        updateControlButtons(true);
        tvStatus.setText("视频准备完成");

        // 设置进度条
        seekBar.setMax(duration);
        updateTotalDuration();

        // 显示视频信息
        String videoInfo = String.format(
                Locale.getDefault(),
                "时长: %s | 分辨率: %d×%d",
                VideoPlayerController.formatTime(duration), width, height
        );
        tvVideoDetails.setText(videoInfo);

        showToast("视频准备完成");
    }

    @Override
    public void onPlaybackStarted() {
        runOnUiThread(() -> {
            btnPlayPause.setText("暂停");
            btnPlayPause.setCompoundDrawablesWithIntrinsicBounds(
                    android.R.drawable.ic_media_pause, 0, 0, 0
            );
            tvStatus.setText("正在播放...");
        });
    }

    @Override
    public void onPlaybackPaused() {
        runOnUiThread(() -> {
            btnPlayPause.setText("播放");
            btnPlayPause.setCompoundDrawablesWithIntrinsicBounds(
                    android.R.drawable.ic_media_play, 0, 0, 0
            );
            tvStatus.setText("已暂停");
        });
    }

    @Override
    public void onPlaybackStopped() {
        runOnUiThread(() -> {
            btnPlayPause.setText("播放");
            btnPlayPause.setCompoundDrawablesWithIntrinsicBounds(
                    android.R.drawable.ic_media_play, 0, 0, 0
            );
            updateControlButtons(false);
            tvStatus.setText("已停止");

            // 重置进度
            seekBar.setProgress(0);
            updateCurrentTime(0);
        });
    }

    @Override
    public void onPlaybackCompleted() {
        runOnUiThread(() -> {
            btnPlayPause.setText("播放");
            btnPlayPause.setCompoundDrawablesWithIntrinsicBounds(
                    android.R.drawable.ic_media_play, 0, 0, 0
            );
            tvStatus.setText("播放完成");

            // 重置进度
            seekBar.setProgress(0);
            updateCurrentTime(0);
        });
    }

    @Override
    public void onBufferingStart() {
        runOnUiThread(() -> {
            tvStatus.setText("正在缓冲...");
        });
    }

    @Override
    public void onBufferingEnd() {
        runOnUiThread(() -> {
            tvStatus.setText("缓冲完成");
        });
    }

    @Override
    public void onRenderingStart() {
        runOnUiThread(() -> {
            tvStatus.setText("开始渲染视频");
        });
    }

    @Override
    public void onError(String errorMessage) {
        runOnUiThread(() -> {
            tvStatus.setText("播放出错: " + errorMessage);
            updateControlButtons(false);
        });
    }

    @Override
    public void onProgressUpdated(int currentPosition, int duration) {
        runOnUiThread(() -> {
            // 更新进度条
            seekBar.setProgress(currentPosition);

            // 更新时间显示
            updateCurrentTime(currentPosition);
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        videoPlayerController.pause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        videoPlayerController.release();
    }
}