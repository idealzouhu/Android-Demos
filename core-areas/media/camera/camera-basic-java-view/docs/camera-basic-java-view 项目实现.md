## 一、项目概述

### 1.1 核心实现思路

在使用Android系统相机和图库的基础上 ，本应用实现了拍照和图片选择功能，采用**运行时权限管理**和**作用域存储**方案，确保在不同Android版本上都能正常工作。核心思路包括：

1. **权限动态请求**：仅在用户操作相关功能时请求对应权限
2. **FileProvider文件共享**：使用安全的URI方式共享图片文件
3. **版本兼容适配**：自动适配不同Android版本的权限和存储策略



### 1.2 关键组件

- **MainActivity**：主界面，管理所有用户交互和业务流程
- **Utils工具类**：封装常用工具方法，提高代码复用性
- **FileProvider**：Android 7.0+ 的安全文件共享机制



### 1.3 项目结构

```
camera-basic-java-view/
├── 📱 app/
│   ├── src/main/
│   │   ├── java/com/example/camera/
│   │   │   ├── MainActivity.java                    # 主活动，包含所有业务逻辑
│   │   │   └── Utils.java                           # 工具类（可选）
│   │   ├── res/
│   │   │   ├── drawable/                            # 图片资源目录
│   │   │   ├── layout/
│   │   │   │   └── activity_main.xml                # 主界面布局文件
│   │   │   └── xml/
│   │   │       └── file_paths.xml                   # FileProvider配置文件
│   │   └── AndroidManifest.xml                      # 应用清单文件                            
│   └── build.gradle.kts                             # 模块级构建配置
├── 📄 README.md                                     # 本文件
└── 📄 settings.gradle.kts                           # 项目设置文件
```



## 二、功能模块详解

### 2.1 基本配置

#### 2.1.1 权限配置

调用相机所需权限：

```xml
<!-- 相机硬件特性声明 -->
<uses-feature
    android:name="android.hardware.camera"
    android:required="true" />
    
<!-- 相机运行时权限 -->
<uses-permission android:name="android.permission.CAMERA" />
```

应用能在不同Android版本上正常读取存储中的图片资源的所需权限：

```xml
<!-- Android 10-12: 传统存储权限 -->
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE"
    android:maxSdkVersion="32" />
    
<!-- Android 13+: 媒体图片完整访问权限 -->
<uses-permission android:name="android.permission.READ_MEDIA_IMAGES" />
    
<!-- Android 14+: 部分媒体访问权限（用户选择） -->
<uses-permission android:name="android.permission.READ_MEDIA_VISUAL_USER_SELECTED" />
```

**权限版本适配策略**：

- **Android 10-12**：使用 `READ_EXTERNAL_STORAGE`权限
- **Android 13**：使用 `READ_MEDIA_IMAGES`权限
- **Android 14+**：使用 `READ_MEDIA_VISUAL_USER_SELECTED`权限



#### 2.1.2 包可见性配置

从 Android 11 (API 30) 开始，Google 引入了包可见性限制，应用默认无法查询设备上其他应用的信息，必须明确声明需要查询哪些应用或组件。

```xml
<!-- 声明需要查询相机应用 -->
<queries>
    <intent>
        <action android:name="android.media.action.IMAGE_CAPTURE" />
    </intent>
</queries>
```





### 2.2 调用相机拍摄照片

#### 2.2.1 创建临时文件

创建一个临时的图片文件用于存储拍照结果。

```java
/**
 * 创建一个临时的图片文件用于存储拍照结果
 * 
 * 存储位置策略：
 * 1. Android 6.0+：使用应用关联缓存目录存放当前应用缓存数据
 *    - 外部存储：/storage/emulated/0/Android/data/<package_name>/cache/
 * 2. Android 10+：必须使用作用域存储，无法直接访问共享存储空间
 *    - 优先使用外部私有存储目录
 *    - 备用内部存储目录
 *
 * @param context 上下文，用于获取文件存储目录
 * @return 创建成功的临时文件对象
 * @throws IOException 创建文件时发生IO异常
 */
public static File createImageFile(Context context) throws IOException {
    // 生成时间戳格式的文件名前缀
    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
    String imageFileName = "JPEG_" + timeStamp + "_";

    // 优先使用外部存储目录
    File storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
    if (storageDir == null || (!storageDir.exists() && !storageDir.mkdirs())) {
        // 外部存储不可用时，使用内部存储
        storageDir = context.getFilesDir();
    }

    // 在指定目录中创建临时文件
    try {
        return File.createTempFile(imageFileName, ".jpg", storageDir);
    } catch (IOException e) {
        throw new IOException("创建图片文件失败，目录: " + storageDir.getAbsolutePath(), e);
    }
}
```

**存储位置说明**：

- 外部私有存储：`/storage/emulated/0/Android/data/<package_name>/files/Pictures/`
- 内部私有存储：`/data/data/<package_name>/files/Pictures/`
- 应用卸载时文件会自动删除，保护用户隐私





