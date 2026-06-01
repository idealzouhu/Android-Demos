### 项目概述

本案例将创建一个简单的记事本风格应用，核心功能是：
- 在 EditText中输入文本。
- 当应用进入后台或关闭时，自动将输入的内容保存到本地文件。
- 当应用再次启动时，自动从文件中读取上次保存的内容并显示，实现数据的持久化。
数据将保存在设备的私有目录（/data/data/<应用包名>/files/）下，其他应用无法访问，安全且有保障


### 项目结构

```
file-basic-java-view/
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

- 文件存储和读取的基本原理。
