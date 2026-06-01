## 一、项目概述

本案例通过三个Service实现类，全面演示了Android Service的不同使用方式。每个Service都有明确的职责和特点，便于理解Service的核心概念。

### 1.1 核心实现思路

通过实现三种不同模式的Service，直观展示它们的差异和适用场景。



### 1.2 关键组件

| 组件              | 职责            | 特点                                 |
| ----------------- | --------------- | ------------------------------------ |
| MyStartedService  | 启动式Service   | 独立运行，不依赖Activity             |
| MyBoundService    | 绑定式Service   | 支持双向通信，与Activity生命周期同步 |
| HybridService     | 混合模式Service | 同时支持启动和绑定                   |
| MainActivity      | 主界面          | 提供UI控制和状态展示                 |
| ServiceConnection | 绑定连接器      | 管理Service绑定状态                  |



### 1.3 项目结构

```
service-startup-java-view/
├── app/src/main/java/com/example/service/startup/
│   ├── MainActivity.java                    # 主Activity，包含UI和交互逻辑
│   ├── MyStartedService.java                # 启动式Service示例
│   ├── MyBoundService.java                  # 绑定式Service示例
│   └── HybridService.java                   # 混合模式Service示例
├── app/src/main/res/
│   ├── layout/
│   │   └── activity_main.xml               # 主界面布局
│   └── values/
│       ├── strings.xml                      # 字符串资源
│       └── colors.xml                       # 颜色资源
└── app/src/main/AndroidManifest.xml        # 应用配置
```



## 二、功能模块详解

### 2.1 启动式Service

**MyStartedService  核心功能**：

- 通过startService()启动，独立于Activity运行
- 在onStartCommand()中执行耗时任务
- 通过stopService()或stopSelf()停止

```java
@Override
public int onStartCommand(Intent intent, int flags, int startId) {
    // 启动后台任务
    new Thread(() -> {
        // 模拟耗时操作
        Thread.sleep(3000);
        // 发送广播通知完成
        sendBroadcast(new Intent("STARTED_SERVICE_TASK_COMPLETED"));
    }).start();
    
    return START_STICKY; // Service被杀死后自动重启
}
```

生命周期：

```
onCreate() → onStartCommand() → onDestroy()
```



### 2.2  绑定式Service

**MyBoundService  核心功能**：

- 通过bindService()绑定，与Activity生命周期同步
- 通过Binder接口提供业务方法
- 支持双向通信

```java
// Binder接口定义
public class LocalBinder extends Binder {
    MyBoundService getService() {
        return MyBoundService.this;
    }
}

// ServiceConnection实现
private ServiceConnection connection = new ServiceConnection() {
    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        MyBoundService.LocalBinder binder = (MyBoundService.LocalBinder) service;
        boundService = binder.getService();
        isBound = true;
    }
    
    @Override
    public void onServiceDisconnected(ComponentName name) {
        isBound = false;
    }
};
```

**生命周期**：

```
onCreate() → onBind() → onUnbind() → onDestroy()
```





### 2.3 混合模式Service

HybridService **核心功能**：

- 同时支持startService()和bindService()
- 启动式任务：使用启动式 Service 模拟下载进度
- 绑定式方法：使用绑定式 Service 获取下载状态，并显示进度

```java
// 启动式任务
@Override
public int onStartCommand(Intent intent, int flags, int startId) {
    if ("start_download".equals(intent.getAction())) {
        startDownloadTask(startId);
    }
    return START_STICKY;
}

// 绑定式方法
public int getDownloadProgress() {
    return downloadProgress;
}

public boolean isDownloading() {
    return isDownloading;
}
```

注意：绑定的 Service 和 启动的 Service 是同一个Service。



## 三、运行效果

service 生命周期的演示过程，可以通过 `package:mine   tag=: StartedService | mainactivity |  BoundService | HybridService`  中的对应 Logcat 日志来查看。






## 四、问题

暂无