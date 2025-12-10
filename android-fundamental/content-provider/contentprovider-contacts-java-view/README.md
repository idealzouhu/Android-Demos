### 项目概述

本案例演示了如何使用 Android 的 ContentProvider 读取手机联系人信息，包括联系人姓名、电话号码等数据。


### 项目结构

```
contentprovider-contacts-java-view/
├── app/src/main/java/com/example/contentprovider/contacts/
│   ├── MainActivity.java                         # 主 Activity，UI 逻辑
│   ├── adapter/
│   │   └── ContactsAdapter.java                  # RecyclerView 适配器
│   ├── model/
│   │   └── Contact.java                          # 数据模型
│   ├── provider/
│   │   ├── ContactsProvider.java                # ContentProvider 数据访问层
│   │   └── ContactsReadException.java           # 自定义异常
│   └── repository/
│       └── ContactsRepository.java              # 数据仓库层
├── app/src/main/res/
│   ├── drawable/                               
│   │   └── circle_background.xml               # 圆形背景文件
│   ├── layout/
│   │   ├── activity_main.xml
│   │   └── list_item_contact.xml
│   └── values/
│       ├── strings.xml
│       └── styles.xml
└── build.gradle
```

### 学习目标

通过该项目，你将掌握：

- ContentProvider 基础：了解 Android 内容提供者的工作原理和用途
- 联系人数据读取：掌握使用 ContentResolver 查询系统联系人数据的方法
- 权限管理：学习运行时权限的申请和处理流程
- RecyclerView 使用：掌握列表数据的展示和适配器模式
- 异步数据处理：了解在后台线程处理耗时操作的最佳实践
