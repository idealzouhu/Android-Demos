## 一、Activity 启动方式

在本项目中，提供了如下 Activity 的使用案例：

| ctivity名称              | 文件路径                    | 模拟功能            | Intent类型                           | 数据传递方式                                       | 学习重点                                    |
| :----------------------- | :-------------------------- | :------------------ | :----------------------------------- | :------------------------------------------------- | :------------------------------------------ |
| **MainActivity**         | `MainActivity.java`         | 主控制面板/导航中心 | 无（入口Activity）                   | 无                                                 | Activity生命周期、多种Intent启动方式        |
| **InternalActivity**     | `InternalActivity.java`     | 应用内页面跳转      | **显式Intent**                       | `putExtra()`传递简单数据                           | 显式Intent使用、基本数据传递、Activity 返回 |
| **WebViewActivity**      | `WebViewActivity.java`      | 网页内容查看器      | **隐式Intent**（响应http/https协议） | `Intent.setData()`传递URI                          | 隐式Intent、Intent Filter、URI数据处理      |
| **DataTransferActivity** | `DataTransferActivity.java` | 复杂数据传递演示    | **显式Intent**                       | `putExtra()`多种数据类型、Bundle、Serializable对象 | 复杂数据传递、Bundle使用、对象序列化        |
| **ResultActivity**       | `ResultActivity.java`       | 双向数据交互        | **显式Intent**（带返回结果）         | `startActivityForResult()`+ `setResult()`          | 双向通信、结果回调、onActivityResult处理    |





## 二、问题

### 2.1 Intent 无法识别到 Activity

#### 2.1.1 问题描述

在 Android1 项目中，查看网页内容的 intent 没有找到相应的 Activity，弹窗显示 "没有应用可以处理该请求"。

```
    private void openInternalWebView() {
        // 隐式Intent：使用相同的ACTION_VIEW和http协议
        // 系统会显示选择器，用户可以选择我们的应用或浏览器
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("https://developer.android.com"));

        // 调试方法
        debugIntentResolution(intent);

        // 创建选择器标题
        Intent chooser = Intent.createChooser(intent, "选择打开方式");

        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(chooser);
        } else {
            Toast.makeText(this, "没有应用可以处理该请求", Toast.LENGTH_SHORT).show();
        }
    }
```







#### 2.1.2 原因分析

**(1) 检查 Activity 是否存在**

手机端浏览器确实存在。

```shell
$ adb shell cmd package resolve-activity -a android.intent.action.VIEW -d https://www.baidu.com
priority=0 preferredOrder=0 match=0x208000 specificIndex=-1 isDefault=true
ActivityInfo:
  name=com.android.browser.RealBrowserActivity
  packageName=com.heytap.browser
  labelRes=0x7f120333 nonLocalizedLabel=null icon=0x7f08156c banner=0x0
  enabled=true exported=true directBootAware=false
  taskAffinity=com.android.browser.sub targetActivity=null persistableMode=PERSIST_ROOT_ONLY
  launchMode=2 flags=0x300228 privateFlags=0x2 theme=0x7f130877
  screenOrientation=-1 configChanges=0x6b3 softInputMode=0x30
  lockTaskLaunchMode=LOCK_TASK_LAUNCH_MODE_DEFAULT
  resizeMode=RESIZE_MODE_RESIZEABLE
  ApplicationInfo:
    name=com.heytap.browser.HeytapApplication
    packageName=com.heytap.browser
    labelRes=0x7f120332 nonLocalizedLabel=null icon=0x7f08156a banner=0x0
    className=com.heytap.browser.HeytapApplication
    processName=com.heytap.browser
    taskAffinity=android.task.browser
    ......
```

**(2) 核实应用内部的 Activity**

应用内部也存在相应的 Activity。

```
		<!-- WebViewActivity - 响应http协议（程序内外都可调用） -->
        <activity
            android:name=".WebViewActivity"
            android:label="网页查看"
            android:exported="true">  <!-- 允许外部调用 -->
            <intent-filter>
                <action android:name="android.intent.action.VIEW" />
                <category android:name="android.intent.category.DEFAULT" />
                <category android:name="android.intent.category.BROWSABLE" />
                <data android:scheme="http" />
                <data android:scheme="https" />
            </intent-filter>
        </activity>
```

**(3) 验证是否能查询到 Activity**

**`MATCH_DEFAULT_ONLY`** 表示只返回支持 `CATEGORY_DEFAULT`的应用。`CATEGORY_DEFAULT`表示一个 Activity **可以作为隐式 Intent 的默认处理器**。

```
  	 private void debugIntentResolution() {
        PackageManager pm = getPackageManager();

        // 创建用于测试的Intent
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("https://developer.android.com"));

        // 创建新的Intent用于当前应用查询
        Intent selfIntent = new Intent(intent);
        selfIntent.setPackage(getPackageName());
        List<ResolveInfo> selfActivities = pm.queryIntentActivities(selfIntent, MATCH_DEFAULT_ONLY );
        Log.d("IntentDebug", "当前应用可处理的数量: " + selfActivities.size());

        // 创建新的Intent用于全局查询
        Intent globalIntent = new Intent(intent);
        globalIntent.setPackage(null);
        List<ResolveInfo> allActivities = pm.queryIntentActivities(globalIntent, MATCH_DEFAULT_ONLY );
        Log.d("IntentDebug", "全局可处理的数量: " + allActivities.size());
    }
```

