### 项目概述

本案例演示了如何使用 CollapsingToolbarLayout实现可折叠式标题栏，并实现标题栏背景图与系统状态栏的融合效果。


### 项目结构

```
collapsing-toolbar-basic-java-view/
├── app/src/main/java/com/example/collapsingtoolbar/
│   └── basic/
│       ├── MainActivity.java                 # 主 Activity，包含业务逻辑
│       └── DetailActivity.java               # 详情页面，展示折叠效果
├── app/src/main/res/
│   ├── layout/
│   │   ├── activity_main.xml                # 主界面布局文件
│   │   └── activity_detail.xml              # 详情页布局文件
│   ├── values/
│   │   ├── strings.xml                      # 字符串资源
│   │   ├── colors.xml                       # 颜色资源
│   │   └── styles.xml                       # 样式主题配置（实现状态栏透明）
│   └── drawable/
│       ├── header_bg.jpg                    # 标题栏背景图片
│       └── placeholder.xml                  # 内容占位符
└── app/build.gradle                         # 项目依赖配置
```

### 学习目标

通过该项目，你将掌握：

- CollapsingToolbarLayout折叠效果：掌握 scrollFlags和 collapseMode的属性配置
- 状态栏融合技术：通过透明状态栏实现视觉延伸效果
