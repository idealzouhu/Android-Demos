package com.example.contentprovider.contacts;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.contentprovider.contacts.adapter.ContactsAdapter;
import com.example.contentprovider.contacts.model.Contact;
import com.example.contentprovider.contacts.repository.ContactsRepository;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_READ_CONTACTS = 100;

    private RecyclerView contactsRecyclerView;
    private ProgressBar loadingProgressBar;
    private Button loadContactsButton;
    private TextView permissionHintTextView;

    private ContactsAdapter contactsAdapter;
    private ContactsRepository contactsRepository;
    private List<Contact> contactList = new ArrayList<>();

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
        setupRecyclerView();
        initRepository();
        checkPermissions();
    }

    private void initViews() {
        contactsRecyclerView = findViewById(R.id.contactsRecyclerView);
        loadingProgressBar = findViewById(R.id.loadingProgressBar);
        loadContactsButton = findViewById(R.id.loadContactsButton);
        permissionHintTextView = findViewById(R.id.permissionHintTextView);

        loadContactsButton.setOnClickListener(v -> loadContacts());
    }

    private void setupRecyclerView() {
        contactsAdapter = new ContactsAdapter(contactList);
        contactsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        contactsRecyclerView.setAdapter(contactsAdapter);
    }

    private void initRepository() {
        contactsRepository = new ContactsRepository(this);
    }

    /**
     * 检查应用所需的联系人读取权限状态
     * <p>
     * 该方法用于检查应用是否已获得读取联系人权限，根据权限状态更新UI显示：
     * - 如果未获得权限：显示权限提示文本
     * - 如果已获得权限：隐藏权限提示文本
     */
    private void checkPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {
            permissionHintTextView.setVisibility(View.VISIBLE);
            // loadContactsButton.setEnabled(false);
        } else {
            permissionHintTextView.setVisibility(View.GONE);
            // loadContactsButton.setEnabled(true);
        }
    }

    /**
     * 请求读取联系人权限
     * <p>
     * 该方法用于向用户请求读取设备联系人的权限，通过ActivityCompat.requestPermissions方法发起权限请求，
     * 请求结果将在onRequestPermissionsResult回调方法中处理。
     */
    private void requestContactsPermission() {
        Log.d("MainActivity", "Requesting READ_CONTACTS permission");
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.READ_CONTACTS},
                PERMISSION_REQUEST_READ_CONTACTS);
    }

    /**
     * 加载联系人列表
     * <p>
     * 该方法首先检查是否具有读取联系人权限，如果没有则请求权限。
     * 如果已有权限，则从联系人仓库异步加载联系人数据，并更新UI显示。
     * 加载过程中会显示加载状态，加载完成后刷新联系人列表并显示提示信息。
     */
    private void loadContacts() {
        // 检查是否具有读取联系人权限
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {
            requestContactsPermission();
            return;
        }

        showLoading(true);

        // 异步加载联系人数据
        contactsRepository.loadContactsAsync(new ContactsRepository.LoadContactsCallback() {
            @Override
            public void onContactsLoaded(List<Contact> contacts) {
                runOnUiThread(() -> {
                    showLoading(false);
                    contactList.clear();
                    contactList.addAll(contacts);
                    contactsAdapter.notifyDataSetChanged();

                    if (contacts.isEmpty()) {
                        Toast.makeText(MainActivity.this, "未找到联系人", Toast.LENGTH_SHORT).show();
                    } else {
                        contactsRecyclerView.setVisibility(View.VISIBLE);
                        Toast.makeText(MainActivity.this,
                                "加载了 " + contacts.size() + " 个联系人", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onError(Exception e) {
                runOnUiThread(() -> {
                    showLoading(false);
                    Toast.makeText(MainActivity.this,
                            "加载联系人失败: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private void showLoading(boolean loading) {
        loadingProgressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
        loadContactsButton.setEnabled(!loading);
    }

    /**
     * 处理权限请求结果的回调方法
     *
     * @param requestCode 权限请求的标识码
     * @param permissions 请求的权限数组
     * @param grantResults 权限授权结果数组
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        Log.d("MainActivity", "onRequestPermissionsResult: " + requestCode);
        if (requestCode == PERMISSION_REQUEST_READ_CONTACTS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                permissionHintTextView.setVisibility(View.GONE);
                loadContactsButton.setEnabled(true);
                loadContacts();
            } else {
                Toast.makeText(this, "需要联系人权限才能读取联系人", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (contactsRepository != null) {
            contactsRepository.shutdown();
        }
    }
}