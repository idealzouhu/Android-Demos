package com.example.mediaplayer.localaudio;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.documentfile.provider.DocumentFile;
import java.io.IOException;

public class MainActivity extends AppCompatActivity
        implements AudioPlayer.PlaybackListener {

    // UI组件
    private Button btnSelectFile;
    private Button btnPlayPause;
    private Button btnStop;
    private SeekBar seekBar;
    private SeekBar volumeSeekBar;
    private TextView tvFileInfo;
    private TextView tvProgress;
    private TextView tvStatus;
    private TextView tvFileDetails;

    // 音频播放器
    private AudioPlayer audioPlayer;

    // 权限请求码
    private static final int REQUEST_STORAGE_PERMISSION = 100;
    private static final int REQUEST_AUDIO_FILE = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 初始化UI
        initViews();

        // 初始化音频播放器
        initAudioPlayer();

        // 请求存储权限
        requestStoragePermission();

        // 设置事件监听
        setupListeners();
    }

    private void initViews() {
        btnSelectFile = findViewById(R.id.btn_select_file);
        btnPlayPause = findViewById(R.id.btn_play_pause);
        btnStop = findViewById(R.id.btn_stop);
        seekBar = findViewById(R.id.seek_bar);
        volumeSeekBar = findViewById(R.id.seek_bar_volume);
        tvFileInfo = findViewById(R.id.tv_file_info);
        tvProgress = findViewById(R.id.tv_progress);
        tvStatus = findViewById(R.id.tv_status);
        tvFileDetails = findViewById(R.id.tv_file_details);

        // 初始化按钮状态
        btnPlayPause.setEnabled(false);
        btnStop.setEnabled(false);
    }

    private void initAudioPlayer() {
        audioPlayer = new AudioPlayer();
        audioPlayer.setPlaybackListener(this);
        audioPlayer.initialize(this);
    }

    private void requestStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (!Environment.isExternalStorageManager()) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                intent.setData(Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, REQUEST_STORAGE_PERMISSION);
            }
        } else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        REQUEST_STORAGE_PERMISSION);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_STORAGE_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                showToast("存储权限已授权");
            } else {
                showToast("需要存储权限来选择音频文件");
            }
        }
    }

    private void setupListeners() {
        // 选择文件按钮
        btnSelectFile.setOnClickListener(v -> openFilePicker());

        // 播放/暂停按钮
        btnPlayPause.setOnClickListener(v -> togglePlayPause());

        // 停止按钮
        btnStop.setOnClickListener(v -> stopPlayback());

        // 进度条
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser && audioPlayer.isPrepared()) {
                    audioPlayer.seekTo(progress);
                    updateCurrentTime(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // 开始拖动时，进度回调会暂停
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // 停止拖动后，进度回调会自动恢复
            }
        });

        // 音量控制
        volumeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                float volume = progress / 100f;
                audioPlayer.setVolume(volume);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }

    private void openFilePicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("audio/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        try {
            startActivityForResult(
                    Intent.createChooser(intent, "选择音频文件"),
                    REQUEST_AUDIO_FILE
            );
        } catch (Exception e) {
            showToast("无法打开文件选择器: " + e.getMessage());
        }
    }

    /**
     * 处理文件选择结果
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_AUDIO_FILE && resultCode == RESULT_OK && data != null) {
            Uri uri = data.getData();
            if (uri != null) {
                loadAudioFile(uri);
            }
        }
    }

    private void loadAudioFile(Uri uri) {
        try {
            audioPlayer.loadAudio(this, uri);
            String fileName = getFileNameFromUri(uri);
            tvFileInfo.setText(fileName);
            tvStatus.setText("正在加载音频文件...");
            tvFileDetails.setText("加载中...");
            tvProgress.setText("00:00 / 00:00");
            seekBar.setProgress(0);

        } catch (IOException e) {
            e.printStackTrace();
            tvStatus.setText("加载失败: " + e.getMessage());
        }
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

    private void togglePlayPause() {
        if (!audioPlayer.isPrepared()) {
            showToast("音频尚未准备好");
            return;
        }

        if (!audioPlayer.isPlaying()) {
            audioPlayer.play();
        } else {
            audioPlayer.pause();
        }
    }

    private void stopPlayback() {
        audioPlayer.stop();
    }

    private void updateCurrentTime(int milliseconds) {
        if (audioPlayer.isPrepared()) {
            String currentTime = AudioPlayer.formatTime(milliseconds);
            String totalTime = AudioPlayer.formatTime(audioPlayer.getDuration());
            tvProgress.setText(currentTime + " / " + totalTime);
        }
    }

    private void updateTotalDuration() {
        if (audioPlayer.isPrepared()) {
            String totalTime = AudioPlayer.formatTime(audioPlayer.getDuration());
            tvProgress.setText("00:00 / " + totalTime);
        }
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    // ========== 播放器回调接口实现 ==========

    @Override
    public void onPrepared() {
        runOnUiThread(() -> {
            btnPlayPause.setEnabled(true);
            btnPlayPause.setText("播放");
            btnPlayPause.setCompoundDrawablesWithIntrinsicBounds(
                    android.R.drawable.ic_media_play, 0, 0, 0
            );
            tvStatus.setText("文件加载完成，可以播放");

            // 设置进度条最大值
            seekBar.setMax(audioPlayer.getDuration());

            // 更新总时长显示
            updateTotalDuration();

            // 显示文件详细信息
            String durationStr = AudioPlayer.formatTime(audioPlayer.getDuration());
            tvFileDetails.setText("时长: " + durationStr);
        });
    }

    @Override
    public void onPlaybackStarted() {
        runOnUiThread(() -> {
            btnPlayPause.setText("暂停");
            btnPlayPause.setCompoundDrawablesWithIntrinsicBounds(
                    android.R.drawable.ic_media_pause, 0, 0, 0
            );
            btnStop.setEnabled(true);
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
            btnStop.setEnabled(false);
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
            btnStop.setEnabled(false);
            tvStatus.setText("播放完成");

            // 重置进度
            seekBar.setProgress(0);
            updateCurrentTime(0);
        });
    }

    @Override
    public void onError(String errorMessage) {
        runOnUiThread(() -> {
            tvStatus.setText("播放出错: " + errorMessage);
            btnPlayPause.setEnabled(false);
            btnStop.setEnabled(false);
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
    protected void onDestroy() {
        super.onDestroy();
        if (audioPlayer != null) {
            audioPlayer.release();
        }
    }
}