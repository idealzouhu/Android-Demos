package com.example.database.basic;

import androidx.annotation.NonNull;

public class Student {
    private int id;
    private String name;
    private int age;

    public Student() {}

    public Student(String name, int age) {
        this.name = name;
        this.age = age;
    }

    // Getter 和 Setter 方法
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }

    @NonNull
    @Override
    public String toString() {
        return "ID: " + id + ", 姓名: " + name + ", 年龄: " + age;
    }
}
