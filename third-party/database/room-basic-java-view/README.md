### 项目概述

本案例演示了 Android 中使用 Room 数据库进行数据持久化的基本使用方法，重点展示了如何实现用户信息的增删改查（CRUD）功能。

### 项目结构

```
room-basic-java-view/

├── app/src/main/java/com/example/room/basic/

│   ├── MainActivity.java                    # 主 Activity，包含业务逻辑

│   ├── User.java                            # 用户实体类（Entity）

│   ├── UserDao.java                         # 数据访问对象（DAO）

│   ├── AppDatabase.java                     # Room 数据库类

│   ├── UserViewModel.java                   # ViewModel，向 UI 层暴露数据

│   └── UserAdapter.java                     # RecyclerView 适配器

├── app/src/main/res/

│   ├── layout/

│   │   ├── activity_main.xml                # 主界面布局文件（ConstraintLayout）

│   │   └── item_user.xml                    # 用户列表项布局

│   └── values/

│       └── strings.xml                       # 字符串资源

└── app/build.gradle.kts                     # 项目依赖配置

```

### 学习目标

通过该项目，你将掌握：

- Room 数据库基础，如 Room 的三层架构（Entity、DAO、Database）及其作用

- 实体类定义，如使用 `@Entity` 注解定义数据库表，使用 `@PrimaryKey` 定义主键

- DAO 接口设计，如使用 `@Dao` 注解定义数据访问接口，掌握 `@Insert`、`@Update`、`@Delete`、`@Query` 等注解的使用

- 数据库初始化，如使用单例模式创建数据库实例，配置数据库参数

- ViewModel 使用，如使用 ViewModel 向 UI 层暴露数据，封装数据库操作，实现 MVVM 架构模式