#### 2.2.2  获取拍照Intent

**URI生成策略**：

- 低于 Android 7.0：使用 `Uri.fromFile()`方法
- Android 7.0+：**使用 `FileProvider.getUriForFile()`方法**，选择性地将封装的 Uri 共享给外部，从而提高安全性

```java
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
```

其中， `MediaStore.EXTRA_OUTPUT` 指示相机应用将拍摄的照片保存到指定的Uri位置， `Intent.FLAG_GRANT_WRITE_URI_PERMISSION`  确保了目标应用（相机）可以写入由FileProvider提供的URI所指向的文件。





#### 2.2.3 执行 Intent并处理结果

在执行 Intent 后，使用 `BitmapFactory.decodeFile()` 方法将图片转换成 Bitmap 对象，最终将其显示到 `ImageView` 组件中。

> 注意：如果某些图片的像素很高，直接加载到内存中就有可能会导致程序崩溃。

```java
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
            Toast.makeText(this, "创建文件失败: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

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
            currentImageUri = Uri.fromFile(currentPhotoFile);

            // 显示图片
            Bitmap bitmap = BitmapFactory.decodeFile(currentImagePath);
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
```



#### 2.2.3 定义文件提供者 provider

从 Android 7.0 开始，**直接使用本地真实路径的 Uri 被认为不安全**，会抛出异常。`FileProvider` 是一种特殊的 ContentProvider，可以对数据进行保护，选择性地将封装的 Uri 共享给外部，从而提高应用安全性。

```xml
</application>
       <!-- 文件提供者 -->
        <provider
            android:name="androidx.core.content.FileProvider"
            android:authorities="${applicationId}.fileprovider"
            android:exported="false"
            android:grantUriPermissions="true">
            <meta-data
                android:name="android.support.FILE_PROVIDER_PATHS"
                android:resource="@xml/file_paths" />
        </provider>       
</application>
```

其中，**配置说明**：

- `android:name`：固定为 `androidx.core.content.FileProvider`
- `android:authorities`：必须与代码中 `FileProvider.getUriForFile()`的第二个参数一致
- `android:exported="false"`：不允许其他应用直接访问
- `android:grantUriPermissions="true"`：允许临时授予URI权限

**FileProvider 会将要共享的文件路径与 `file_paths.xml` 配置文件中定义的路径进行匹配**， 只有在配置文件中声明的路径才能生成有效的 content URI。如果找不到匹配项，就会抛出 IllegalArgumentException。生成的URI格式为：`content://${authorities}/${name}/${path}`



**file_paths.xml配置案例**：

```xml
<?xml version="1.0" encoding="utf-8"?>
<paths xmlns:android="http://schemas.android.com/apk/res/android">
    <!-- 外部私有存储目录 -->
    <external-files-path
        name="external_images"
        path="Pictures/" />
    <!-- 内部私有存储目录 -->
    <files-path
        name="internal_images"
        path="." />
</paths>
```



### 2.3 从相册中读取照片

#### 2.3.1 选择图片

```java
// 从相册选择图片
    private void selectImageFromGallery() {
        Intent pickImageIntent = Utils.getPickImageIntent();
        startActivityForResult(pickImageIntent, Utils.REQUEST_IMAGE_PICK);
    }
```



#### 2.3.2 显示选择的图片

```java
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
    
    // 处理相册选择结果
    private void handleGalleryResult(Intent data) {
        if (data != null && data.getData() != null) {
            currentImageUri = data.getData();

            try {
                // 获取图片
                InputStream inputStream = getContentResolver().openInputStream(currentImageUri);
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                ivPreview.setImageBitmap(bitmap);

                if (inputStream != null) {
                    inputStream.close();
                }

                // 更新图片信息
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
```



## 三、问题

无法使用 device explorer 查看 `/storage/emulated/0/Android/data/com.example.camera.basic/files/Pictures/JPEG_20251210_154910_6137018459019720966.jpg` 这个文件

- 内部存储： `/data/data/<package_name>/`
- 外部存储:    `/storage/emulated/0/Android/data/<package_name>/files/`



在Android设备上，应用私有目录（`/data/data/`或 `/storage/emulated/0/Android/data/`）默认对用户和其他应用是不可见的





注意内容 Uri 和 文件 Uri 的区别。

```
* 注意：
* 1. 对于内容Uri（如 content:// 开头的Uri），getPath()返回的是虚拟路径，无法直接通过
* BitmapFactory.decodeFile 访问。
* 2. 文件Uri (file://)直接指向文件，可以直接通过 BitmapFactory.decodeFile 访问。
* 3. 内容Uri 指向Content Provider管理的数据，需要通过 ContentResolver 获取真实路径。
* 相册图片通常使用内容 Uri，如 content://media/external/images/media/123。
```

