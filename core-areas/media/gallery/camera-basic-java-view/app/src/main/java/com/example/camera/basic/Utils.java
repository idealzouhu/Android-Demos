package com.example.camera.basic;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.provider.Settings;

import androidx.core.content.FileProvider;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Utils {
    // 权限请求码
    public static final int REQUEST_CAMERA_PERMISSION = 100;
    public static final int REQUEST_STORAGE_PERMISSION = 101;

    // 活动请求码
    public static final int REQUEST_IMAGE_CAPTURE = 200;
    public static final int REQUEST_IMAGE_PICK = 201;


    /**
     * 创建一个临时的图片文件用于存储拍照结果
     * <p>
     *     1. 从 Android 6.0开始，需使用应用关联缓存目录存放当前应用缓存数据。即/sdcard/Android/data/<package name>/cache
     *     2. 从 Android 10开始，共有SD卡目录不允许直接访问，必须使用作用域存储。
     * </p>
     *
     * @param context 上下文，用于获取文件存储目录
     * @return 创建成功的临时文件对象
     * @throws IOException 创建文件时发生IO异常
     */
    public static File createImageFile(Context context) throws IOException {
        // 生成时间戳格式的文件名前缀
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";

        // 获取外部存储目录，如果外部存储不可用则使用内部存储
        File storageDir = context.getExternalFilesDir("Pictures");
        if (storageDir == null || (!storageDir.exists() && !storageDir.mkdirs())) {
            storageDir = context.getFilesDir();
        }

        // 在指定目录中创建临时文件
        try {
            return File.createTempFile(imageFileName, ".jpg", storageDir);
        } catch (IOException e) {
            throw new IOException("Failed to create image file in directory: " + storageDir.getAbsolutePath(), e);
        }
    }

    /**
     * 获取拍照Intent
     * <p>
     * 低于Android 7.0，直接使用 Uri.formFile() 方法将 File 对象转换成 Uri 对象。
     * 从 Android 7.0 开始，使用 FileProvider.getUriForFile(）方法将 File 对象转换成 Uri 对象。
     * </p>
     * <p>
     * 关键参数说明：
     * 1. MediaStore.EXTRA_OUTPUT：指示相机应用将拍摄的照片保存到指定的Uri位置
     * 2. Intent.FLAG_GRANT_WRITE_URI_PERMISSION：确保目标应用（相机）可以写入FileProvider提供的URI
     * </p>
     *
     * @param context 上下文对象
     * @param photoFile 照片文件对象
     * @return 拍照Intent对象
     */
    public static Intent getTakePictureIntent(Context context, File photoFile) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (takePictureIntent.resolveActivity(context.getPackageManager()) != null && photoFile != null) {
            Uri photoURI = FileProvider.getUriForFile(
                    context,
                    context.getPackageName() + ".fileprovider",
                    photoFile
            );
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
            takePictureIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        }

        return takePictureIntent;
    }

    // 获取选择图片Intent
    public static Intent getPickImageIntent() {
        Intent pickImageIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickImageIntent.setType("image/*");
        return pickImageIntent;
    }


    // 获取需要的存储权限（根据Android版本）
    public static String[] getStoragePermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            // Android 14+ 需要部分媒体访问权限
            return new String[]{
                    android.Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED
            };
        } else if (Build.VERSION.SDK_INT == Build.VERSION_CODES.TIRAMISU) {
            // Android 13+ 需要完整媒体访问权限
            return new String[]{
                    android.Manifest.permission.READ_MEDIA_IMAGES
            };
        } else {
            // Android 10-12 需要外部存储权限
            return new String[]{
                    android.Manifest.permission.READ_EXTERNAL_STORAGE
            };
        }
    }

    // 获取当前Android版本所需的存储权限字符串
    public static String getCurrentStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            return android.Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED;
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            return android.Manifest.permission.READ_MEDIA_IMAGES;
        } else {
            return android.Manifest.permission.READ_EXTERNAL_STORAGE;
        }
    }

    // 获取系统权限设置Intent
    public static Intent getAppSettingsIntent(Context context) {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", context.getPackageName(), null);
        intent.setData(uri);
        return intent;
    }
}
