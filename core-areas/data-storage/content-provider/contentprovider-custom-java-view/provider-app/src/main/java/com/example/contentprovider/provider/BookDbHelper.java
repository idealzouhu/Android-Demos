package com.example.contentprovider.provider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.contentprovider.shared.BookContract;

/**
 * 管理书籍数据库的创建和版本控制
 */
public class BookDbHelper  extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "bookstore.db";
    private static final int DATABASE_VERSION = 1;

    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + BookContract.BookEntry.TABLE_NAME + " (" +
                    BookContract.BookEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    BookContract.BookEntry.COLUMN_NAME_TITLE + " TEXT NOT NULL," +
                    BookContract.BookEntry.COLUMN_NAME_AUTHOR + " TEXT NOT NULL," +
                    BookContract.BookEntry.COLUMN_NAME_PRICE + " REAL," +
                    BookContract.BookEntry.COLUMN_NAME_ISBN + " TEXT," +
                    BookContract.BookEntry.COLUMN_NAME_PUBLISH_DATE + " TEXT)";

    public BookDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + BookContract.BookEntry.TABLE_NAME);
        onCreate(db);
    }
}
