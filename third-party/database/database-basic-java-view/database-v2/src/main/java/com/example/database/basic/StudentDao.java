package com.example.database.basic;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

public class StudentDao {
    private DatabaseHelper dbHelper;

    public StudentDao(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    /**
     * 插入学生信息（包含邮箱字段）
     */
    public long insert(Student student) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        String sql = "INSERT INTO student (name, age, email) VALUES (?, ?, ?)";
        android.database.sqlite.SQLiteStatement statement = db.compileStatement(sql);
        statement.bindString(1, student.getName());
        statement.bindLong(2, student.getAge());
        statement.bindString(3, student.getEmail());

        long result = statement.executeInsert();
        db.close();
        return result;
    }

    /**
     * 根据ID删除学生信息
     */
    public int delete(int id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        String sql = "DELETE FROM student WHERE id = ?";
        android.database.sqlite.SQLiteStatement statement = db.compileStatement(sql);
        statement.bindLong(1, id);

        int result = statement.executeUpdateDelete();
        db.close();
        return result;
    }

    /**
     * 更新学生信息（包含邮箱字段）
     */
    public int update(Student student) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        String sql = "UPDATE student SET name = ?, age = ?, email = ? WHERE id = ?";
        android.database.sqlite.SQLiteStatement statement = db.compileStatement(sql);
        statement.bindString(1, student.getName());
        statement.bindLong(2, student.getAge());
        statement.bindString(3, student.getEmail());
        statement.bindLong(4, student.getId());

        int result = statement.executeUpdateDelete();
        db.close();
        return result;
    }

    /**
     * 查询所有学生信息（包含邮箱和创建时间字段）
     */
    public List<Student> queryAll() {
        List<Student> studentList = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String sql = "SELECT * FROM student ORDER BY id ASC";
        android.database.Cursor cursor = db.rawQuery(sql, null);

        while (cursor.moveToNext()) {
            Student student = new Student();
            student.setId(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
            student.setName(cursor.getString(cursor.getColumnIndexOrThrow("name")));
            student.setAge(cursor.getInt(cursor.getColumnIndexOrThrow("age")));
            student.setEmail(cursor.getString(cursor.getColumnIndexOrThrow("email")));
            student.setCreatedTime(cursor.getString(cursor.getColumnIndexOrThrow("created_time")));

            studentList.add(student);
        }

        cursor.close();
        db.close();
        return studentList;
    }

    /**
     * 根据ID查询学生信息
     */
    public Student queryById(int id) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String sql = "SELECT * FROM student WHERE id = ?";
        String[] selectionArgs = {String.valueOf(id)};

        android.database.Cursor cursor = db.rawQuery(sql, selectionArgs);
        Student student = null;

        if (cursor.moveToFirst()) {
            student = new Student();
            student.setId(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
            student.setName(cursor.getString(cursor.getColumnIndexOrThrow("name")));
            student.setAge(cursor.getInt(cursor.getColumnIndexOrThrow("age")));
            student.setEmail(cursor.getString(cursor.getColumnIndexOrThrow("email")));
            student.setCreatedTime(cursor.getString(cursor.getColumnIndexOrThrow("created_time")));
        }

        cursor.close();
        db.close();
        return student;
    }

    /**
     * 根据邮箱查询学生信息（新增功能）
     */
    public List<Student> queryByEmail(String email) {
        List<Student> studentList = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String sql = "SELECT * FROM student WHERE email LIKE ? ORDER BY id ASC";
        String[] selectionArgs = {"%" + email + "%"};

        android.database.Cursor cursor = db.rawQuery(sql, selectionArgs);

        while (cursor.moveToNext()) {
            Student student = new Student();
            student.setId(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
            student.setName(cursor.getString(cursor.getColumnIndexOrThrow("name")));
            student.setAge(cursor.getInt(cursor.getColumnIndexOrThrow("age")));
            student.setEmail(cursor.getString(cursor.getColumnIndexOrThrow("email")));
            student.setCreatedTime(cursor.getString(cursor.getColumnIndexOrThrow("created_time")));

            studentList.add(student);
        }

        cursor.close();
        db.close();
        return studentList;
    }

    /**
     * 根据年龄范围查询学生信息
     */
    public List<Student> queryByAgeRange(int minAge, int maxAge) {
        List<Student> studentList = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String sql = "SELECT * FROM student WHERE age BETWEEN ? AND ? ORDER BY age ASC";
        String[] selectionArgs = {String.valueOf(minAge), String.valueOf(maxAge)};

        android.database.Cursor cursor = db.rawQuery(sql, selectionArgs);

        while (cursor.moveToNext()) {
            Student student = new Student();
            student.setId(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
            student.setName(cursor.getString(cursor.getColumnIndexOrThrow("name")));
            student.setAge(cursor.getInt(cursor.getColumnIndexOrThrow("age")));
            student.setEmail(cursor.getString(cursor.getColumnIndexOrThrow("email")));
            student.setCreatedTime(cursor.getString(cursor.getColumnIndexOrThrow("created_time")));

            studentList.add(student);
        }

        cursor.close();
        db.close();
        return studentList;
    }

    /**
     * 获取学生总数
     */
    public int getStudentCount() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String sql = "SELECT COUNT(*) FROM student";
        android.database.Cursor cursor = db.rawQuery(sql, null);

        int count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }

        cursor.close();
        db.close();
        return count;
    }

    /**
     * 清空所有学生数据
     */
    public int clearAll() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int result = db.delete("student", null, null);
        db.close();
        return result;
    }
}
