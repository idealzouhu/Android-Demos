### 项目概述

本案例演示了 Android 中单个 Activity 的基础使用方法，重点展示了如何实现自定义标题栏和菜单栏功能。


### 项目结构

```
activity-basic-java-view/
├── app/src/main/java/com/example/activity/basic/
│   └── MainActivity.java                    # 主 Activity，包含业务逻辑
├── app/src/main/res/
│   ├── layout/
│   │   └── activity_main.xml                # 主界面布局文件
│   ├── menu/
│   │   └── main_menu.xml                    # 菜单资源文件
│   └── values/
│       ├── strings.xml                      # 字符串资源
│       └── styles.xml                       # 样式主题配置
└── build.gradle                             # 项目依赖配置
```

### 学习目标

通过该项目，你将掌握：

- 自定义标题栏布局设计，如动态标题修改、菜单按钮集成、Material Design 风格
- 菜单系统实现，如菜单资源文件创建与配置、菜单项点击事件处理
- 基础交互功能，如Button 点击事件监听，Toast 消息提示，状态实时更新