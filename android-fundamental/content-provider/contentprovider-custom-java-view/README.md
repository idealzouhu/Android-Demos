### 项目概述

本案例演示了 Android Content Provider 的核心机制，通过多应用架构实现跨应用数据共享。项目包含一个数据提供者应用（Provider）和一个数据消费者应用（Client），完整展示了 Content Provider 的创建、配置和使用流程。
核心特性：
- 多应用架构：Provider 应用提供数据，Client 应用消费数据
- 完整 CRUD 操作：支持数据的增删改查操作
- 权限安全控制：基于权限的数据访问安全机制
- 实时数据同步：Content Observer 实现数据变化监听


### 项目结构

```
content-provider-custom-java-view/
├── 📱 shared-library/                          # 共享库模块
│   ├── src/main/java/com/example/contentprovider/shared/
│   │   └── BookContract.java                  # 数据契约类（URI、表结构定义）
│   └── build.gradle.kts
├── 📱 provider-app/                           # 数据提供者应用
│   ├── src/main/java/com/example/contentprovider/provider/
│   │   ├── BookDbHelper.java                  # 数据库帮助类
│   │   ├── BookProvider.java                  # Content Provider 实现
│   │   └── ProviderMainActivity.java          # Provider 主界面
│   ├── src/main/res/
│   │   ├── layout/
│   │   │   ├── activity_provider_main.xml    # Provider 主界面布局
│   │   │   └── item_book.xml                 # 书籍列表项布局
│   │   └── AndroidManifest.xml
│   └── build.gradle.kts
├── 📱 client-app/                            # 数据消费者应用
│   ├── src/main/java/com/example/contentprovider/client/
│   │   └── ClientMainActivity.java           # Client 主界面
│   ├── src/main/res/
│   │   ├── layout/
│   │   │   ├── activity_client_main.xml      # Client 主界面布局
│   │   │   └── item_book_client.xml          # 客户端书籍项布局
│   │   └── AndroidManifest.xml
│   └── build.gradle.kts
├── 📄 settings.gradle.kts                    # 多模块配置
└── 📄 README.md
```

### 学习目标

通过本项目的学习，你将掌握：

- 自定义 Content Provider
- 自定义 Content Provider 所需要的权限
- 了解 Provider 和 Client 应用之间的统一接口，即数据契约类
