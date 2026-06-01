## 一、SharedPreferences 概述

### 1.1 什么是 SharedPreferences 

SharedPreferences 以**键值对（Key-Value）**的形式存储数据。它支持存储几种基本数据类型：`Boolean`, `Int`, `Float`, `Long`, `String`以及 `Set<String>`

它通常用于保存一些简单的数据，例如用户的设置选项、应用的自定义配置，或者实现类似“记住密码”的功能。**所有数据会以 XML 文件的形式保存在设备的 `/data/data/<应用程序包名>/shared_prefs/`目录**下，当应用被卸载时，这些文件也会随之被清除。

> **注意**：虽然 SharedPreferences 非常方便，但它是为存储少量简单数据设计的。对于大量或复杂结构的数据，建议考虑使用 Room 数据库等方案。此外，虽然官方提供了 `apply()`和 `commit()`两种提交数据的方式，但更推荐使用 `apply()`，因为它是异步的，不会阻塞主线程





## 二、SharedPreferences 概述

使用 SharedPreferences 的基本流程是：获取实例 -> 读写数据。

### 2.1 获取 SharedPreferences  对象

有三种常见的方式来获取 SharedPreferences 对象：

1. **`Context` 类的 `getSharedPreferences()`(最常用)**：当你需要多个不同名称的配置文件时使用此方法。

   ```java
   // 参数1: 配置文件名，参数2: 模式（通常使用 MODE_PRIVATE，表示仅本应用可访问）
   SharedPreferences sharedPref = getSharedPreferences("my_preferences", Context.MODE_PRIVATE);
   ```

2. **`Activity` 类的 `getPreferences()`**：在 Activity 内部使用，如果该 Activity 只需要一个 SharedPreferences 文件，可以省略文件名。

   ```java
   // 系统会自动以当前 Activity 的类名作为文件名
   SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
   ```

3. **`PreferenceManager.getDefaultSharedPreferences()`**：获取默认的、以应用包名为前缀的 SharedPreferences 文件，通常用于存储整个应用的通用设置。



### 2.2 修改数据

**修改数据（写入/删除/清空）**需要通过 `SharedPreferences.Editor`对象来完成。

1. 通过 `edit()`方法获取 `Editor`对象。
2. 使用 `put`系列方法（如 `putString`, `putInt`, `putBoolean`）添加键值对，使用 `remove()`方法删除特定的键值对，或者使用 `clear()`方法可以一次性清空整个文件的数据。
3. 调用 `apply()`方法（推荐，异步）或 `commit()`方法（同步，有返回值）提交更改。

```java
// 1. 获取 Editor 对象
SharedPreferences.Editor editor = sharedPref.edit();

// 2. 添加或者删除键值对
editor.putString("username", "张三");
editor.putInt("user_age", 25);
editor.putBoolean("is_logged_in", true);
editor.remove("old_data");

// 3. 推荐使用 apply()
editor.apply();
```





### 2.3 读取数据

读取数据相对直接，无需 `Editor`。

- 获取特定值 `getString()`、`getInt()` 
- **检查某个键是否存在 ** `contains("key")`
- **获取所有键值对**   `getAll()`

```java
// 参数1: 键名，参数2: 如果键不存在时使用的默认值
String username = sharedPref.getString("username", "");
int age = sharedPref.getInt("user_age", 0);
boolean isLoggedIn = sharedPref.getBoolean("is_logged_in", false);
```





## 三、最佳实践

- **文件命名**：为 SharedPreferences 文件命名时，建议使用能唯一标识你应用的名称，例如加上应用ID作为前缀。

- **模式选择**：现在只应使用 `MODE_PRIVATE`，其他模式（如 `MODE_WORLD_READABLE`）已被废弃，在高版本系统上使用会抛出异常。

- **现代替代方案**：官方推荐在新的项目中考虑使用 **DataStore** 作为 SharedPreferences 的现代化替代方案。DataStore 基于 Kotlin 协程和 Flow 构建，克服了 SharedPreferences 的一些缺点

  。