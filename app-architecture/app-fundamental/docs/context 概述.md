## 一、Context 概述

### 1.1 什么是 Context

**Context** 是一个抽象类，它的实现由 Android 系统提供。可以理解为：

- **当前应用环境的“上下文”信息**
- **连接应用组件与系统服务的桥梁**
- **访问应用资源的入口点**



### 1.2 Context 的主要作用

#### 1.2.1 访问应用资源



```java
// 获取字符串资源
String appName = context.getString(R.string.app_name);

// 获取颜色资源
int color = context.getColor(R.color.primary);

// 获取Drawable资源
Drawable icon = context.getDrawable(R.drawable.ic_launcher);

// 访问assets和res
AssetManager assets = context.getAssets();
Resources res = context.getResources();
```



#### 1.2.2 启动组件

```java
// 启动Activity
context.startActivity(new Intent(context, MainActivity.class));

// 启动Service
context.startService(new Intent(context, MyService.class));

// 发送广播
context.sendBroadcast(new Intent("MY_ACTION"));
```





#### 1.2.3 获取系统服务

```java
// 获取各种系统服务
NotificationManager nm = (NotificationManager) 
    context.getSystemService(Context.NOTIFICATION_SERVICE);

LocationManager lm = (LocationManager)
    context.getSystemService(Context.LOCATION_SERVICE);

AudioManager am = (AudioManager)
    context.getSystemService(Context.AUDIO_SERVICE);

PackageManager pm = context.getPackageManager(); 
```



#### 1.2.4 获取应用上下文

通过 Context 获取当前应用在 Android 系统中的完整环境信息。

```java
// 获取应用身份
String packageName = context.getPackageName();      // 包名
int uid = context.getApplicationInfo().uid;         // 用户ID
String processName = getApplicationInfo().processName; // 进程名

// 获取应用存储路径
File filesDir = context.getFilesDir();      // /data/data/<package>/files
File cacheDir = context.getCacheDir();      // /data/data/<package>/cache
File externalDir = context.getExternalFilesDir(null);  // 外部存储


// 运行环境
ApplicationInfo appInfo = context.getApplicationInfo();  // 应用信息
int theme = context.getThemeResId();                     // 当前主题
ClassLoader classLoader = context.getClassLoader();     // 类加载器

// 配置信息
SharedPreferences prefs = context.getSharedPreferences("config", MODE_PRIVATE);

```



### 1.3 Context 的类型

| Context 类型                  | 生命周期                 | 是否关联UI | 是否包含主题 | 典型用途                                   | 内存泄漏风险 | 注意事项                                   |
| ----------------------------- | ------------------------ | ---------- | ------------ | ------------------------------------------ | ------------ | ------------------------------------------ |
| **Application Context**       | 应用全程（从启动到终止） | ❌ 否       | ❌ 否         | 单例、全局配置、长时间任务、资源访问       | 低           | 启动Activity需添加`FLAG_ACTIVITY_NEW_TASK` |
| **Activity Context**          | Activity生命周期         | ✅ 是       | ✅ 是         | UI操作、启动Activity、显示Dialog、加载布局 | 高           | 避免静态或长时间持有                       |
| **Service Context**           | Service生命周期          | ❌ 否       | ❌ 否         | 后台任务、系统服务调用                     | 中等         | 与Service生命周期绑定                      |
| **BroadcastReceiver Context** | 广播接收期间             | ❌ 否       | ❌ 否         | 广播处理、短暂任务                         | 低           | 不能用于注册需要长期监听的组件             |



#### 1.3.1 Application Context

Application Context 适合单例、长时间运行的任务，但不能用于启动与 UI 相关的操作（如弹Toast、启动Activity需要NEW_TASK）。

```java
// 全局唯一的Context，生命周期与应用相同
Context appContext = getApplicationContext();
```



#### 1.3.2 Activity Context

Activity Context 與 Activity 的生命周期一致，適用於與當前 Activity 相關的操作，例如啟動新 Activity 或顯示 Dialog。可以直接使用 `this` 或 `getActivity()` 獲取。

Activity Context 与Activity生命周期绑定，包含主题信息，用于UI相关操作、启动Activity、显示Dialog等。注意，避免长时间持有导致内存泄漏

```java
// Activity本身就是Context
Context activityContext = this;  // 在Activity中
```



#### 1.3.3 Service Context

```java
// Service中的Context
public class MyService extends Service {
    public void someMethod() {
        Context serviceContext = this;
    }
}
```



#### 1.3.4 BroadcastReceiver Context





## 二、Context 的实现原理

### 2.1 Context 的继承结构

Context 通过继承结构，由 ContextImpl 实现具体功能，ContextWrapper 进行功能扩展，ContextThemeWrapper 负责主题相关的管理 。

```
Context (抽象类)
    ├── ContextWrapper (包装类)
    │       ├── Application
    │       ├── Service
    │       └── ContextThemeWrapper
    │               └── Activity
    └── ContextImpl (系统实现，实际功能提供者)
```









## 三、最佳实践

谨慎使用全局 context









## 四、其他

### 4.1 弹出 Toast 可以使用 Application Context 吗？

可以弹出，但有条件：

- **Android 9.0 (API 28) 及以上**：可以直接使用 Application Context
- **Android 8.1 (API 27) 及以下**：在某些情况下会崩溃

Toast 显示需要的东西：

1. **Window Token**：标识显示在哪个窗口上
2. **应用包名**：标识是哪个应用显示的
3. **UI 线程**：需要在主线程显示

关键区别在于：

```java
// Activity Context
Activity activity = (Activity) context;
IBinder token = activity.getWindow().getDecorView().getWindowToken();
// 返回有效的 Window Token

// Application Context
Application app = (Application) context;
IBinder token = app.getWindowContextToken();  // 可能返回 null
// Application 没有关联的 Window
```





### 4.2 使用不同 context 来启动组件

```java
// Activity Context：在当前任务栈
activityContext.startActivity(intent);
// 不需要 FLAG_ACTIVITY_NEW_TASK
// 启动的 Activity 在同一个返回栈


// Application Context：必须新建任务栈
intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
appContext.startActivity(intent);
// 可能创建新的任务栈，影响返回逻辑
```









## 参考资料

[一文吃透Android Context：从原理到实战一、Context 是什么？ 在 Android 开发中，你是否曾好 - 掘金](https://juejin.cn/post/7483066698318839835#heading-12)

