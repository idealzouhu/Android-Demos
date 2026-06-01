package com.example.contentprovider.contacts.model;

import androidx.annotation.NonNull;

/**
 * 联系人数据模型
 */
public class Contact {
    private long id;
    private String name;
    private String phone;

    public Contact(long id, String name, String phone) {
        this.id = id;
        this.name = name;
        this.phone = phone;
    }

    public Contact(String name, String phone) {
        this(-1, name, phone);
    }

    // Getters
    public long getId() { return id; }
    public String getName() { return name; }
    public String getPhone() { return phone; }

    public String getAvatarText() {
        if (name != null && !name.isEmpty()) {
            return name.substring(0, 1).toUpperCase();
        }
        return "?";
    }

    @NonNull
    @Override
    public String toString() {
        return "Contact{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", phone='" + phone + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Contact contact = (Contact) o;
        return id == contact.id;
    }

    @Override
    public int hashCode() {
        return Long.hashCode(id);
    }
}
