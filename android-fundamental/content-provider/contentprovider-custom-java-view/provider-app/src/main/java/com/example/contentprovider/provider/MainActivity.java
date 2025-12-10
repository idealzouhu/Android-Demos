package com.example.contentprovider.provider;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.contentprovider.shared.BookContract;

public class MainActivity extends AppCompatActivity {

    private EditText editTitle, editAuthor, editPrice, editIsbn;
    private LinearLayout bookListLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initViews();
        setupClickListeners();
        loadBooks();
    }

    private void initViews() {
        editTitle = findViewById(R.id.edit_title);
        editAuthor = findViewById(R.id.edit_author);
        editPrice = findViewById(R.id.edit_price);
        editIsbn = findViewById(R.id.edit_isbn);
        bookListLayout = findViewById(R.id.book_list_layout);

        Button btnAdd = findViewById(R.id.btn_add);
        Button btnRefresh = findViewById(R.id.btn_refresh);

        btnAdd.setOnClickListener(v -> addBook());
        btnRefresh.setOnClickListener(v -> loadBooks());
    }

    private void setupClickListeners() {
        // 初始化时已设置
    }

    private void addBook() {
        String title = editTitle.getText().toString().trim();
        String author = editAuthor.getText().toString().trim();
        String priceStr = editPrice.getText().toString().trim();
        String isbn = editIsbn.getText().toString().trim();

        if (title.isEmpty() || author.isEmpty() || priceStr.isEmpty()) {
            Toast.makeText(this, "请填写完整信息", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            double price = Double.parseDouble(priceStr);

            ContentValues values = new ContentValues();
            values.put(BookContract.BookEntry.COLUMN_NAME_TITLE, title);
            values.put(BookContract.BookEntry.COLUMN_NAME_AUTHOR, author);
            values.put(BookContract.BookEntry.COLUMN_NAME_PRICE, price);
            values.put(BookContract.BookEntry.COLUMN_NAME_ISBN, isbn);

            getContentResolver().insert(BookContract.BookEntry.CONTENT_URI, values);

            Toast.makeText(this, "添加成功", Toast.LENGTH_SHORT).show();
            clearInputs();
            loadBooks();

        } catch (NumberFormatException e) {
            Toast.makeText(this, "价格格式错误", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadBooks() {
        bookListLayout.removeAllViews();

        Cursor cursor = getContentResolver().query(
                BookContract.BookEntry.CONTENT_URI,
                null, null, null, null
        );

        if (cursor != null) {
            try {
                if (cursor.moveToFirst()) {
                    do {
                        String title = cursor.getString(cursor.getColumnIndexOrThrow(
                                BookContract.BookEntry.COLUMN_NAME_TITLE));
                        String author = cursor.getString(cursor.getColumnIndexOrThrow(
                                BookContract.BookEntry.COLUMN_NAME_AUTHOR));
                        double price = cursor.getDouble(cursor.getColumnIndexOrThrow(
                                BookContract.BookEntry.COLUMN_NAME_PRICE));

                        addBookItemView(title, author, price);
                    } while (cursor.moveToNext());
                } else {
                    TextView emptyView = new TextView(this);
                    emptyView.setText("暂无书籍数据");
                    emptyView.setPadding(16, 16, 16, 16);
                    bookListLayout.addView(emptyView);
                }
            } finally {
                cursor.close();
            }
        }
    }

    private void addBookItemView(String title, String author, double price) {
        View bookItemView = getLayoutInflater().inflate(R.layout.item_book, bookListLayout, false);

        TextView textTitle = bookItemView.findViewById(R.id.text_title);
        TextView textAuthor = bookItemView.findViewById(R.id.text_author);
        TextView textPrice = bookItemView.findViewById(R.id.text_price);

        textTitle.setText("书名: " + title);
        textAuthor.setText("作者: " + author);
        textPrice.setText("价格: ¥" + price);

        bookListLayout.addView(bookItemView);
    }

    private void clearInputs() {
        editTitle.setText("");
        editAuthor.setText("");
        editPrice.setText("");
        editIsbn.setText("");
    }
}