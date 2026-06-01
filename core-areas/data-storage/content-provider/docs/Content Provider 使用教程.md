## 一、ContentProvider 概述

ContentProvider 是 Android 四大组件之一，主要用于在不同应用之间安全地共享数据。它提供了一套标准化的接口，**可以对数据进行增删改查操作，而使用者无需关心数据的具体存储方式**（可以是 SQLite 数据库、文件、甚至网络数据）。

### 1.1 ContentProvider 的核心概念

#### 1.1.1 **URI（统一资源标识符）**

URI（统一资源标识符）是 ContentProvider 的核心。每个数据资源都有一个唯一的 URI 来标识。标准格式为：`content://<authority>/<path>[/<id>]`

- `content://`： 固定模式。
- `<authority>`： ContentProvider 的唯一标识，通常在 AndroidManifest.xml 中注册时定义，一般是应用的包名加上.provider。
- `<path>`： 用来指定数据的类型或表名。例如 `user`， `book`。
- `<id>`： （可选）指定某条记录的唯一 ID。

同时，URI 支持通配符 `*` 表示匹配任意长度的任意字符、\#表示匹配任意长度的数字。

例如：

```
# 调用方期望访问的是com.example.app这个应用的table1表中id为1的数据
content://com.example.app.provider/table1/1

# 匹配任意表
content://com.example.app.provider/*

# 匹配table1表中任意一行数据的内容
content://com.example.app.provider/table1/#
```







#### 1.1.2 MIME 类型

URI所对应的MIME字符串主要由3部分组成，Android 对这3个部分做了如下格式规定。

- 必须以 vnd 开头。

- 如果内容 URI 以路径结尾，则后接 `android.cursor.dir/` ；如果内容 URI 以id结尾，则后接`android.cursor.item/`。

- 最后接上 `vnd.<authority>.<path>`。



`ContentProvider` 提供了 `getType()`  函数，可以根据传入的内容 URI 返回相应的 MIME 类型，让客户端知道 URI 对应的数据格式。例如在Intent解析、数据传输等场景中需要正确的MIME类型。





#### 1.1.3 数据模型

ContentProvider 将数据以类似数据库表的形式呈现，每一行是一个记录，每一列是一个字段。





### 1.2 如何保护隐私数据

因为所有的增删改查操作都一定要匹配到相应的内容URI格式才能进行，而我们当然不可能向 UriMatcher 中

添加隐私数据的 URI，所以这部分数据根本无法被外部程序访问，安全问题也就不存在了





## 二、ContentProvider 的使用方法

ContentProvider 的用途主要有：

- 使用现有的 ContentProvider 读取和操作相应程序中的数据
- 创建自定义的 ContentProvider，给程序的数据提供外部访问接口



### 2.1 工具类

#### 1.1.2 ContentResolver

应用不直接访问 ContentProvider，而是通过 ContentResolver 对象来进行交互。

你的应用通过 `getContentResolver()`获取此对象，然后使用它提供的方法与 ContentProvider 交互。



#### 1.1.3 UriMatcher

 在自定义 Provider 中， UriMatcher 用于解析 URI 的意图。



即 UriMatcher 只有在自定义 ContentProvider 的情景下才会使用。





### 2.1 使用 ContentProvider

#### 2.1.1 具体步骤

具体步骤为：

1. 声明权限：例如，由于访问系统联系人是敏感操作，必须在 `AndroidManifest.xml`文件中声明相应的权限。

2. 获取 ContentResolver：在你的 Activity 或 Fragment 中，通过 `getContentResolver()`获取 ContentResolver 对象。

   ```
   ContentResolver contentResolver = getContentResolver();
   ```

3. 构建查询 URI： 需要知道要访问的 ContentProvider 的 URI。系统联系人的常用 URI 是 `ContactsContract.Contacts.CONTENT_URI`。

   ```java
   Uri contactUri = ContactsContract.Contacts.CONTENT_URI;
   ```

4. 使用 ContentResolver 进行查询：使用 `query()`方法，参数与 SQLiteDatabase 的 query 方法类似。

   ```java
   // 要查询的列
   String[] projection = {
           ContactsContract.Contacts._ID,
           ContactsContract.Contacts.DISPLAY_NAME
   };
   
   // 查询条件（可选，null 表示查询所有）
   String selection = null;
   String[] selectionArgs = null;
   
   // 排序方式（可选）
   String sortOrder = null;
   
   // 执行查询
   Cursor cursor = contentResolver.query(
           contactUri,    // URI
           projection,    // 要返回的列
           selection,     // WHERE 条件
           selectionArgs, // WHERE 条件的参数
           sortOrder      // ORDER BY
   );
   ```

