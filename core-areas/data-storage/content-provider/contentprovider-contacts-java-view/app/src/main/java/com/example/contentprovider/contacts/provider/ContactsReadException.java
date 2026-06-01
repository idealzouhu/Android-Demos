package com.example.contentprovider.contacts.provider;

/**
 * 联系人读取异常
 */
public class ContactsReadException  extends Exception {
    public ContactsReadException(String message) {
        super(message);
    }

    public ContactsReadException(String message, Throwable cause) {
        super(message, cause);
    }

}
