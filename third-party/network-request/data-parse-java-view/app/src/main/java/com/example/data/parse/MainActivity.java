package com.example.data.parse;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.data.parse.api.XmlFeedClient;
import com.example.data.parse.model.RssItem;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private Button btnFetch;
    private ProgressBar progress;
    private TextView tvResult;
    private XmlFeedClient xmlFeedClient;

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

        xmlFeedClient = new XmlFeedClient();
        btnFetch = findViewById(R.id.btn_fetch);
        progress = findViewById(R.id.progress);
        tvResult = findViewById(R.id.tv_result);

        btnFetch.setOnClickListener(v -> fetchXmlData());
    }

    private void fetchXmlData() {
        btnFetch.setEnabled(false);
        progress.setVisibility(View.VISIBLE);
        tvResult.setText(R.string.loading);

        xmlFeedClient.fetchRssItems(new XmlFeedClient.FeedCallback() {
            @Override
            public void onResult(List<RssItem> items) {
                btnFetch.setEnabled(true);
                progress.setVisibility(View.GONE);
                if (items == null || items.isEmpty()) {
                    tvResult.setText("未解析到任何条目。");
                    return;
                }
                StringBuilder sb = new StringBuilder();
                sb.append("共解析 ").append(items.size()).append(" 条\n\n");
                for (int i = 0; i < items.size() && i < 20; i++) {
                    RssItem item = items.get(i);
                    sb.append(i + 1).append(". ").append(item.getTitle()).append("\n");
                    if (item.getLink() != null) sb.append("   链接: ").append(item.getLink()).append("\n");
                    if (item.getPubDate() != null) sb.append("   日期: ").append(item.getPubDate()).append("\n");
                    sb.append("\n");
                }
                if (items.size() > 20) {
                    sb.append("… 仅显示前 20 条");
                }
                tvResult.setText(sb.toString());
            }

            @Override
            public void onError(Throwable t) {
                btnFetch.setEnabled(true);
                progress.setVisibility(View.GONE);
                String msg = getString(R.string.error_prefix) + (t != null ? t.getMessage() : "未知错误");
                tvResult.setText(msg);
                Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (xmlFeedClient != null) {
            xmlFeedClient.shutdown();
        }
    }
}
