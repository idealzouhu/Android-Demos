package com.example.multi.model;

public class User implements java.io.Serializable {

    private String name;
    private String email;
    private int age;

    public User(String name, String email, int age) {
        this.name = name;
        this.email = email;
        this.age = age;
    }

    // getter方法
    public String getName() { return name; }
    public String getEmail() { return email; }
    public int getAge() { return age; }
}
