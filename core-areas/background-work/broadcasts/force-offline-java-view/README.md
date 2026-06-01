### 项目概述

本案例演示了 Android 应用中实现强制离线功能的完整解决方案。当用户账号在其他设备登录时，系统会自动将当前设备的所有界面强制退出，并返回登录页面。该项目采用了广播机制、Activity 统一管理和全局对话框等核心技术，展示了如何优雅地处理用户会话过期问题。


### 项目结构

```
force-offline-java-view/
├── app/src/main/java/com/example/broadcast/forceoffline/
│   ├── base/
│   │   └── BaseActivity.java                 # 所有Activity的基类，统一处理强制离线广播
│   ├── manager/
│   │   └── ActivityCollector.java            # Activity管理器，统一管理所有Activity
│   ├── receiver/
│   │   └── ForceOfflineReceiver.java         # 强制离线广播接收器
│   ├── ui/
│   │   ├── LoginActivity.java                # 登录界面
│   │   └── MainActivity.java                 # 主界面（演示强制离线功能）
│   └── utils/
│       └── Constants.java                    # 常量定义
├── app/src/main/res/
│   ├── layout/
│   │   ├── activity_login.xml                # 登录界面布局
│   │   └── activity_main.xml                 # 主界面布局
│   ├── values/
│   │   ├── strings.xml                       # 字符串资源
│   │   └── colors.xml                        # 颜色资源
│   └── menu/
│       └── main_menu.xml                     # 菜单资源文件
├── app/src/main/AndroidManifest.xml           # 应用配置文件
└── app/build.gradle                          # 项目依赖配置
```

### 学习目标

通过该项目，你将掌握：

1. Activity 统一管理（ActivityCollector）
   - 使用单例模式管理所有活跃的 Activity
   - 提供一键关闭所有 Activity 的方法
   - 避免内存泄漏，确保完全退出应用
2. 基类设计（BaseActivity）
   - 所有 Activity 继承自此基类
   - 动态注册强制离线广播接收器
   - 统一处理生命周期管理
   - 确保任何界面都能接收强制离线通知
3. 广播机制实现
   - 使用有序广播确保处理顺序
   - 全局对话框阻止用户操作
   - 安全跳转回登录界面