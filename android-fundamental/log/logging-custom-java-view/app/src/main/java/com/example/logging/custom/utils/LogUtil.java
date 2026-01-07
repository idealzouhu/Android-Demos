package com.example.logging.custom.utils;

import android.util.Log;
import android.text.TextUtils;

import com.example.logging.custom.BuildConfig;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * 自定义日志工具类
 * 1. 控制日志输出（开发/生产环境）
 * 2. 统一日志格式
 * 3. 避免敏感信息泄漏
 */
public class LogUtil {

    // 日志级别
    public static final int VERBOSE = 1;
    public static final int DEBUG = 2;
    public static final int INFO = 3;
    public static final int WARN = 4;
    public static final int ERROR = 5;
    public static final int ASSERT = 6;

    // 当前日志级别（开发环境设为DEBUG，生产环境设为ERROR或WARN）
    public static int LOG_LEVEL = BuildConfig.DEBUG ? DEBUG : WARN;

    // 是否开启日志
    public static boolean IS_DEBUG = BuildConfig.DEBUG;

    // 日志TAG最大长度
    private static final int TAG_MAX_LENGTH = 23;

    // 时间格式
    private static final SimpleDateFormat TIME_FORMAT =
            new SimpleDateFormat("HH:mm:ss.SSS", Locale.getDefault());

    /**
     * Verbose 级别日志
     */
    public static void v(String tag, String message) {
        if (IS_DEBUG && LOG_LEVEL <= VERBOSE) {
            Log.v(tag, message);
        }
    }

    /**
     * Debug 级别日志
     */
    public static void d(String tag, String message) {
        if (IS_DEBUG && LOG_LEVEL <= DEBUG) {
            Log.d(tag, message);
        }
    }

    /**
     * Info 级别日志
     */
    public static void i(String tag, String message) {
        if (LOG_LEVEL <= INFO) {
            Log.i(tag, message);
        }
    }

    /**
     * Warn 级别日志
     */
    public static void w(String tag, String message) {
        if (LOG_LEVEL <= WARN) {
            Log.w(tag, message);
        }
    }

    /**
     * Error 级别日志（始终输出）
     */
    public static void e(String tag, String message) {
        Log.e(tag, message);
    }

    /**
     * Error 级别日志（带异常）
     */
    public static void e(String tag, String message, Throwable throwable) {
        Log.e(tag, message, throwable);
    }

    /**
     * 格式化输出 JSON
     */
    public static void json(String tag, String json) {
        if (IS_DEBUG && LOG_LEVEL <= DEBUG) {
            if (TextUtils.isEmpty(json)) {
                d(tag, "Empty/Null json content");
                return;
            }

            try {
                json = json.trim();
                if (json.startsWith("{")) {
                    JSONObject jsonObject = new JSONObject(json);
                    String message = jsonObject.toString(2);
                    d(tag, "\n" + message);
                } else if (json.startsWith("[")) {
                    JSONArray jsonArray = new JSONArray(json);
                    String message = jsonArray.toString(2);
                    d(tag, "\n" + message);
                } else {
                    e(tag, "Invalid JSON: " + json);
                }
            } catch (JSONException e) {
                e(tag, "JSON parse error: " + json, e);
            }
        }
    }

    /**
     * 性能监控 - 记录方法执行时间
     */
    public static PerformanceTracker trackPerformance(String tag, String methodName) {
        return new PerformanceTracker(tag, methodName);
    }

    /**
     * 性能追踪器
     */
    public static class PerformanceTracker {
        private final String tag;
        private final String methodName;
        private final long startTime;

        public PerformanceTracker(String tag, String methodName) {
            this.tag = tag;
            this.methodName = methodName;
            this.startTime = System.currentTimeMillis();
            d(tag, methodName + " - 开始执行");
        }

        public void finish() {
            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;
            String message = String.format(Locale.getDefault(),"%s - 执行完成，耗时: %dms", methodName, duration);
            d(tag, message);
        }
    }

    /**
     * 验证和格式化 TAG
     */
    private static String validateTag(String tag) {
        if (TextUtils.isEmpty(tag)) {
            return "AppLog";
        }

        // 截断过长的 TAG
        if (tag.length() > TAG_MAX_LENGTH) {
            return tag.substring(0, TAG_MAX_LENGTH);
        }

        return tag;
    }

    /**
     * 设置日志级别
     */
    public static void setLogLevel(int level) {
        LOG_LEVEL = level;
    }

    /**
     * 启用/禁用调试日志
     */
    public static void setDebug(boolean isDebug) {
        IS_DEBUG = isDebug;
    }
}