结果却为：

```
2025-11-10 16:01:50.898 27391-27391 IntentDebug             com.example.multi.activity           D  当前应用可处理的数量: 1
2025-11-10 16:01:50.904 27391-27391 IntentDebug             com.example.multi.activity           D  全局可处理的数量: 0
```

这个就比较矛盾了，`pm.queryIntentActivities(selfIntent, MATCH_DEFAULT_ONLY )` 可以查询到，但 `intent.resolveActivity(getPackageManager())` 和 `pm.queryIntentActivities(globalIntent, MATCH_DEFAULT_ONLY )` 却查询不到。





#### 2.1.3 解决方案

从Android 10（API 29）开始，Google引入了**重大隐私和安全变更**，即**包可见性过滤 (Package Visibility Filtering)**。这直接影响了Intent查询的结果。

| Android版本         | 默认行为                       |
| :------------------ | :----------------------------- |
| **Android 9及以下** | 可查询所有应用                 |
| **Android 10+**     | 只能查询自己应用和少数系统应用 |

在AndroidManifest.xml中添加：

```xml
<!-- 添加查询权限 -->
    <queries>
        <intent>
            <action android:name="android.intent.action.VIEW" />
            <category android:name="android.intent.category.DEFAULT" />
            <data android:scheme="https" />
        </intent>
    </queries>
```





### 2.2 Intent 查询的 Activity 有问题

#### 2.2.1 问题描述

```java
   private void debugIntentResolution() {
        PackageManager pm = getPackageManager();

        // 创建用于测试的Intent
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("https://developer.android.com"));

        // 创建新的Intent用于当前应用查询
        Intent selfIntent = new Intent(intent);
        selfIntent.setPackage(getPackageName());
        List<ResolveInfo> selfActivities = pm.queryIntentActivities(selfIntent, MATCH_DEFAULT_ONLY );
        Log.d("IntentDebug", "当前应用可处理的数量: " + selfActivities.size());
        for (ResolveInfo info : selfActivities) {
            Log.d("IntentDebug", "应用: " + info.activityInfo.packageName);
        }

        // 创建新的Intent用于全局查询
        Intent globalIntent = new Intent(intent);
        globalIntent.setPackage(null);
        // List<ResolveInfo> allActivities = pm.queryIntentActivities(globalIntent, MATCH_ALL );
        List<ResolveInfo> allActivities = pm.queryIntentActivities(globalIntent, MATCH_DEFAULT_ONLY );
        Log.d("IntentDebug", "全局可处理的数量: " + allActivities.size());
        for (ResolveInfo info : allActivities) {
            Log.d("IntentDebug", "应用: " + info.activityInfo.packageName);
        }
    }

```

针对以上代码，使用 `MATCH_ALL` 能查询程序内自己定义的 Activity， 结果为

```
2025-11-10 17:01:42.352  8530-8530  IntentDebug             com.example.multi.activity           D  当前应用可处理的数量: 1
2025-11-10 17:01:42.352  8530-8530  IntentDebug             com.example.multi.activity           D  应用: com.example.multi.activity
2025-11-10 17:01:42.354  8530-8530  IntentDebug             com.example.multi.activity           D  全局可处理的数量: 5
2025-11-10 17:01:42.354  8530-8530  IntentDebug             com.example.multi.activity           D  应用: com.heytap.browser
2025-11-10 17:01:42.354  8530-8530  IntentDebug             com.example.multi.activity           D  应用: com.android.chrome
2025-11-10 17:01:42.354  8530-8530  IntentDebug             com.example.multi.activity           D  应用: com.example.multi.activity
2025-11-10 17:01:42.354  8530-8530  IntentDebug             com.example.multi.activity           D  应用: com.netease.cloudmusic
2025-11-10 17:01:42.354  8530-8530  IntentDebug             com.example.multi.activity           D  应用: com.tencent.mtt
```

但是，使用 `MATCH_DEFAULT_ONLY`查询的时候，结果为：

```
2025-11-10 17:07:34.690 10255-10255 IntentDebug             com.example.multi.activity           D  当前应用可处理的数量: 1
2025-11-10 17:07:34.690 10255-10255 IntentDebug             com.example.multi.activity           D  应用: com.example.multi.activity
2025-11-10 17:07:34.697 10255-10255 IntentDebug             com.example.multi.activity           D  全局可处理的数量: 1
2025-11-10 17:07:34.698 10255-10255 IntentDebug             com.example.multi.activity           D  应用: com.heytap.browser
```







#### 2.2.2 原因分析

(1) 核实应用内部的 Activity

应用内部也存在相应的 Activity。

```
		<!-- WebViewActivity - 响应http协议（程序内外都可调用） -->
        <activity
            android:name=".WebViewActivity"
            android:label="网页查看"
            android:exported="true">  <!-- 允许外部调用 -->
            <intent-filter>
                <action android:name="android.intent.action.VIEW" />
                <category android:name="android.intent.category.DEFAULT" />
                <category android:name="android.intent.category.BROWSABLE" />
                <data android:scheme="http" />
                <data android:scheme="https" />
            </intent-filter>
        </activity>
```

