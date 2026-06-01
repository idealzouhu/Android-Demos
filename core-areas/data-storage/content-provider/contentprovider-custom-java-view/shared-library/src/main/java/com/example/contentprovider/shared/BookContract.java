package com.example.contentprovider.shared;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * 数据契约类 - Provider 和 Client 应用之间的统一接口
 * <p>
 *     1. 定义了 Provider 和 Client 应用之间通信的接口，包括表定义、列定义、MIME 类型定义、Content URI 定义、权限定义。
 *     2. 如果以后需要添加更多表（如作者表、分类表），可以创建新的 Entry 类
 * </p>
 */
public final class BookContract {

    private BookContract() {}

    // Content Provider 的唯一标识符
    public static final String CONTENT_AUTHORITY = "com.example.contentprovider.provider";

    // 基础 Content URI
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    /**
     * 书籍数据表的契约定义
     * <p>
     * 提供了书籍数据表的表定义、列定义和 MIME 类型定义、Content URI 定义、权限定义。
     * 这些定义用于 Content Provider 和 Content Resolver 之间的通信。
     */
    public static class BookEntry implements BaseColumns {
        private BookEntry() {}

        // 表名
        public static final String TABLE_NAME = "books";

        // 书籍表的完整 Content URI
        public static final Uri CONTENT_URI =
                Uri.withAppendedPath(BASE_CONTENT_URI, TABLE_NAME);

        // 列定义
        public static final String COLUMN_NAME_TITLE = "title";
        public static final String COLUMN_NAME_AUTHOR = "author";
        public static final String COLUMN_NAME_PRICE = "price";
        public static final String COLUMN_NAME_ISBN = "isbn";
        public static final String COLUMN_NAME_PUBLISH_DATE = "publish_date";

        // MIME 类型定义
        public static final String CONTENT_LIST_TYPE =
                "vnd.android.cursor.dir/vnd." + CONTENT_AUTHORITY + "." + TABLE_NAME;
        public static final String CONTENT_ITEM_TYPE =
                "vnd.android.cursor.item/vnd." + CONTENT_AUTHORITY + "." + TABLE_NAME;

        // 权限定义(自定义权限)
        public static final String PERMISSION_READ = CONTENT_AUTHORITY + ".READ_BOOKS";
        public static final String PERMISSION_WRITE = CONTENT_AUTHORITY + ".WRITE_BOOKS";
    }
}
