package com.example.file.basic;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class MainActivity extends AppCompatActivity {

    private EditText mEditText;
    private Button saveButton;


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

        mEditText = findViewById(R.id.edit_text);
        saveButton = findViewById(R.id.save_button);

        // 应用启动时，从文件加载已保存的数据并显示在输入框
        String savedContent = loadFromFile();
        if (!TextUtils.isEmpty(savedContent)) {
            mEditText.setText(savedContent);
            // 将光标移动到文本末尾，方便继续输入
            mEditText.setSelection(savedContent.length());
            Toast.makeText(this, "内容已恢复", Toast.LENGTH_SHORT).show();
        }

        saveButton.setOnClickListener(v -> {
            String content = mEditText.getText().toString();
            saveToFile(content);
            Toast.makeText(MainActivity.this, "内容已保存", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 在活动销毁时（如退出应用），保存当前输入的内容
        String inputText = mEditText.getText().toString();
        saveToFile(inputText);
    }

    /**
     * 将字符串数据保存到文件
     * <p>
     * 代码中使用 openFileOutput()和 openFileInput()方法，文件会自动保存在应用的内置存储空间内，路径通常是
     * /data/data/<你的应用包名>/files/note_data。你无需指定完整路径，也无需申请存储权限，因为这些文件默认是
     * 应用私有的
     *
     * @param text 要保存的文本内容
     */
    private void saveToFile(String text) {
        FileOutputStream out = null;
        BufferedWriter writer = null;
        try {
            // 使用 openFileOutput 打开文件输出流
            // 参数1: 文件名，如 "note_data"
            // 参数2: 操作模式，MODE_PRIVATE 表示覆盖原文件
            out = openFileOutput("note_data", Context.MODE_PRIVATE);
            writer = new BufferedWriter(new OutputStreamWriter(out));
            writer.write(text); // 执行写入操作
        } catch (IOException e) {
            Log.e("MainActivity", "Failed to save file content", e);
        } finally {
            // 最终关闭流，释放资源
            try {
                if (writer != null) {
                    writer.close();
                }
            } catch (IOException e) {
                Log.e("MainActivity", "Failed to close writer", e);
            }
        }
    }

    /**
     * 从文件中加载数据
     *
     * @return 文件中的文本内容，如果文件不存在或读取失败则返回空字符串
     */
    private String loadFromFile() {
        FileInputStream in = null;
        BufferedReader reader = null;
        StringBuilder content = new StringBuilder();
        try {
            in = openFileInput("note_data"); // 打开文件输入流
            reader = new BufferedReader(new InputStreamReader(in));
            String line = "";
            // 逐行读取文件内容
            while ((line = reader.readLine()) != null) {
                content.append(line);
            }
        } catch (IOException e) {
            Log.e("MainActivity", "Failed to load file content", e);
        } finally {
            // 最终关闭流，释放资源
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    Log.e("MainActivity", "Failed to close reader", e);
                }
            }
        }
        return content.toString();
    }
}