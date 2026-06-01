### 项目概述

本案例演示了 Android 中 Toolbar控件的基础使用方法, 重点展示了如何配置和使用 Toolbar作为应用栏，包括设置标题、菜单以及处理其点击事件。


### 项目结构

```
toolbar-basic-java-view/
├── app/src/main/java/com/example/toolbar/basic/
│   ├── MainActivity.java                    # 主 Activity，包含 Toolbar 初始化及事件逻辑
│   └── SecondActivity.java                  # 二级页面，展示返回导航
├── app/src/main/res/
│   ├── layout/
│   │   ├── activity_main.xml                # 主界面布局文件
│   │   └── activity_second.xml              # 二级界面布局文件
│   ├── menu/
│   │   ├── main_menu.xml                    # 主页面菜单资源文件
│   │   └── second_menu.xml                  # 二级页面菜单资源文件 (可选)
│   └── values/
│       ├── colors.xml                        # 颜色资源定义
│       ├── strings.xml                       # 字符串资源
│       └── styles.xml                        # 样式主题配置 (关键：使用 NoActionBar 主题)
└── build.gradle                              # 项目依赖配置
```


### 学习目标

通过该项目，你将掌握：

- Toolbar 基础配置与主题设置
- 菜单系统实现与事件处理
