package com.example.glide.basic;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

/**
 * Glide 基础示例：网络图片列表。
 * - 底层网络由 OkHttp 提供（okhttp3-integration）
 * - 使用 Generated API（GlideApp）及 @GlideOption listThumb()
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView recycler = findViewById(R.id.recycler);
        recycler.setLayoutManager(new LinearLayoutManager(this));
        recycler.setHasFixedSize(true);

        ImageListAdapter adapter = new ImageListAdapter();
        adapter.setItems(buildImageItems());
        recycler.setAdapter(adapter);
    }

    /** 构建演示用的网络图片列表（使用 picsum.photos 占位图服务） */
    private static List<ImageItem> buildImageItems() {
        String base = "https://picsum.photos/400/300";
        List<ImageItem> list = new ArrayList<>();
        for (int i = 1; i <= 20; i++) {
            String url = base + "?random=" + i;
            list.add(new ImageItem(url, "网络图片 " + i));
        }
        return list;
    }
}
