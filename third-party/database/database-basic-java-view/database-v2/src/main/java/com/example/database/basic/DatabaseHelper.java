package com.example.database.basic;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper  extends SQLiteOpenHelper {
    private static final String DB_NAME = "student.db";
    private static final int DATABASE_VERSION = 2; // 版本号从1升级到2

    // 自带的构造方法
    public DatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    // 自定义构造方法，简化创建过程
    public DatabaseHelper(Context context) {
        this(context, DB_NAME, null, DATABASE_VERSION);
    }

    // 版本变更时的构造方法
    public DatabaseHelper(Context context, int version) {
        this(context, DB_NAME, null, version);
    }

    // 当数据库首次创建时调用
    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d("DatabaseHelper", "Creating database v2");

        // 创建学生表
        String createStudentTable = "CREATE TABLE student (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name VARCHAR(20) NOT NULL, " +
                "age INTEGER, " +
                "created_time DATETIME DEFAULT CURRENT_TIMESTAMP" + // 新增字段：创建时间
                ")";
        db.execSQL(createStudentTable);

        // 创建班级表（新增表）
        String createClassTable = "CREATE TABLE class (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "class_name VARCHAR(50) NOT NULL, " +
                "teacher VARCHAR(50)" +
                ")";
        db.execSQL(createClassTable);

        // 插入初始数据
        insertSampleData(db);
    }

    // 当数据库版本更新时调用
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // 使用循环实现跨版本升级，确保从任何旧版本都能正确升级到最新版本
        for (int version = oldVersion; version < newVersion; version++) {
            switch (version) {
                case 1:
                    upgradeFromVersion1To2(db);
                    break;
                // 未来可以继续添加更多版本升级逻辑
                // case 2:
                //     upgradeFromVersion2To3(db);
                //     break;
                default:
                    break;
            }
        }
    }

    /**
     * 从版本1升级到版本2
     * 升级内容：
     * 1. 为学生表添加邮箱字段
     * 2. 创建班级表
     * 3. 为学生表添加创建时间字段
     */
    private void upgradeFromVersion1To2(SQLiteDatabase db) {
        Log.d("DatabaseHelper", "Upgrading from version 1 to 2");
        try {
            // 开始事务，确保升级操作的原子性
            db.beginTransaction();

            // 1. 为学生表添加邮箱字段
            db.execSQL("ALTER TABLE student ADD COLUMN email VARCHAR(50)");

            // 2. 创建班级表
            String createClassTable = "CREATE TABLE IF NOT EXISTS class (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "class_name VARCHAR(50) NOT NULL, " +
                    "teacher VARCHAR(50)" +
                    ")";
            db.execSQL(createClassTable);

            // 3. 由于SQLite不支持直接添加DEFAULT值的列，需要重建表来添加创建时间字段
            upgradeTableWithDefaultValue(db);

            // 事务成功完成
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
            // 事务失败会自动回滚
        } finally {
            db.endTransaction();
        }
    }

    /**
     * 通过重建表的方式添加带有默认值的字段
     */
    private void upgradeTableWithDefaultValue(SQLiteDatabase db) {
        // 创建临时表
        db.execSQL("CREATE TEMPORARY TABLE student_backup(id, name, age, email)");

        // 将数据从原表复制到临时表
        db.execSQL("INSERT INTO student_backup SELECT id, name, age, email FROM student");

        // 删除原表
        db.execSQL("DROP TABLE student");

        // 创建新表（包含created_time字段）
        String createStudentTable = "CREATE TABLE student (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name VARCHAR(20) NOT NULL, " +
                "age INTEGER, " +
                "email VARCHAR(50), " +
                "created_time DATETIME DEFAULT CURRENT_TIMESTAMP" +
                ")";
        db.execSQL(createStudentTable);

        // 将数据从临时表复制回新表
        db.execSQL("INSERT INTO student (id, name, age, email) SELECT id, name, age, email FROM student_backup");

        // 删除临时表
        db.execSQL("DROP TABLE student_backup");
    }

    /**
     * 插入示例数据
     */
    private void insertSampleData(SQLiteDatabase db) {
        // 插入班级数据
        db.execSQL("INSERT INTO class (class_name, teacher) VALUES ('计算机科学1班', '张老师')");
        db.execSQL("INSERT INTO class (class_name, teacher) VALUES ('软件工程2班', '李老师')");

        // 插入学生数据
        db.execSQL("INSERT INTO student (name, age, email) VALUES ('张三', 20, 'zhangsan@example.com')");
        db.execSQL("INSERT INTO student (name, age, email) VALUES ('李四', 22, 'lisi@example.com')");
        db.execSQL("INSERT INTO student (name, age, email) VALUES ('王五', 21, 'wangwu@example.com')");
    }

    /**
     * 数据库降级处理（通常用于开发测试）
     */
    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // 简单的降级策略：删除所有表并重新创建
        db.execSQL("DROP TABLE IF EXISTS student");
        db.execSQL("DROP TABLE IF EXISTS class");
        onCreate(db);
    }
}