5. 处理返回的 Cursor： 查询结果通过 Cursor 对象返回，你需要遍历它来获取数据。

   ```java
   if (cursor != null && cursor.moveToFirst()) {
       do {
           // 获取列索引
           int idIndex = cursor.getColumnIndex(ContactsContract.Contacts._ID);
           int nameIndex = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
   
           // 通过列索引获取数据
           String contactId = cursor.getString(idIndex);
           String displayName = cursor.getString(nameIndex);
   
           // 处理数据，例如显示在 Log 或 UI 上
           Log.d("Contacts", "ID: " + contactId + ", Name: " + displayName);
   
       } while (cursor.moveToNext()); // 移动到下一行
   
       cursor.close(); // 非常重要！使用完毕后必须关闭 Cursor
   }
   ```

   



#### 2.1.2 CRUD 方法

不同于SQLiteDatabase，ContentResolver中的增删改查方法都是不接收表名参数的，而是使用一个Uri参数代替





### 2.2 自定义 ContentProvider

#### 2.2.1 具体步骤

1. 创建继承自 ContentProvider 的类

   你需要实现六个抽象方法：

   - `onCreate()`: 初始化提供者，在这里可以初始化数据库等。
   - `query(Uri, String[], String, String[], String)`
   - `insert(Uri, ContentValues)`
   - `update(Uri, ContentValues, String, String[])`
   - `delete(Uri, String, String[])`
   - `getType(Uri)`: 返回给定 URI 的 MIME 类型。

2. 定义 Contract 类（最佳实践）：定义一个 Contract 类来集中管理 URI、MIME 类型和列名等常量。

   ```
   public final class MyBookContract {
       // Authority，通常为你的包名
       public static final String AUTHORITY = "com.example.myapp.provider";
       // 基础 URI
       public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/books");
   
       // MIME 类型常量
       public static final String CONTENT_LIST_TYPE = "vnd.android.cursor.dir/vnd.com.example.provider.books";
       public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.com.example.provider.books";
   
       // 表名和列名
       public static final String TABLE_BOOKS = "books";
       public static final String COLUMN_ID = "_id";
       public static final String COLUMN_TITLE = "title";
       public static final String COLUMN_AUTHOR = "author";
   }
   ```

3. 实现 UriMatcher： UriMatcher 帮助你解析传入的 URI，以确定客户端请求的是单个项目还是整个列表。

   ```java
   public class MyBookProvider extends ContentProvider {
       private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
   
       // 定义匹配代码
       private static final int BOOKS = 100; // 操作整个books表
       private static final int BOOK_ID = 101; // 操作单个book
   
       static {
           // 对于整个表的URI：content://com.example.myapp.provider/books
           sUriMatcher.addURI(MyBookContract.AUTHORITY, "books", BOOKS);
           // 对于单条记录的URI：content://com.example.myapp.provider/books/5
           sUriMatcher.addURI(MyBookContract.AUTHORITY, "books/#", BOOK_ID);
       }
   
       private MyDatabaseHelper mDbHelper; // 你的数据库帮助类
   
       @Override
       public boolean onCreate() {
           mDbHelper = new MyDatabaseHelper(getContext());
           return true;
       }
   
       @Override
       public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
           SQLiteDatabase db = mDbHelper.getReadableDatabase();
           Cursor cursor;
           // 根据 URI 匹配决定查询范围
           switch (sUriMatcher.match(uri)) {
               case BOOKS:
                   // 查询整个表
                   cursor = db.query(MyBookContract.TABLE_BOOKS, projection, selection, selectionArgs, null, null, sortOrder);
                   break;
               case BOOK_ID:
                   // 从 URI 中提取 ID，并添加到查询条件中
                   selection = MyBookContract.COLUMN_ID + "=?";
                   selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                   cursor = db.query(MyBookContract.TABLE_BOOKS, projection, selection, selectionArgs, null, null, sortOrder);
                   break;
               default:
                   throw new IllegalArgumentException("Unknown URI: " + uri);
           }
           // 设置通知 URI，当数据改变时，Cursor 会自动更新（如果使用了 Loader）
           cursor.setNotificationUri(getContext().getContentResolver(), uri);
           return cursor;
       }
   
       // ... 实现 insert, update, delete, getType 方法，逻辑类似
       @Override
       public String getType(Uri uri) {
           switch (sUriMatcher.match(uri)) {
               case BOOKS:
                   return MyBookContract.CONTENT_LIST_TYPE;
               case BOOK_ID:
                   return MyBookContract.CONTENT_ITEM_TYPE;
               default:
                   throw new IllegalArgumentException("Unknown URI: " + uri);
           }
       }
   }
   ```

4.  在 AndroidManifest.xml 中注册

      ```
   <application ...>
          <provider
              android:name=".MyBookProvider"
              android:authorities="com.example.myapp.provider"
              android:enabled="true"
              android:exported="true" /> <!-- exported="true" 允许其他应用访问 -->
      </application>
     ```
   
      

   

## 三、数据同步机制



```
cursor.setNotificationUri(getContext().getContentResolver(), uri);
```

主要观察者类型

- CursorLoader: 最常见的观察者，用于在 Activity/Fragment 中自动加载和更新数据
- ContentObserver: 自定义的内容观察者，可以监听特定 URI 的数据变化
- Cursor: 查询返回的游标对象，通过 setNotificationUri 注册为观察者