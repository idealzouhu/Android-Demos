package com.example.contentprovider.provider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.example.contentprovider.shared.BookContract;

/**
 * BookProvider类继承自ContentProvider，用于管理书籍数据的内容提供者
 * <p>
 * BookProvider实现了对书籍数据的增删改查操作，通过URI匹配器来区分不同的数据访问请求
 */
public class BookProvider extends ContentProvider {
    private BookDbHelper dbHelper;

    /** URI匹配器，用于匹配不同的数据访问请求 */
    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    /** 匹配所有书籍列表的URI代码 */
    private static final int BOOKS = 100;

    /** 匹配特定书籍ID的URI代码 */
    private static final int BOOK_ID = 101;

    /*
      静态初始化块，用于初始化URI匹配器
      注册书籍相关的URI模式，包括所有书籍列表和特定ID书籍的访问路径
     */
    static {
        uriMatcher.addURI(BookContract.CONTENT_AUTHORITY, "books", BOOKS);
        uriMatcher.addURI(BookContract.CONTENT_AUTHORITY, "books/#", BOOK_ID);
    }

    @Override
    public boolean onCreate() {
        dbHelper = new BookDbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor;

        int match = uriMatcher.match(uri);
        switch (match) {
            case BOOKS:
                cursor = db.query(BookContract.BookEntry.TABLE_NAME,
                        projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case BOOK_ID:
                selection = BookContract.BookEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = db.query(BookContract.BookEntry.TABLE_NAME,
                        projection, selection, selectionArgs, null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        int match = uriMatcher.match(uri);
        if (match != BOOKS) {
            throw new IllegalArgumentException("Insertion not supported for " + uri);
        }

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        long id = db.insert(BookContract.BookEntry.TABLE_NAME, null, values);

        if (id == -1) {
            return null;
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        int match = uriMatcher.match(uri);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int rowsUpdated;

        switch (match) {
            case BOOKS:
                rowsUpdated = db.update(BookContract.BookEntry.TABLE_NAME,
                        values, selection, selectionArgs);
                break;
            case BOOK_ID:
                selection = BookContract.BookEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsUpdated = db.update(BookContract.BookEntry.TABLE_NAME,
                        values, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Update not supported for " + uri);
        }

        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsUpdated;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int match = uriMatcher.match(uri);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int rowsDeleted;

        switch (match) {
            case BOOKS:
                rowsDeleted = db.delete(BookContract.BookEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case BOOK_ID:
                selection = BookContract.BookEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = db.delete(BookContract.BookEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion not supported for " + uri);
        }

        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsDeleted;
    }

    /**
     * 获取指定URI对应的数据类型
     *
     * @param uri 需要查询数据类型的URI
     * @return 返回URI对应的MIME类型字符串
     * @throws IllegalArgumentException 当URI不匹配任何已知模式时抛出异常
     */
    @Override
    public String getType(Uri uri) {
        int match = uriMatcher.match(uri);
        switch (match) {
            case BOOKS:
                return BookContract.BookEntry.CONTENT_LIST_TYPE;
            case BOOK_ID:
                return BookContract.BookEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
    }
}
