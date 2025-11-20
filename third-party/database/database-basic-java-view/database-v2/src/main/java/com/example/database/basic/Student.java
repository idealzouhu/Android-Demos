package com.example.database.basic;

public class Student {
    private int id;
    private String name;
    private int age;
    private String email;        // 新增邮箱字段
    private String createdTime;  // 新增创建时间字段

    // 构造方法
    public Student() {}

    public Student(String name, int age) {
        this.name = name;
        this.age = age;
        this.email = "v2@test.com";
    }

    public Student(String name, int age, String email) {
        this.name = name;
        this.age = age;
        this.email = email;
    }

    // Getter 和 Setter 方法
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getCreatedTime() { return createdTime; }
    public void setCreatedTime(String createdTime) { this.createdTime = createdTime; }

    @Override
    public String toString() {
        return "ID: " + id + ", 姓名: " + name + ", 年龄: " + age +
                ", 邮箱: " + email + ", 创建时间: " + createdTime;
    }
}
