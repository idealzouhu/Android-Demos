package com.example.logging.custom.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Looper;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * 网络工具类 - 演示 LogUtil 在实际组件中的使用
 */
public class NetworkUtil {

    private static final String TAG = "NetworkUtil";

    /**
     * 检查网络连接
     */
    public static boolean isNetworkAvailable(Context context) {
        LogUtil.d(TAG, "检查网络连接状态");

        try {
            ConnectivityManager cm = (ConnectivityManager)
                    context.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (cm == null) {
                LogUtil.e(TAG, "ConnectivityManager 为 null");
                return false;
            }

            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            boolean isConnected = activeNetwork != null && activeNetwork.isConnected();

            LogUtil.i(TAG, "网络状态: " + (isConnected ? "已连接" : "未连接"));
            return isConnected;
        } catch (SecurityException e) {
            LogUtil.e(TAG, "网络权限检查失败", e);
            return false;
        } catch (Exception e) {
            LogUtil.e(TAG, "网络检查异常", e);
            return false;
        }
    }

    /**
     * 发起 GET 请求
     */
    public static void getRequest(String url, NetworkCallback callback) {
        LogUtil.d(TAG, "发起 GET 请求: " + url);

        // 使用性能追踪
        LogUtil.PerformanceTracker tracker = LogUtil.trackPerformance(TAG, "getRequest");

        new Thread(() -> {
            HttpURLConnection connection = null;
            try {
                URL requestUrl = new URL(url);
                connection = (HttpURLConnection) requestUrl.openConnection();

                connection.setRequestMethod("GET");
                connection.setConnectTimeout(10000);
                connection.setReadTimeout(10000);

                LogUtil.d(TAG, "设置请求参数完成");

                int responseCode = connection.getResponseCode();
                LogUtil.i(TAG, "响应码: " + responseCode);

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    InputStream inputStream = connection.getInputStream();
                    BufferedReader reader = new BufferedReader(
                            new InputStreamReader(inputStream)
                    );

                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    reader.close();

                    String responseString = response.toString();
                    LogUtil.json(TAG, responseString);

                    // 返回结果
                    new Handler(Looper.getMainLooper()).post(() -> {
                        callback.onSuccess(responseString);
                        tracker.finish();
                    });
                } else {
                    throw new Exception("HTTP 错误: " + responseCode);
                }
            } catch (Exception e) {
                LogUtil.e(TAG, "网络请求失败: " + url, e);
                new Handler(Looper.getMainLooper()).post(() -> {
                    callback.onError(e.getMessage());
                    tracker.finish();
                });
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }
        }).start();
    }

    /**
     * 模拟 POST 请求
     */
    public static void postRequest(String url, Map<String, String> params, NetworkCallback callback) {
        LogUtil.d(TAG, "发起 POST 请求: " + url);

        // 记录请求参数（注意：实际应用中要过滤敏感信息）
        StringBuilder paramLog = new StringBuilder("请求参数: ");
        for (Map.Entry<String, String> entry : params.entrySet()) {
            // 不记录密码等敏感信息
            if (!"password".equals(entry.getKey()) && !"token".equals(entry.getKey())) {
                paramLog.append(entry.getKey()).append("=").append(entry.getValue()).append(", ");
            } else {
                paramLog.append(entry.getKey()).append("=[FILTERED], ");
            }
        }
        LogUtil.d(TAG, paramLog.toString());

        // 模拟请求
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            try {
                // 模拟成功响应
                JSONObject jsonResponse = new JSONObject();
                jsonResponse.put("status", "success");
                jsonResponse.put("message", "请求处理成功");
                jsonResponse.put("timestamp", System.currentTimeMillis());

                String response = jsonResponse.toString();
                LogUtil.json(TAG, response);
                callback.onSuccess(response);
            } catch (Exception e) {
                LogUtil.e(TAG, "模拟请求失败", e);
                callback.onError(e.getMessage());
            }
        }, 1000);
    }

    /**
     * 网络回调接口
     */
    public interface NetworkCallback {
        void onSuccess(String response);
        void onError(String error);
    }
}