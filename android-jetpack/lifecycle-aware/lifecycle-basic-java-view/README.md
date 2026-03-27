### 项目概述

本案例演示 Android Lifecycle 组件的基本使用方法，重点展示如何通过 LifecycleObserver 监听 Activity 的生命周期变化.



### 项目结构

```
lifecycle-basic-java-view/
├── 📁 app/
│   ├── src/main/java/com/example/lifecycle/basic/
│   │   ├── MainActivity.java                     # 主 Activity，管理生命周期和界面
│   │   └── MyLifecycleObserver.java              # 生命周期观察者，监听状态变化
│   ├── src/main/res/
│   │   ├── layout/
│   │   │   └── activity_main.xml                 # 主界面布局，使用 ConstraintLayout
│   │   ├── values/
│   │   │   ├── colors.xml                        # 颜色资源定义
│   │   │   └── strings.xml                       # 字符串资源定义
│   └── build.gradle.kts                          # 模块级依赖配置
├── 📄 build.gradle.kts                           # 项目级构建配置
├── 📄 README.md                                  # 项目说明文档
└── 📄 settings.gradle.kts                        # 项目设置
```



### 学习目标

通过该项目，你将掌握：

- Lifecycle 组件的基本概念和使用方法
- 如何实现 LifecycleObserver 接口监听生命周期事件
- 在 Activity 中注册生命周期观察者
