package com.example.contentprovider.contacts.provider;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;

import com.example.contentprovider.contacts.model.Contact;

import java.util.ArrayList;
import java.util.List;

/**
 * 联系人数据提供者类
 * 封装所有与 ContentProvider 交互的逻辑
 */
public class ContactsProvider {

    /**
     * 内容解析器实例，用于访问和操作内容提供者数据
     * 通过ContentResolver可以与系统或其他应用的内容提供者进行交互，
     * 执行查询、插入、更新、删除等数据操作
     */
    private final ContentResolver contentResolver;

    public ContactsProvider(Context context) {
        this.contentResolver = context.getContentResolver();
    }

    /**
     * 读取所有联系人信息
     *
     * @return 联系人列表
     */
    public List<Contact> getAllContacts() throws ContactsReadException {
        List<Contact> contacts = new ArrayList<>();
        Cursor cursor = null;

        try {
            // 查询联系人数据
            cursor = contentResolver.query(
                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    getProjection(),
                    null,
                    null,
                    ContactsContract.Contacts.DISPLAY_NAME + " ASC"
            );

            if (cursor != null) {
                while (cursor.moveToNext()) {
                    Contact contact = mapCursorToContact(cursor);
                    if (contact != null) {
                        contacts.add(contact);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new ContactsReadException("读取联系人数据失败", e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return contacts;
    }

    /**
     * 根据姓名搜索联系人
     *
     * @param name 搜索关键词
     * @return 匹配的联系人列表
     */
    public List<Contact> searchContacts(String name) throws ContactsReadException {
        List<Contact> contacts = new ArrayList<>();
        Cursor cursor = null;

        try {
            String selection = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " LIKE ?";
            String[] selectionArgs = new String[]{"%" + name + "%"};

            cursor = contentResolver.query(
                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    getProjection(),
                    selection,
                    selectionArgs,
                    ContactsContract.Contacts.DISPLAY_NAME + " ASC"
            );

            if (cursor != null) {
                while (cursor.moveToNext()) {
                    Contact contact = mapCursorToContact(cursor);
                    if (contact != null) {
                        contacts.add(contact);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new ContactsReadException("搜索联系人失败", e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return contacts;
    }

    /**
     * 获取联系人数量
     * @return 联系人总数
     */
    public int getContactsCount() {
        Cursor cursor = null;
        try {
            cursor = contentResolver.query(
                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    new String[]{"COUNT(*)"},
                    null, null, null
            );

            if (cursor != null && cursor.moveToFirst()) {
                return cursor.getInt(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return 0;
    }

    /**
     * 定义查询的列
     */
    private String[] getProjection() {
        return new String[]{
                ContactsContract.CommonDataKinds.Phone._ID,
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Phone.NUMBER,
                ContactsContract.CommonDataKinds.Phone.CONTACT_ID
        };
    }

    /**
     * 将 Cursor 数据映射为 Contact 对象
     */
    private Contact mapCursorToContact(Cursor cursor) {
        try {
            String name = cursor.getString(cursor.getColumnIndexOrThrow(
                    ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));

            String phone = cursor.getString(cursor.getColumnIndexOrThrow(
                    ContactsContract.CommonDataKinds.Phone.NUMBER));

            long contactId = cursor.getLong(cursor.getColumnIndexOrThrow(
                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID));

            // 清理电话号码格式
            phone = formatPhoneNumber(phone);

            return new Contact(contactId, name, phone);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 格式化电话号码
     */
    private String formatPhoneNumber(String phone) {
        if (phone == null) return "";
        return phone.replaceAll("\\s+", "").replaceAll("-", "");
    }
}