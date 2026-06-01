package com.example.sharedpreferences.basic;


import android.content.Context;
import android.content.SharedPreferences;

/**
 * SharedPreferences 管理工具类
 * <p>
 * 演示基本的存储、读取、删除操作
 */
public class SettingsManager {
    private static final String PREF_NAME = "user_settings";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_PASSWORD = "password";
    private static final String KEY_REMEMBER = "remember_password";

    private final SharedPreferences sharedPreferences;
    private final SharedPreferences.Editor editor;

    public SettingsManager(Context context) {
        // 获取SharedPreferences实例，文件名为"user_settings.xml"
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    /**
     * 保存用户登录凭证
     */
    public void saveUserCredentials(String username, String password, boolean rememberPassword) {
        editor.putString(KEY_USERNAME, username);

        if (rememberPassword) {
            editor.putString(KEY_PASSWORD, password);
            editor.putBoolean(KEY_REMEMBER, true);
        } else {
            editor.remove(KEY_PASSWORD);
            editor.putBoolean(KEY_REMEMBER, false);
        }

        // 提交更改
        editor.apply();
    }

    /**
     * 获取保存的用户名
     */
    public String getUsername() {
        return sharedPreferences.getString(KEY_USERNAME, "");
    }

    /**
     * 获取保存的密码
     */
    public String getPassword() {
        return sharedPreferences.getString(KEY_PASSWORD, "");
    }

    /**
     * 检查是否设置了记住密码
     */
    public boolean isRememberPassword() {
        return sharedPreferences.getBoolean(KEY_REMEMBER, false);
    }

    /**
     * 清除所有保存的数据
     */
    public void clearUserCredentials() {
        editor.clear();
        editor.apply();
    }

    /**
     * 检查是否包含某个key
     */
    public boolean containsKey(String key) {
        return sharedPreferences.contains(key);
    }

    /**
     * 获取所有保存的数据（用于调试）
     */
    public String getAllData() {
        return sharedPreferences.getAll().toString();
    }
}
