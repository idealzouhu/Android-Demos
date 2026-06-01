## 一、Android 权限机制

### 1.1 什么是 Android 权限机制

Android 权限机制是 Android 系统安全模型的核心组成部分，旨在保护用户隐私和设备安全。该机制通过控制应用程序对系统资源和用户数据的访问权限，确保应用只能在获得用户明确授权的情况下执行敏感操作。

随着 Android 版本的演进，权限机制不断强化：

- **Android 6.0**：引入运行时权限模型
- **Android 10**：存储分区和权限细化
- **Android 11**：单次授权和自动重置权限
- **Android 13**：细粒度媒体权限控制



### 1.2 权限类型

#### 1.2.1 按保护级别分类对比

| **类别**     | **特点**                                       | **授权方式**                   | **示例权限**                                                 |
| :----------- | :--------------------------------------------- | :----------------------------- | :----------------------------------------------------------- |
| **普通权限** | 低风险，不影响用户隐私或设备安全               | 安装时自动授予（无需用户确认） | `INTERNET`、`ACCESS_NETWORK_STATE`、`BLUETOOTH`、`VIBRATE`   |
| **危险权限** | 涉及用户隐私或设备安全（如相机、位置、存储等） | 运行时动态请求（用户手动授权） | `CAMERA`、`READ_CONTACTS`、`ACCESS_FINE_LOCATION`、`RECORD_AUDIO` |
| **签名权限** | 仅允许相同签名的应用访问                       | 系统自动授予（同签名应用）     | `BIND_ACCESSIBILITY_SERVICE`、`INSTALL_PACKAGES`             |
| **特殊权限** | 需用户通过系统设置手动授权                     | 跳转到系统设置页面授权         | `WRITE_SETTINGS`、`SYSTEM_ALERT_WINDOW`、`REQUEST_IGNORE_BATTERY_OPTIMIZATIONS` |



#### 1.2.2 **按使用场景分类对比**

| **类别**       | **特点**                                 | **适用版本** | **示例权限**                                      |
| :------------- | :--------------------------------------- | :----------- | :------------------------------------------------ |
| **安装时权限** | 普通权限在安装时自动授予                 | Android 11+  | `INTERNET`、`ACCESS_WIFI_STATE`                   |
| **运行时权限** | 危险权限需在运行时动态请求               | Android 6.0+ | `READ_EXTERNAL_STORAGE`、`ACCESS_COARSE_LOCATION` |
| **后台权限**   | 部分权限（如位置）在后台使用时需额外声明 | Android 10+  | `ACCESS_BACKGROUND_LOCATION`                      |



#### 1.2.3 **按权限类型分类对比**

| **类别**         | **功能范围**                         | **示例权限**                                              |
| :--------------- | :----------------------------------- | :-------------------------------------------------------- |
| **硬件相关权限** | 访问设备硬件功能（相机、传感器等）   | `CAMERA`、`BLUETOOTH_SCAN`、`BODY_SENSORS`                |
| **数据相关权限** | 访问用户数据（通讯录、存储、短信等） | `READ_CONTACTS`、`READ_SMS`、`WRITE_EXTERNAL_STORAGE`     |
| **系统级权限**   | 控制系统行为或跨应用交互             | `BIND_NOTIFICATION_LISTENER_SERVICE`、`CHANGE_WIFI_STATE` |











## 二、权限机制演进

随着 Android 版本的演进，权限机制不断强化：

- **Android 6.0**：引入运行时权限模型
- **Android 10**：存储分区和权限细化
- **Android 11**：单次授权和自动重置权限
- **Android 13**：细粒度媒体权限控制



### 2.1 早期权限机制(Android 6.0 之前)

在早期的 Android 系统，用户只需要在 AndroidManifest.xml 中加入权限声明即可。

```xml
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
 package="com.example.broadcasttest">
     <uses-permission android:name="android.permission.RECEIVE_BOOT_COMPLETED" />
     ...
</manifest>
```



