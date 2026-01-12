package com.example.fab.basic;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

public class MainActivity extends AppCompatActivity {

    private CoordinatorLayout coordinatorLayout;
    private FloatingActionButton fab;
    private TextView myTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.coordinatorLayout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // 初始化视图
        coordinatorLayout = findViewById(R.id.coordinatorLayout);
        fab = findViewById(R.id.fab);
        myTextView = findViewById(R.id.textView);

        // 设置FAB的点击事件
        fab.setOnClickListener(view -> {
            // 点击FAB后显示 Snackbar
            Snackbar.make(coordinatorLayout, "这是一个Snackbar提示信息", Snackbar.LENGTH_LONG)
                    .setAction("知道了", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // 点击 Snackbar 上的按钮后的操作
                            myTextView.setText("Snackbar的按钮被点击了！文本已更新。");
                        }
                    })
                    .show(); // 显示 Snackbar
        });
    }
}