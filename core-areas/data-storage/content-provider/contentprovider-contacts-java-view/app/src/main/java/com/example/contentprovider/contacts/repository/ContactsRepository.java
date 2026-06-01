package com.example.contentprovider.contacts.repository;

import android.content.Context;

import com.example.contentprovider.contacts.model.Contact;
import com.example.contentprovider.contacts.provider.ContactsProvider;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 联系人数据仓库
 * 处理数据获取和缓存逻辑
 */
public class ContactsRepository {

    private final ContactsProvider contactsProvider;
    private final ExecutorService executorService;

    public ContactsRepository(Context context) {
        this.contactsProvider = new ContactsProvider(context);
        this.executorService = Executors.newSingleThreadExecutor();
    }

    public interface LoadContactsCallback {
        void onContactsLoaded(List<Contact> contacts);
        void onError(Exception e);
    }

    public interface ContactsCountCallback {
        void onCountLoaded(int count);
        void onError(Exception e);
    }

    /**
     * 异步加载所有联系人
     */
    public void loadContactsAsync(LoadContactsCallback callback) {
        executorService.execute(() -> {
            try {
                List<Contact> contacts = contactsProvider.getAllContacts();
                callback.onContactsLoaded(contacts);
            } catch (Exception e) {
                callback.onError(e);
            }
        });
    }

    /**
     * 异步搜索联系人
     */
    public void searchContactsAsync(String query, LoadContactsCallback callback) {
        executorService.execute(() -> {
            try {
                List<Contact> contacts = contactsProvider.searchContacts(query);
                callback.onContactsLoaded(contacts);
            } catch (Exception e) {
                callback.onError(e);
            }
        });
    }

    /**
     * 异步获取联系人数量
     */
    public void getContactsCountAsync(ContactsCountCallback callback) {
        executorService.execute(() -> {
            try {
                int count = contactsProvider.getContactsCount();
                callback.onCountLoaded(count);
            } catch (Exception e) {
                callback.onError(e);
            }
        });
    }

    /**
     * 关闭线程池
     */
    public void shutdown() {
        executorService.shutdown();
    }
}