## 一、项目概述

本项目是一个基于 Android ContentProvider 的联系人数据读取应用，演示了如何通过 ContentResolver 查询系统联系人信息，并采用现代化的 Android 架构实现权限管理、数据加载和界面展示。该应用主要用于学习和研究 Android 系统数据访问机制。

### 1.1 核心实现思路

1. 分层架构设计： 采用**数据层-表示层**分离架构，将数据访问逻辑与 UI 逻辑完全分离，提高代码的可测试性和可维护性。

2. 运行时权限管理：遵循 Android 6.0+ 的运行时权限模型，在访问敏感数据前动态请求用户授权，确保符合现代 Android 应用的安全规范。

3. 异步数据加载：通过线程池和回调机制实现联系人数据的异步加载，避免在主线程执行耗时操作，保证应用的流畅性。




### 1.2 关键组件

| 组件类型       | 类名               | 职责描述                                  |
| :------------- | :----------------- | :---------------------------------------- |
| **主界面**     | MainActivity       | 处理UI交互、权限管理、生命周期控制        |
| **数据模型**   | Contact            | 封装联系人数据结构和业务逻辑              |
| **数据访问**   | ContactsProvider   | 通过ContentResolver与系统联系人数据库交互 |
| **数据仓库**   | ContactsRepository | 协调数据加载、异步处理和错误管理          |
| **界面适配器** | ContactsAdapter    | 管理RecyclerView数据显示和视图复用        |



### 1.3 项目结构

```
contentprovider-contacts-java-view/
├── app/src/main/java/com/example/contentprovider/contacts/
│   ├── MainActivity.java                         # 主界面，UI逻辑控制
│   ├── adapter/
│   │   └── ContactsAdapter.java                  # RecyclerView适配器
│   ├── model/
│   │   └── Contact.java                          # 数据模型
│   ├── provider/
│   │   ├── ContactsProvider.java                 # 数据访问层
│   │   └── ContactsReadException.java            # 自定义异常
│   └── repository/
│       └── ContactsRepository.java              # 数据仓库层
├── app/src/main/res/
│   ├── layout/
│   │   ├── activity_main.xml                    # 主界面布局
│   │   └── list_item_contact.xml                # 列表项布局
│   ├── drawable/
│   │   └── circle_background.xml               # 圆形背景资源
│   └── values/
│       ├── strings.xml                          # 字符串资源
│       └── styles.xml                           # 样式配置
└── app/src/main/AndroidManifest.xml            # 应用配置和权限声明
```



## 二、项目实现

如何使用 contentprovider 可以查看  [Content Provider 使用教程.md](Content Provider 使用教程.md) 



### 2.1 ContactsContract 类

**ContactsContract** 是 Android 提供的联系人数据库访问助手类，包含所有联系人相关的常量和方法。



#### 2.1.1 联系人 ContentProvider 的 URI

```
// 系统联系人的 ContentProvider URI
ContactsContract.CommonDataKinds.Phone.CONTENT_URI
// 等价于：content://com.android.contacts/data/phones
```



#### 2.1.2 基本查询配置

**ContactsContract** 定义了提供给 content provider 所使用的数据类型。

```java
// 定义要查询的列
private String[] getProjection() {
    return new String[]{
        ContactsContract.CommonDataKinds.Phone._ID,           // 记录ID
        ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,   // 显示名称
        ContactsContract.CommonDataKinds.Phone.NUMBER,         // 电话号码
        ContactsContract.CommonDataKinds.Phone.CONTACT_ID     // 联系人ID
    };
}
```



#### 2.1.3 联系人数据

联系人数据存储在多个表中，主要包含：

- **contacts表**：存储联系人的基本信息
- **data表**：存储联系人的具体数据（电话、邮箱等）
- **raw_contacts表**：存储账户相关的联系人信息










## 三、问题