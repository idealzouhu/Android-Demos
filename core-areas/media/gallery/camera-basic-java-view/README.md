### 项目概述

本案例演示了如何使用系统相机和图库API进行拍照和图片选择功能。


### 项目结构

```
camera-basic-java-view/
├── 📱 app/
│   ├── src/main/
│   │   ├── java/com/example/camera/
│   │   │   ├── MainActivity.java                    # 主活动，包含所有业务逻辑
│   │   │   └── Utils.java                           # 工具类（可选）
│   │   ├── res/
│   │   │   ├── drawable/                            # 图片资源目录
│   │   │   ├── layout/
│   │   │   │   └── activity_main.xml                # 主界面布局文件
│   │   │   └── xml/
│   │   │       └── file_paths.xml                   # FileProvider配置文件
│   │   └── AndroidManifest.xml                      # 应用清单文件                            
│   └── build.gradle.kts                             # 模块级构建配置
├── 📄 README.md                                     # 本文件
└── 📄 settings.gradle.kts                           # 项目设置文件
```

### 学习目标

通过该项目，你将掌握：

- 使用系统相机和图库功能
- Android不同版本的权限适配（READ_EXTERNAL_STORAGE vs READ_MEDIA_IMAGES）
- 应用私有文件存储位置管理