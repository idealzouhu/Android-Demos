package com.example.camera.basic;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
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

import java.io.File;
import java.io.InputStream;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private ImageView ivPreview;
    private TextView tvImageInfo;
    private Button btnTakePhoto;
    private Button btnSelectImage;
    private Button btnClear;

    private File currentPhotoFile;
    private Uri currentImageUri;
    private String currentImagePath;


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

        initViews();
        setupListeners();
    }

    private void initViews() {
        ivPreview = findViewById(R.id.iv_preview);
        tvImageInfo = findViewById(R.id.tv_image_info);
        btnTakePhoto = findViewById(R.id.btn_take_photo);
        btnSelectImage = findViewById(R.id.btn_select_image);
        btnClear = findViewById(R.id.btn_clear);
    }

    private void setupListeners() {
        // 拍照按钮点击事件
        btnTakePhoto.setOnClickListener(v -> {
            if (checkCameraPermission()) {
                takePhoto();
            } else {
                requestCameraPermission();
            }
        });

        // 选择图片按钮点击事件
        btnSelectImage.setOnClickListener(v -> {
            if (checkStoragePermission()) {
                selectImageFromGallery();
            } else {
                requestStoragePermission();
            }
        });

        // 清除按钮点击事件
        btnClear.setOnClickListener(v -> clearImage());
    }

    // ============ 拍照功能 ============
    // 检查相机权限
    private boolean checkCameraPermission() {
        return ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
    }

    // 请求相机权限
    private void requestCameraPermission() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.CAMERA},
                Utils.REQUEST_CAMERA_PERMISSION);
    }

    // 拍照
    private void takePhoto() {
        try {
            currentPhotoFile = Utils.createImageFile(this);
            Intent takePictureIntent = Utils.getTakePictureIntent(this, currentPhotoFile);
            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                // 启动相机应用
                startActivityForResult(takePictureIntent, Utils.REQUEST_IMAGE_CAPTURE);
            } else {
                Toast.makeText(this, "无法启动相机应用", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.e("MainActivity", "创建文件失败: " + e.getMessage());
        }
    }

    // ============ 选择图片功能 ============
    // 检查存储权限
    private boolean checkStoragePermission() {
        String[] permissions = Utils.getStoragePermissions();

        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission)
                    != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    // 请求存储权限
    private void requestStoragePermission() {
        String[] permissions = Utils.getStoragePermissions();
        ActivityCompat.requestPermissions(this,
                permissions,
                Utils.REQUEST_STORAGE_PERMISSION);
    }

    // 从相册选择图片
    private void selectImageFromGallery() {
        Intent pickImageIntent = Utils.getPickImageIntent();
        startActivityForResult(pickImageIntent, Utils.REQUEST_IMAGE_PICK);
    }

    // 清除图片
    private void clearImage() {
        ivPreview.setImageResource(android.R.drawable.ic_menu_camera);
        tvImageInfo.setText("未选择图片");
        currentImageUri = null;
        currentImagePath = null;
        currentPhotoFile = null;
        Toast.makeText(this, "已清除图片", Toast.LENGTH_SHORT).show();
    }

    // ============ 权限请求结果处理 ============
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            switch (requestCode) {
                case Utils.REQUEST_CAMERA_PERMISSION:
                    takePhoto();
                    break;
                case Utils.REQUEST_STORAGE_PERMISSION:
                    selectImageFromGallery();
                    break;
            }
        } else {
            Toast.makeText(this, "需要权限才能继续操作", Toast.LENGTH_SHORT).show();
        }
    }

    // ============ 活动结果处理 ============
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case Utils.REQUEST_IMAGE_CAPTURE:
                    handleCameraResult();
                    break;
                case Utils.REQUEST_IMAGE_PICK:
                    handleGalleryResult(data);
                    break;
            }
        }
    }

    // 处理拍照结果
    private void handleCameraResult() {
        if (currentPhotoFile != null && currentPhotoFile.exists()) {
            // 获取图片路径
            currentImagePath = currentPhotoFile.getAbsolutePath();
            // currentImageUri = Uri.fromFile(currentPhotoFile);

            // 显示图片
            // Bitmap bitmap = BitmapFactory.decodeFile(currentImagePath);
            Bitmap bitmap = decodeSampledBitmapFromFile(currentImagePath, ivPreview.getWidth(), ivPreview.getHeight());
            ivPreview.setImageBitmap(bitmap);

            // 更新图片信息
            String info = String.format(Locale.getDefault(), "拍摄时间: %s\n尺寸: %dx%d",
                    new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                            .format(new java.util.Date(currentPhotoFile.lastModified())),
                    bitmap.getWidth(),
                    bitmap.getHeight());
            tvImageInfo.setText(info);

            Toast.makeText(this, "照片已保存", Toast.LENGTH_SHORT).show();
        }
    }


    /**
     * 处理相册选择结果
     *
     * @param data 相册选择结果
     */
    private void handleGalleryResult(Intent data) {
        if (data != null && data.getData() != null) {
            currentImageUri = data.getData();

            try {
                // 获取图片
                // InputStream inputStream = getContentResolver().openInputStream(currentImageUri);
                // Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                // ivPreview.setImageBitmap(bitmap);
                // if (inputStream != null) {
                //     inputStream.close();
                // }
                Bitmap bitmap = decodeSampledBitmapFromUri(currentImageUri, ivPreview.getWidth(), ivPreview.getHeight());
                ivPreview.setImageBitmap(bitmap);

                // 更新图片信息
                assert bitmap != null;
                String info = String.format(Locale.getDefault(), "来自相册\n尺寸: %dx%d",
                        bitmap.getWidth(),
                        bitmap.getHeight());
                tvImageInfo.setText(info);

                Toast.makeText(this, "图片已选择", Toast.LENGTH_SHORT).show();

            } catch (Exception e) {
                Log.e("MainActivity", "加载图片失败: " + e.getMessage());
                Toast.makeText(this, "加载图片失败: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }


    // ============ 辅助方法 ============
    /**
     * 加载图片时进行采样压缩，避免内存溢出
     *
     * @param imagePath 图片文件路径
     * @param reqWidth 期望宽度
     * @param reqHeight 期望高度
     * @return 压缩后的Bitmap对象
     */
    private Bitmap decodeSampledBitmapFromFile(String imagePath, int reqWidth, int reqHeight) {
        // 第一次解析将inJustDecodeBounds设置为true，获取原始图片尺寸
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imagePath, options);

        // 计算inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // 使用计算得到的inSampleSize再次解析图片
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(imagePath, options);
    }

    /**
     * 计加载图片时进行采样压缩，避免内存溢出
     *
     * <p>
     * 注意：
     * 1. 对于内容Uri（如 content:// 开头的Uri），getPath()返回的是虚拟路径，无法直接通过
     * BitmapFactory.decodeFile 访问。
     * 2. 文件Uri (file://)直接指向文件，可以直接通过 BitmapFactory.decodeFile 访问。
     * 3. 内容Uri 指向Content Provider管理的数据，需要通过 ContentResolver 获取真实路径。
     * 相册图片通常使用内容 Uri，如 content://media/external/images/media/123。
     * </p>
     *
     * @param uri 图片uri
     * @param reqWidth 期望宽度
     * @param reqHeight 期望高度
     * @return 采样率
     */
    private Bitmap decodeSampledBitmapFromUri(Uri uri, int reqWidth, int reqHeight) {
        try {
            // 获取图片尺寸
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;

            InputStream inputStream = getContentResolver().openInputStream(uri);
            BitmapFactory.decodeStream(inputStream, null, options);
            if (inputStream != null) {
                inputStream.close();
            }

            // 计算采样率
            options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
            options.inJustDecodeBounds = false;

            // 加载压缩后的图片
            inputStream = getContentResolver().openInputStream(uri);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream, null, options);
            if (inputStream != null) {
                inputStream.close();
            }

            return bitmap;
        } catch (Exception e) {
            Log.e("MainActivity", "加载图片失败: " + e.getMessage());
            return null;
        }
    }

    /**
     * 计算图片采样率
     *
     * @param options BitmapFactory.Options对象
     * @param reqWidth 期望宽度
     * @param reqHeight 期望高度
     * @return 采样率
     */
    private int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // 原始图片的宽高
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // 在保证解析出的bitmap宽高分别大于目标尺寸宽高的前提下，取可能的inSampleSize的最大值
            while ((halfHeight / inSampleSize) >= reqHeight && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

}