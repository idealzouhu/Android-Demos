package com.zouhu.qrcode;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class MainActivity extends AppCompatActivity {

    private EditText editTextSessionId;
    private ImageButton buttonScanQrCode;
    // 用于启动扫描Activity的请求码，可自定义
    private static final int RC_SCAN = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        editTextSessionId = findViewById(R.id.editTextSessionId);
        buttonScanQrCode = findViewById(R.id.buttonScanQrCode);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        buttonScanQrCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 点击扫描按钮时，检查相机权限并启动扫描
                if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA)
                        == PackageManager.PERMISSION_GRANTED) {
                    launchQRScanner();
                } else {
                    // 如果没有权限，则向用户申请
                    ActivityCompat.requestPermissions(MainActivity.this,
                            new String[]{Manifest.permission.CAMERA},
                            RC_SCAN); // 可以使用相同的请求码，或在权限回调中处理
                }
            }
        });
    }

    // 处理权限申请结果
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == RC_SCAN) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                launchQRScanner();
            } else {
                Toast.makeText(this, "需要相机权限才能扫描二维码", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // 启动ZXing的扫描Activity
    private void launchQRScanner() {
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE); // 只识别QR码
        integrator.setPrompt("将二维码放入框内扫描"); // 设置提示语
        integrator.setCameraId(0); // 使用后置摄像头
        integrator.setBeepEnabled(false); // 扫描成功是否播放提示音
        integrator.setBarcodeImageEnabled(true); // 是否保存扫描的图片
        integrator.initiateScan(); // 启动扫描
    }

    // 接收扫描结果
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                // 用户取消了扫描
                Toast.makeText(this, "扫描已取消", Toast.LENGTH_SHORT).show();
            } else {
                // 扫描成功，将结果设置到EditText中
                String scannedSessionId = result.getContents();
                editTextSessionId.setText(scannedSessionId);
                // 这里可以进一步验证或处理获取到的sessionId
            }
        }
    }
}