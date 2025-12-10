package com.example.contentprovider.client;

import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.contentprovider.shared.BookContract;

/**
 *
 */
public class MainActivity extends AppCompatActivity {
    private static final int PERMISSION_REQUEST_CODE = 1001;
    private LinearLayout bookListLayout;
    private Button btnLoad;

    // 权限数组
    private final String[] REQUIRED_PERMISSIONS = {
            BookContract.BookEntry.PERMISSION_READ,
            BookContract.BookEntry.PERMISSION_WRITE
    };


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
        checkProviderExists();
    }

    private void initViews() {
        bookListLayout = findViewById(R.id.book_list_layout);
        btnLoad = findViewById(R.id.btn_load);

        btnLoad.setOnClickListener(v -> {
            if (hasPermissions()) {
                loadBooksFromProvider();
            } else {
                requestPermissions();
            }
        });
    }

    /**
     * 检查 Provider 是否存在
     */
    private void checkProviderExists() {
        try {
            PackageManager pm = getPackageManager();
            ProviderInfo providerInfo = pm.resolveContentProvider(
                    BookContract.CONTENT_AUTHORITY, 0);

            if (providerInfo != null) {
                Toast.makeText(this, "✅ Provider 应用已安装", Toast.LENGTH_LONG).show();
                btnLoad.setEnabled(true);
            } else {
                Toast.makeText(this, "❌ Provider 应用未安装，请先安装 Provider 应用", Toast.LENGTH_LONG).show();
                btnLoad.setEnabled(false);
            }
        } catch (Exception e) {
            Toast.makeText(this, "检查 Provider 时出错: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private boolean hasPermissions() {
        for (String permission : REQUIRED_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(this, permission)
                    != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    /**
     * 请求自定义权限
     * <p>
     *     1. 检查是否需要显示权限解释(即用户之前拒绝过该权限)。
     *     2. ActivityCompat.requestPermissions() 方法本身不支持自定义权限请求对话框的内容。
     *     3. 系统权限管理界面只显示标准系统权限，不显示应用自定义权限。
     * </p>
     *
     */
    private void requestPermissions() {
        Log.d("MainActivity", "requestPermissions");

        // 检查是否需要显示权限解释
        boolean shouldShowRationale = false;
        for (String permission : REQUIRED_PERMISSIONS) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
                shouldShowRationale = true;
                break;
            }
        }

        if (shouldShowRationale) {
            // 显示解释对话框
            new androidx.appcompat.app.AlertDialog.Builder(this)
                    .setTitle("需要权限")
                    .setMessage("需要权限访问书籍数据")
                    .setPositiveButton("确定", (dialog, which) -> {
                        // 请求权限
                        ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, PERMISSION_REQUEST_CODE);
                    })
                    .setNegativeButton("取消", (dialog, which) -> {
                        Toast.makeText(this, "权限被拒绝，无法访问数据", Toast.LENGTH_LONG).show();
                    })
                    .show();
        } else {
            // 直接请求权限
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, PERMISSION_REQUEST_CODE);
        }
    }

    /**
     * 处理权限请求结果
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "权限获取成功", Toast.LENGTH_SHORT).show();
                loadBooksFromProvider();
            } else {
                Toast.makeText(this, "权限被拒绝，无法访问数据", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void loadBooksFromProvider() {
        Log.d("MainActivity", "loadBooksFromProvider");
        bookListLayout.removeAllViews();

        try {
            Cursor cursor = getContentResolver().query(
                    BookContract.BookEntry.CONTENT_URI,
                    null, null, null,
                    BookContract.BookEntry.COLUMN_NAME_TITLE + " ASC"
            );

            if (cursor == null) {
                showError("无法访问 Provider，请确保 Provider 应用已安装");
                return;
            }

            if (cursor.getCount() == 0) {
                TextView emptyView = new TextView(this);
                emptyView.setText("Provider 中没有书籍数据");
                emptyView.setPadding(16, 16, 16, 16);
                bookListLayout.addView(emptyView);
            } else {
                while (cursor.moveToNext()) {
                    String title = cursor.getString(cursor.getColumnIndexOrThrow(
                            BookContract.BookEntry.COLUMN_NAME_TITLE));
                    String author = cursor.getString(cursor.getColumnIndexOrThrow(
                            BookContract.BookEntry.COLUMN_NAME_AUTHOR));
                    double price = cursor.getDouble(cursor.getColumnIndexOrThrow(
                            BookContract.BookEntry.COLUMN_NAME_PRICE));
                    String isbn = cursor.getString(cursor.getColumnIndexOrThrow(
                            BookContract.BookEntry.COLUMN_NAME_ISBN));

                    addBookItemView(title, author, price, isbn);
                }
                Toast.makeText(this, "加载完成，共 " + cursor.getCount() + " 本书", Toast.LENGTH_SHORT).show();
            }

            cursor.close();

        } catch (SecurityException e) {
            showError("权限不足: " + e.getMessage());
        } catch (Exception e) {
            showError("访问失败: " + e.getMessage());
        }
    }

    private void addBookItemView(String title, String author, double price, String isbn) {
        View bookItemView = getLayoutInflater().inflate(R.layout.item_book, bookListLayout, false);

        TextView textTitle = bookItemView.findViewById(R.id.text_title);
        TextView textAuthor = bookItemView.findViewById(R.id.text_author);
        TextView textPrice = bookItemView.findViewById(R.id.text_price);
        TextView textIsbn = bookItemView.findViewById(R.id.text_isbn);

        textTitle.setText(title);
        textAuthor.setText("作者: " + author);
        textPrice.setText("价格: ¥" + price);
        textIsbn.setText("ISBN: " + (isbn != null ? isbn : "无"));

        bookListLayout.addView(bookItemView);
    }

    private void showError(String message) {
        TextView errorView = new TextView(this);
        errorView.setText(message);
        errorView.setPadding(16, 16, 16, 16);
        errorView.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
        bookListLayout.addView(errorView);
    }
}