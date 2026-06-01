package com.example.data.parse.api;

import android.os.Handler;
import android.os.Looper;

import com.example.data.parse.model.RssItem;
import com.example.data.parse.parser.RssPullParser;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * 使用 OkHttp 请求 XML 数据，并在后台解析后通过回调返回结果。
 */
public class XmlFeedClient {

    private static final String DEFAULT_RSS_URL =
            "https://developer.android.com/feeds/android-developers.xml";

    private final OkHttpClient client = new OkHttpClient.Builder().build();
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    /**
     * 获取默认 RSS 源并解析为 RssItem 列表。
     *
     * @param callback 结果回调，onResult 在主线程执行，onError 在主线程执行
     */
    public void fetchRssItems(FeedCallback callback) {
        fetchRssItems(DEFAULT_RSS_URL, callback);
    }

    /**
     * 请求指定 URL 的 XML，解析为 RssItem 列表。
     *
     * @param url      XML/RSS 地址
     * @param callback 结果回调
     */
    public void fetchRssItems(String url, FeedCallback callback) {
        executor.execute(() -> {
            try {
                Request request = new Request.Builder().url(url).build();
                try (Response response = client.newCall(request).execute()) {
                    if (!response.isSuccessful() || response.body() == null) {
                        postError(callback, new IOException("HTTP " + response.code()));
                        return;
                    }
                    ResponseBody body = response.body();
                    String charset = body.contentType() != null && body.contentType().charset() != null
                            ? body.contentType().charset().name()
                            : "UTF-8";
                    try (InputStream is = body.byteStream()) {
                        List<RssItem> items = RssPullParser.parseItems(is, charset);
                        postResult(callback, items);
                    }
                }
            } catch (IOException | XmlPullParserException e) {
                postError(callback, e);
            }
        });
    }

    private void postResult(FeedCallback callback, List<RssItem> items) {
        mainHandler.post(() -> callback.onResult(items));
    }

    private void postError(FeedCallback callback, Throwable t) {
        mainHandler.post(() -> callback.onError(t));
    }

    /**
     * 释放资源，可在 Activity 销毁时调用。
     */
    public void shutdown() {
        executor.shutdown();
    }
    
    public interface FeedCallback {
        void onResult(List<RssItem> items);

        void onError(Throwable t);
    }
}
