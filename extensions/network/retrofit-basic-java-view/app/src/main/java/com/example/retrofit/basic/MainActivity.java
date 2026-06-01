package com.example.retrofit.basic;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.retrofit.basic.api.JsonPlaceholderService;
import com.example.retrofit.basic.api.RetrofitClient;
import com.example.retrofit.basic.model.Post;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 演示 Retrofit 基本用法：通过接口发起 GET 请求，用 Callback 处理异步结果。
 */
public class MainActivity extends AppCompatActivity {

    private TextView tvResult;
    private Button btnFetch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvResult = findViewById(R.id.tvResult);
        btnFetch = findViewById(R.id.btnFetch);

        btnFetch.setOnClickListener(v -> fetchPosts());
    }

    private void fetchPosts() {
        JsonPlaceholderService service = RetrofitClient.getJsonPlaceholderService();
        Call<List<Post>> call = service.getPosts();

        tvResult.setText("请求中…");
        btnFetch.setEnabled(false);

        call.enqueue(new Callback<List<Post>>() {
            @Override
            public void onResponse(@NonNull Call<List<Post>> call, @NonNull Response<List<Post>> response) {
                btnFetch.setEnabled(true);
                if (response.isSuccessful() && response.body() != null) {
                    List<Post> posts = response.body();
                    StringBuilder sb = new StringBuilder();
                    int showCount = Math.min(posts.size(), 5);
                    for (int i = 0; i < showCount; i++) {
                        Post p = posts.get(i);
                        sb.append("[").append(p.getId()).append("] ").append(p.getTitle()).append("\n\n");
                    }
                    sb.append("… 共 ").append(posts.size()).append(" 条");
                    tvResult.setText(sb.toString());
                } else {
                    tvResult.setText("响应异常: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Post>> call, @NonNull Throwable t) {
                btnFetch.setEnabled(true);
                tvResult.setText("请求失败: " + t.getMessage());
                Toast.makeText(MainActivity.this, "请检查网络", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
