package com.example.database.basic;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

public class StudentDao {
    private DatabaseHelper dbHelper;

    public StudentDao(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    // 插入数据
    public long insert(Student student) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", student.getName());
        values.put("age", student.getAge());
        long result = db.insert("student", null, values);
        db.close();
        return result;
    }

    // 删除数据
    public int delete(int id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int result = db.delete("student", "id=?", new String[]{String.valueOf(id)});
        db.close();
        return result;
    }

    // 更新数据
    public int update(Student student) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", student.getName());
        values.put("age", student.getAge());
        int result = db.update("student", values, "id=?",
                new String[]{String.valueOf(student.getId())});
        db.close();
        return result;
    }

    // 查询所有数据
    public List<Student> queryAll() {
        List<Student> list = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query("student", null, null, null, null, null, "id asc");

        while (cursor.moveToNext()) {
            Student student = new Student();
            student.setId(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
            student.setName(cursor.getString(cursor.getColumnIndexOrThrow("name")));
            student.setAge(cursor.getInt(cursor.getColumnIndexOrThrow("age")));
            list.add(student);
        }

        cursor.close();
        db.close();
        return list;
    }

    // 根据ID查询数据
    public Student queryById(int id) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query("student", null, "id=?",
                new String[]{String.valueOf(id)}, null, null, null);

        Student student = null;
        if (cursor.moveToFirst()) {
            student = new Student();
            student.setId(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
            student.setName(cursor.getString(cursor.getColumnIndexOrThrow("name")));
            student.setAge(cursor.getInt(cursor.getColumnIndexOrThrow("age")));
        }

        cursor.close();
        db.close();
        return student;
    }
}
