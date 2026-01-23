# Room 数据库使用指南

## 一、Room 库概述

### 1.1 什么是 Room 库

[Room](https://developer.android.google.cn/topic/libraries/architecture/room?hl=zh-cn) 是一个属于 Android Jetpack 的持久性库，简化了 Android 数据库的使用。换句话说，Room 是 SQLite 之上的一个抽象层，提供方便的 API 来设置、配置和查询数据库。

Room 并不直接使用 SQLite，而是负责简化数据库设置和配置以及数据库与应用交互方面的琐碎工作。Room 还提供 SQLite 语句的**编译时检查**，可以在编译阶段发现 SQL 语法错误，而不是等到运行时。

### 1.2 依赖配置

Room 使用注解处理器在编译时生成数据库实现代码，因此需要配置相应的注解处理器。注解处理器会扫描 `@Entity`、`@Dao`、`@Database` 等注解，自动生成 SQLite 数据库操作代码，实现编译时检查 SQL 语句的正确性，并生成高效的数据库访问实现。

#### 1.2.1 Kotlin 项目依赖配置

对于 Kotlin 项目，使用 KSP（Kotlin Symbol Processing）作为注解处理器：

```kotlin
// build.gradle.kts
plugins {
    id("com.google.devtools.ksp") version "2.0.21-1.0.27"
}

dependencies {
    // Room 运行时库
    implementation("androidx.room:room-runtime:2.6.1")
    // Room 编译器（KSP）
    ksp("androidx.room:room-compiler:2.6.1")
    // Room KTX 扩展（可选，提供 Kotlin 协程支持）
    implementation("androidx.room:room-ktx:2.6.1")
}
```

> **注意**：KSP 是一个功能强大且简单易用的 API，用于解析 Kotlin 注解。它是 Kotlin 版本的注解处理器。

#### 1.2.2 Java 项目依赖配置

对于 Java 项目，使用 `annotationProcessor` 作为注解处理器：

```kotlin
// build.gradle.kts
dependencies {
    // Room 运行时库
    implementation("androidx.room:room-runtime:2.6.1")
    // Room 编译器（annotationProcessor）
    annotationProcessor("androidx.room:room-compiler:2.6.1")
}
```

> **重要提示**：
> - Kotlin 项目使用 `ksp`，Java 项目使用 `annotationProcessor`
> - 不能同时使用两者，否则会导致编译错误
> - 对于 Gradle 文件中的库依赖项，请务必使用 [AndroidX 版本](https://developer.android.google.cn/jetpack/androidx/versions?hl=zh-cn) 页面中最新稳定发布版本的版本号

#### 1.2.3 使用 Version Catalog（推荐）

使用 `libs.versions.toml` 统一管理版本：

```toml
[versions]
room = "2.6.1"

[libraries]
room-runtime = { group = "androidx.room", name = "room-runtime", version.ref = "room" }
room-compiler = { group = "androidx.room", name = "room-compiler", version.ref = "room" }
room-ktx = { group = "androidx.room", name = "room-ktx", version.ref = "room" }
```

在 `build.gradle.kts` 中使用：

```kotlin
dependencies {
    implementation(libs.room.runtime)
    // Kotlin 项目
    ksp(libs.room.compiler)
    // 或 Java 项目
    // annotationProcessor(libs.room.compiler)
    implementation(libs.room.ktx) // 可选
}
```

### 1.3 Room 库和官方原生 API 的区别

| 特性维度           | SQLiteOpenHelper / SQLiteDatabase                            | Room                                                         |
| :----------------- | :----------------------------------------------------------- | :----------------------------------------------------------- |
| **抽象层级**       | 较低层级，需直接编写 SQL 语句，管理数据库连接和版本          | 较高层级，通过注解和抽象类（DAO）进行声明式操作              |
| **SQL 查询检查**   | 运行时才能发现 SQL 语法错误                                  | **编译时检查** SQL 语句的正确性，提前发现错误                |
| **数据库操作线程** | 默认不处理，需开发者自行管理线程，否则易在主线程操作导致 ANR | 默认强制**后台线程**执行数据库操作，避免阻塞 UI 线程         |
| **数据与对象映射** | 手动实现，需在 `ContentValues` 和对象间来回转换              | **自动化**，内置完整的 **ORM** 功能，直接操作对象            |
| **数据库迁移**     | 在 `onUpgrade` 方法中**手动编写**迁移 SQL 逻辑，繁琐易错     | 提供 **Migration** 类，可验证和**渐进式**迁移，更安全可靠    |
| **架构整合**       | 无直接支持                                                   | 与 **LiveData**、**RxJava**、**Flow** 等响应式组件无缝集成，便于实现 MVVM |
| **学习与使用成本** | 需要熟悉 SQL 语法，编写较多样板代码                          | 基于注解的 API 更简洁，易于上手                              |
| **性能**           | 直接操作 SQLite，有最直接的性能表现                          | 性能接近原生，优化良好，开销可忽略                           |

## 二、Room 核心组件

Room 由三个核心组件组成，它们协同工作以实现数据库操作：

- **[Entity](https://developer.android.google.cn/training/data-storage/room/defining-data?hl=zh-cn)**: 表示应用数据库中的表。您可以使用它们更新表中的行所存储的数据，以及创建要插入的新行。
- **[DAO](https://developer.android.google.cn/training/data-storage/room/accessing-data?hl=zh-cn)**: 提供了供应用在数据库中检索、更新、插入和删除数据的方法。
- **[Database](https://developer.android.google.cn/reference/kotlin/androidx/room/Database?hl=zh-cn)**: 用于定义数据库中的关键信息，包括数据库的版本号、包含哪些实体类以及提供 Dao 层的访问实例。

下图演示了 Room 的各组件如何协同工作以与数据库交互。

![a3288e8f37250031.png](images/a3288e8f37250031.png)

### 2.1 Entity（实体类）

[Entity](https://developer.android.google.cn/reference/androidx/room/Entity?hl=zh-cn) 类定义了一个表，该类的每个实例都表示数据库表中的一行。Entity 类以映射告知 Room 它打算如何呈现数据库中的信息并与之交互。

#### 2.1.1 基本用法

`@Entity` 注解用于将某个类标记为数据库 Entity 类。对于每个 Entity 类，该应用都会创建一个数据库表来保存这些项。除非另行说明，否则 Entity 的每个字段在数据库中都表示为一列。

**Java 示例：**

```java
@Entity(tableName = "users")
public class User {
    @PrimaryKey(autoGenerate = true)
    private int id;
    
    private String name;
    private String email;
    private int age;

    // 构造函数
    public User() {}
    
    public User(String name, String email, int age) {
        this.name = name;
        this.email = email;
        this.age = age;
    }

    // Getter 和 Setter 方法
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }
}
```

#### 2.1.2 关键注解

- **`@Entity`**: 标记类为数据库实体
  - `tableName`: 自定义表名（默认使用类名）
  - `indices`: 定义索引
  - `foreignKeys`: 定义外键

- **`@PrimaryKey`**: 标记主键字段
  - `autoGenerate = true`: 自动生成主键值

- **`@ColumnInfo`**: 自定义列信息
  - `name`: 自定义列名
  - `index`: 是否为该列创建索引

- **`@Ignore`**: 忽略字段，不存储到数据库

> **注意**：
> - 存储在数据库中的每个实体实例都必须有一个主键
> - 主键用于唯一标识数据库表中的每个记录/条目
> - 应用分配主键后，便无法再修改主键；只要主键存在于数据库中，它就会表示实体对象
> - 默认情况下，表名称与类名称相同。使用 `tableName` 参数可自定义表名称

### 2.2 DAO（数据访问对象）

[数据访问对象](https://developer.android.google.cn/reference/androidx/room/Dao?hl=zh-cn) (DAO) 是一种模式，其作用是通过提供抽象接口将持久性层与应用的其余部分分离。这种分离遵循[单一责任原则](https://en.wikipedia.org/wiki/Single-responsibility_principle)。

DAO 的功能在于，让在底层持久性层执行数据库操作所涉及的所有复杂性与应用的其余部分分离。这样，您就可以独立于使用数据的代码更改数据层。

![8b91b8bbd7256a63.png](images/8b91b8bbd7256a63.png)

DAO 是一个自定义接口，提供查询/检索、插入、删除和更新数据库的便捷方法。**Room 将在编译时生成该类的实现。**

#### 2.2.1 基本注解

- **`@Insert`**: 插入数据，无需编写 SQL 语句
- **`@Update`**: 更新数据
- **`@Delete`**: 删除数据
- **`@Query`**: 自定义 SQL 查询语句。当我们想要从数据库中查询数据，或者使用非实体类参数来增删改数据的时候，必须通过编写 SQL 语句来实现。

**Java 示例：**

```java
@Dao
public interface UserDao {
    // 插入
    @Insert
    void insert(User user);
    
    // 插入多个
    @Insert
    void insertAll(User... users);
    
    // 更新
    @Update
    void update(User user);
    
    // 删除
    @Delete
    void delete(User user);
    
    // 查询所有用户
    @Query("SELECT * FROM users ORDER BY id DESC")
    LiveData<List<User>> getAllUsers();
    
    // 根据 ID 查询
    @Query("SELECT * FROM users WHERE id = :userId")
    LiveData<User> getUserById(int userId);
    
    // 根据名称查询
    @Query("SELECT * FROM users WHERE name LIKE :name")
    LiveData<List<User>> findUsersByName(String name);
    
    // 删除所有
    @Query("DELETE FROM users")
    void deleteAllUsers();
}
```

#### 2.2.2 返回值类型

DAO 方法可以返回不同的类型：

1. **`LiveData<T>`**（推荐用于 Java 项目）
   - 生命周期感知
   - 自动在后台线程执行查询
   - 数据变化时自动通知观察者

2. **`Flow<T>`**（推荐用于 Kotlin 项目）
   - 响应式数据流
   - 自动在后台线程执行查询
   - 需要 Kotlin 协程支持

3. **`RxJava`**（`Observable`、`Single` 等）
   - 需要添加 `room-rxjava2` 或 `room-rxjava3` 依赖

4. **直接返回对象**（不推荐）
   - 必须在后台线程调用
   - 无法自动响应数据变化

> **重要提示**：
> - 数据库操作的执行可能用时较长，因此需要在单独的线程上运行
> - Room 不允许在主线程上访问数据库（除非使用 `allowMainThreadQueries()`，不推荐）
> - 使用 `LiveData` 或 `Flow` 作为返回值时，Room 会自动在后台线程执行查询
> - 对于写操作（insert、update、delete），必须在后台线程执行

### 2.3 Database（数据库类）

**[`Database`](https://developer.android.google.cn/reference/kotlin/androidx/room/Database?hl=zh-cn) 类可为您的应用提供您定义的 DAO 实例。**反过来，应用可以使用 DAO 从数据库中检索数据，作为关联的数据实体对象的实例。此外，应用还可以使用定义的数据实体更新相应表中的行，或者创建新行供插入。

#### 2.3.1 创建 Database 类

您需要创建一个抽象 `RoomDatabase` 类，并为其添加 `@Database` 注解。 Database 类的内容非常固定，只需要定义3部分内容：数据库的版本号、包含哪些实体类、提供 Dao 层的访问实例。

**Java 示例：**

```java
@Database(entities = {User.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    public abstract UserDao userDao();

    private static volatile AppDatabase INSTANCE;
    private static final int NUMBER_OF_THREADS = 4;
    public static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    public static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, "user_database")
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
```

#### 2.3.2 关键配置

- **`@Database` 注解参数**：
  - `entities`: 数据库包含的实体类数组
  - `version`: 数据库版本号（用于迁移）
  - `exportSchema`: 是否导出数据库架构（默认 true）

- **单例模式**：
  - 整个应用只需要一个 `RoomDatabase` 实例
  - 使用双重检查锁定确保线程安全

- **后台线程执行**：
  - 提供 `ExecutorService` 用于执行数据库写操作
  - 避免在主线程执行数据库操作

- **数据库名称**：
  - 使用有意义的数据库名称
  - 数据库文件存储在应用的私有目录中

> **注意**：
> - 使用 `context.getApplicationContext()` 而不是 Activity 的 Context，避免内存泄漏
> - 不要使用 `allowMainThreadQueries()`，这会导致 ANR 风险
> - 在生产环境中，应该使用后台线程执行所有数据库操作

#### 2.3.3 数据库升级

当应用需要修改数据库结构（如添加新表、新字段、修改字段类型等）时，需要升级数据库版本。Room 使用 **Migration** 机制来处理数据库升级，确保数据不丢失。

**升级步骤：**

1. **增加版本号**：在 `@Database` 注解中将 `version` 参数增加（如从 1 改为 2）

2. **创建 Migration 对象**：定义从旧版本到新版本的迁移逻辑

3. **应用 Migration**：在构建数据库时使用 `.addMigrations()` 添加迁移规则

**示例：添加新字段**

假设需要在 `User` 实体中添加 `phone` 字段：

```java
// 1. 更新实体类，添加 phone 字段
@Entity(tableName = "users")
public class User {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String name;
    private String email;
    private int age;
    private String phone; // 新增字段
    
    // ... getter 和 setter 方法
}

// 2. 更新 Database 版本号
@Database(entities = {User.class}, version = 2, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    public abstract UserDao userDao();

    // 3. 定义 Migration（从版本 1 升级到版本 2）
    static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            // 在 users 表中添加 phone 列
            database.execSQL("ALTER TABLE users ADD COLUMN phone TEXT");
        }
    };

    public static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, "user_database")
                            .addMigrations(MIGRATION_1_2) // 4. 应用迁移
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
```

**多版本迁移示例：**

如果需要支持从多个旧版本升级，可以定义多个 Migration：

```java
@Database(entities = {User.class}, version = 3, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    // 从版本 1 升级到版本 2
    static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE users ADD COLUMN phone TEXT");
        }
    };

    // 从版本 2 升级到版本 3
    static final Migration MIGRATION_2_3 = new Migration(2, 3) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE users ADD COLUMN address TEXT");
        }
    };

    // 从版本 1 直接升级到版本 3（可选，用于处理跳版本升级）
    static final Migration MIGRATION_1_3 = new Migration(1, 3) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE users ADD COLUMN phone TEXT");
            database.execSQL("ALTER TABLE users ADD COLUMN address TEXT");
        }
    };

    public static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, "user_database")
                            .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_1_3)
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
```

**常见迁移操作：**

- **添加列**：`ALTER TABLE table_name ADD COLUMN column_name TYPE`
- **删除列**：需要创建新表，复制数据，删除旧表，重命名新表
- **修改列类型**：需要创建新表，转换数据，删除旧表，重命名新表
- **添加索引**：`CREATE INDEX index_name ON table_name(column_name)`

**注意事项：**

- ⚠️ **必须提供 Migration**：如果数据库版本增加但没有提供相应的 Migration，Room 会抛出异常并删除数据库，导致数据丢失
- ✅ **测试迁移**：在生产环境发布前，务必测试数据库迁移逻辑
- ✅ **备份数据**：对于重要数据，建议在迁移前进行备份
- ✅ **渐进式迁移**：对于复杂的结构变更，可以分多个版本逐步迁移

## 三、Room 与架构组件集成

### 3.1 Room + LiveData（Java 项目推荐）

`LiveData` 是生命周期感知的组件，非常适合与 Room 配合使用。

**优势：**
- 生命周期感知，自动管理观察者
- 自动在后台线程执行查询
- 数据变化时自动通知 UI

**示例：**

```java
// DAO
@Dao
public interface UserDao {
    @Query("SELECT * FROM users")
    LiveData<List<User>> getAllUsers();
}

// ViewModel
public class UserViewModel extends AndroidViewModel {
    private UserDao userDao;
    private LiveData<List<User>> allUsers;

    public UserViewModel(@NonNull Application application) {
        super(application);
        AppDatabase database = AppDatabase.getDatabase(application);
        userDao = database.userDao();
        allUsers = userDao.getAllUsers();
    }

    public LiveData<List<User>> getAllUsers() {
        return allUsers;
    }

    public void insert(User user) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            userDao.insert(user);
        });
    }
}

// Activity
public class MainActivity extends AppCompatActivity {
    private UserViewModel userViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        
        userViewModel.getAllUsers().observe(this, new Observer<List<User>>() {
            @Override
            public void onChanged(List<User> users) {
                // 更新 UI
                adapter.submitList(users);
            }
        });
    }
}
```

### 3.2 Room + Flow（Kotlin 项目推荐）

`Flow` 是 Kotlin 的响应式数据流，与 Room 配合使用非常方便。

**优势：**
- 响应式数据流
- 自动在后台线程执行查询
- 支持 Kotlin 协程

**示例：**

```kotlin
// DAO
@Dao
interface UserDao {
    @Query("SELECT * FROM users")
    fun getAllUsers(): Flow<List<User>>
}

// ViewModel
class UserViewModel(application: Application) : AndroidViewModel(application) {
    private val userDao = AppDatabase.getDatabase(application).userDao()
    val allUsers: Flow<List<User>> = userDao.getAllUsers()

    fun insert(user: User) {
        viewModelScope.launch {
            userDao.insert(user)
        }
    }
}
```

### 3.3 Room + ViewModel 架构模式

使用 ViewModel 封装数据库操作，实现 MVVM 架构：

```
┌─────────────┐
│   Activity  │  ← UI 层
│  Fragment   │
└──────┬──────┘
       │ 观察 LiveData
       ↓
┌─────────────┐
│  ViewModel  │  ← 业务逻辑层
└──────┬──────┘
       │ 调用 DAO
       ↓
┌─────────────┐
│     DAO     │  ← 数据访问层
└──────┬──────┘
       │ 操作数据库
       ↓
┌─────────────┐
│  Database   │  ← 数据库层
└─────────────┘
```

**优势：**
- 职责分离，代码结构清晰
- ViewModel 在配置变更时保持数据
- 便于单元测试
- 符合 Android 架构指南

## 四、最佳实践

### 4.1 线程管理

1. **查询操作**：
   - 使用 `LiveData` 或 `Flow` 作为返回值，Room 自动在后台线程执行
   - 避免在主线程直接查询数据库

2. **写操作**：
   - 必须在后台线程执行
   - 使用 `ExecutorService` 或协程执行写操作
   - 不要使用 `allowMainThreadQueries()`

3. **示例：**

```java
// ✅ 正确：使用 ExecutorService
public void insert(User user) {
    AppDatabase.databaseWriteExecutor.execute(() -> {
        userDao.insert(user);
    });
}

// ❌ 错误：在主线程执行
public void insert(User user) {
    userDao.insert(user); // 会导致异常
}
```

### 4.2 数据库版本管理

1. **版本号管理**：
   - 每次修改数据库结构时，必须增加版本号
   - 使用 Migration 处理版本升级

2. **数据库迁移**：

```java
@Database(entities = {User.class}, version = 2, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    // Migration 示例
    static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE users ADD COLUMN phone TEXT");
        }
    };

    public static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, "user_database")
                            .addMigrations(MIGRATION_1_2)
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
```

### 4.3 性能优化

1. **使用索引**：
   ```java
   @Entity(tableName = "users", indices = {@Index("email")})
   public class User {
       // ...
   }
   ```

2. **批量操作**：
   ```java
   @Insert
   void insertAll(User... users);
   ```

3. **使用事务**：
   ```java
   @Transaction
   @Query("SELECT * FROM users")
   LiveData<List<User>> getAllUsersWithRelations();
   ```

### 4.4 错误处理

1. **捕获异常**：
   ```java
   try {
       AppDatabase.databaseWriteExecutor.execute(() -> {
           userDao.insert(user);
       });
   } catch (Exception e) {
       // 处理错误
       Log.e(TAG, "Error inserting user", e);
   }
   ```

2. **验证数据**：
   - 在插入数据前验证数据有效性
   - 使用 Room 的编译时检查发现 SQL 错误

## 五、常见问题

### 5.1 为什么不能跳过 Database 直接使用 DAO？

这是一个关于 Room 架构设计的核心问题。要理解这一点，需要从技术实现和架构设计两个角度来看：

(1) 技术实现角度

1. **DAO 只是接口，没有实现**：
   ```java
   @Dao
   public interface UserDao {  // 注意：这是 interface，不是 class
       @Insert
       void insert(User user);
   }
   ```
   DAO 只是一个接口定义，它本身没有任何实现代码。Room 的注解处理器会在编译时**自动生成** DAO 的实现类（如 `UserDao_Impl`）。

2. **Database 是生成的实现类的容器**：
   ```java
   @Database(entities = {User.class}, version = 1)
   public abstract class AppDatabase extends RoomDatabase {
       public abstract UserDao userDao();  // Room 会生成这个方法的具体实现
   }
   ```
   Room 在编译时会生成 `AppDatabase_Impl` 类，这个类包含了：
   - 数据库连接管理
   - DAO 实现类的实例（如 `UserDao_Impl`）
   - `userDao()` 方法的具体实现，返回 DAO 实例

3. **为什么必须通过 Database 获取**：
   - DAO 的实现类需要访问数据库连接（SQLiteDatabase）
   - 数据库连接由 Database 类管理和维护
   - 只有通过 Database 获取 DAO，才能确保 DAO 使用的是正确的数据库连接
   - 如果直接创建 DAO 实例，它无法获取数据库连接，无法执行操作

(2) 架构设计角度

1. **单一职责原则**：
   - **Database**：负责数据库的创建、连接管理、版本控制、迁移等
   - **DAO**：只负责定义数据访问操作，不关心数据库如何创建和管理

2. **依赖注入**：
   - Database 作为"工厂"，负责创建和提供 DAO 实例
   - DAO 通过 Database 获取，自动获得正确的数据库连接
   - 这种设计模式确保了依赖关系的正确性

3. **解耦和封装**：
   - 开发者只需要定义接口（DAO），不需要关心实现细节
   - Database 封装了所有底层复杂性（SQLite 连接、线程管理、事务处理等）
   - 通过 Database 获取 DAO，实现了接口与实现的分离

(3) 总结

**DAO 必须通过 Database 获取，因为：**
1. DAO 是接口，其实现由 Room 在编译时生成，需要数据库连接才能工作
2. Database 负责管理数据库连接，只有它能提供正确的连接给 DAO
3. 这种设计实现了职责分离，让代码更清晰、更易维护
4. 符合依赖注入和单一职责原则，是良好的架构设计

### 5.2 为什么 Room 不允许在主线程访问数据库？

数据库操作（特别是写操作）可能耗时较长，如果在主线程执行会导致应用无响应（ANR）。Room 默认禁止在主线程访问数据库，强制开发者使用后台线程，从而避免 ANR 问题。



### 5.3 LiveData 和 Flow 有什么区别？

- **LiveData**：
  - Java 项目推荐使用
  - 生命周期感知
  - 简单易用

- **Flow**：
  - Kotlin 项目推荐使用
  - 响应式数据流
  - 支持更复杂的操作（map、filter 等）

### 5.4 如何处理数据库迁移？

1. 增加数据库版本号
2. 创建 Migration 对象
3. 在 Database 构建时添加 Migration

```java
static final Migration MIGRATION_1_2 = new Migration(1, 2) {
    @Override
    public void migrate(SupportSQLiteDatabase database) {
        // 执行迁移 SQL
        database.execSQL("ALTER TABLE users ADD COLUMN phone TEXT");
    }
};

Room.databaseBuilder(context, AppDatabase.class, "database_name")
    .addMigrations(MIGRATION_1_2)
    .build();
```

### 5.5 如何调试 Room 数据库？

1. **查看生成的 SQL**：
   - 在 Database 构建时添加 `.setQueryCallback()`

2. **导出数据库架构**：
   - 设置 `exportSchema = true`
   - 查看生成的 JSON 文件

3. **使用数据库查看工具**：
   - Android Studio 的 Database Inspector
   - 第三方工具如 DB Browser for SQLite



## 参考资料

- [使用 Room 将数据保存到本地数据库](https://developer.android.google.cn/training/data-storage/room?hl=zh-cn)
- [androidx.room 官方文档](https://developer.android.google.cn/reference/androidx/room/package-summary?hl=zh-cn)
- [Room 实体定义](https://developer.android.google.cn/training/data-storage/room/defining-data?hl=zh-cn)
- [Room 数据访问对象](https://developer.android.google.cn/training/data-storage/room/accessing-data?hl=zh-cn)
- [Room 数据库迁移](https://developer.android.google.cn/training/data-storage/room/migrating-db-versions?hl=zh-cn)
- [AndroidX 版本页面](https://developer.android.google.cn/jetpack/androidx/versions?hl=zh-cn)
