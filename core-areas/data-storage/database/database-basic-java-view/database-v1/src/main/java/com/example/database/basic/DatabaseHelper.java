package com.example.database.basic;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "student.db";
    private static final int VERSION = 1;

    // 自带的构造方法
    public DatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    // 自定义构造方法，简化创建过程
    public DatabaseHelper(Context context) {
        this(context, DB_NAME, null, VERSION);
    }

    // 版本变更时的构造方法
    public DatabaseHelper(Context context, int version) {
        this(context, DB_NAME, null, version);
    }

    // 当数据库首次创建时调用
    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d("DatabaseHelper", "Creating database v1");

        String sql = "create table student(" +
                "id integer primary key autoincrement," +
                "name varchar(20)," +
                "age integer)";
        db.execSQL(sql);
    }

    // 当数据库版本更新时调用
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String sql = "drop table if exists student";
        db.execSQL(sql);
        onCreate(db);
    }
}