用户在两个方面得到了保护：

1. 在安装程序的时候，用户可以清楚地知晓该程序一共申请了哪些权限，从而决定是否要安装这个程序
2. 用户可以随时在应用程序管理界面查看任意一个程序的权限申请情况





### 2.2 权限机制( Android 6.0 )

Android 6.0 引入了运行时权限。运行时权限的核心就是在程序运行过程中由用户授权我们去执行某些危险操作，程序是不可以擅自做主去执行这些危险操作的。

#### 2.2.1 为什么引入运行时权限

- **问题**：早期 Android 系统在安装时要求用户一次性授予所有权限，用户无法细粒度控制敏感权限（如相机、位置、通讯录等）。
- **解决方案**：运行时权限将危险权限（Dangerous Permissions）的授权延迟到应用实际需要使用时，用户可明确知晓权限用途并动态管理。
  - **示例**：用户只有在使用拍照功能时才会被请求相机权限，而非安装时就强制同意。

这样做的主要目的是保护用户隐私和安全，减少恶意应用滥用权限。用户不需要在安装软件的时候一次性授权所有申请的权限，而是可以在软件的使用过程中再对某一项权限申请进行授权。





#### 2.2.2 运行时权限申请流程

基本步骤：

1. **权限检查**：使用 `ContextCompat.checkSelfPermission()`检查是否已获得权限
2. **权限请求**：如果未获得权限，调用 `ActivityCompat.requestPermissions()`发起请求
3. **处理结果**：重写 `onRequestPermissionsResult()`方法处理用户授权结果

```java
// 检查权限
if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) 
    != PackageManager.PERMISSION_GRANTED) {
    // 请求权限
    ActivityCompat.requestPermissions(this, 
        new String[]{Manifest.permission.CAMERA}, 
        PERMISSION_REQUEST_CODE);
} else {
    // 已有权限，执行操作
    openCamera();
}

// 处理权限请求结果
@Override
public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
    if (requestCode == PERMISSION_REQUEST_CODE) {
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            openCamera();
        } else {
            // 权限被拒绝
            showPermissionDeniedMessage();
        }
    }
}
```







1. **检查权限状态**: 使用 `ContextCompat.checkSelfPermission()` 方法进行检查。

   ```java
   if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) 
       != PackageManager.PERMISSION_GRANTED) {
       // 未授权，需要请求
   }
   ```

2. **请求权限**：如果没有授权的话，则需要调用 `ActivityCompat.requestPermissions()` 方法向用户申请授权。

   ```
   ActivityCompat.requestPermissions(activity, 
       new String[]{Manifest.permission.CAMERA}, 
       REQUEST_CODE);
   ```

3. **处理用户选择**

   ```
   @Override
   public void onRequestPermissionsResult(int code, String[] permissions, int[] results) {
       if (code == REQUEST_CODE && results[0] == PERMISSION_GRANTED) {
           // 用户同意，执行操作
       } else {
           // 用户拒绝，提示或降级处理
       }
   }
   ```

   



## 四、最佳实践

1. 合理申请权限

- 仅在需要时申请权限，避免一次性申请过多权限
- 对非核心功能权限采用按需申请策略

2. 权限解释

- 在请求权限前向用户解释权限用途，提高用户接受率
- 使用 `ActivityCompat.shouldShowRequestPermissionRationale()`判断是否需要解释

3. 错误处理

- 处理权限被拒绝的情况，提供友好的错误提示
- 对于永久拒绝的权限，引导用户到系统设置中手动开启

4. 版本适配

- 针对不同 Android 版本进行权限适配
- 注意 Android 10+ 对存储权限的变更



### 谨慎使用权限组

原则上，**用户一旦同意了某个权限申请之后，同组的其他权限也会被系统自动授权**。但是请谨记，不要基于此规则来实现任何功能逻辑，因为Android系统随时有可能调整权限的分 组



## 参考资料

[官方文档](https://developer.android.com/guide/topics/permissions/overview)

