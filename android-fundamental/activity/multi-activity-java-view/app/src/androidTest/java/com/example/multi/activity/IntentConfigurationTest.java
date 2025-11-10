package com.example.multi.activity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.util.Log;

import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import java.util.List;

@RunWith(AndroidJUnit4.class)
public class IntentConfigurationTest {
    private static final String TAG = "IntentTest";

    @Test
    public void testIntentConfiguration() {
        PackageManager pm = ApplicationProvider.getApplicationContext().getPackageManager();

        // 测试1：检查自己的应用是否能响应
        Intent selfTest = new Intent(Intent.ACTION_VIEW);
        selfTest.setData(Uri.parse("https://test.com"));
        selfTest.setPackage(ApplicationProvider.getApplicationContext().getPackageName());

        List<ResolveInfo> selfResults = pm.queryIntentActivities(selfTest, 0);
        Log.d(TAG, "自己应用响应数量: " + selfResults.size());

        // 测试2：检查所有能处理的应用
        Intent globalTest = new Intent(Intent.ACTION_VIEW);
        globalTest.setData(Uri.parse("https://test.com"));

        List<ResolveInfo> globalResults = pm.queryIntentActivities(globalTest, 0);
        Log.d(TAG, "全局响应数量: " + globalResults.size());

        for (ResolveInfo info : globalResults) {
            Log.d(TAG, "应用: " + info.activityInfo.packageName);
        }
    }

    @Test
    public void testSimpleIntentResolution() {
        PackageManager pm = ApplicationProvider.getApplicationContext().getPackageManager();

        // 方法1：只指定action，不指定data
        Intent intent1 = new Intent(Intent.ACTION_VIEW);
        List<ResolveInfo> results1 = pm.queryIntentActivities(intent1,
                PackageManager.MATCH_DEFAULT_ONLY);
        Log.d("SimpleTest", "只有ACTION: " + results1.size());
        for (ResolveInfo info : results1) {
            Log.d("SimpleTest", "应用: " + info.activityInfo.packageName);
        }

        // 方法2：指定scheme，不指定具体URL
        Intent intent2 = new Intent(Intent.ACTION_VIEW);
        intent2.setData(Uri.parse("https://"));
        List<ResolveInfo> results2 = pm.queryIntentActivities(intent2,
                PackageManager.MATCH_DEFAULT_ONLY);
        Log.d("SimpleTest", "只有scheme: " + results2.size());
        for (ResolveInfo info : results2) {
            Log.d("SimpleTest", "应用: " + info.activityInfo.packageName);
        }

        // 方法3：指定scheme，指定具体URL
        Intent intent3 = new Intent(Intent.ACTION_VIEW);
        intent3.setData(Uri.parse("https://developer.android.com"));
        List<ResolveInfo> results3 = pm.queryIntentActivities(intent3,
                PackageManager.MATCH_DEFAULT_ONLY);
        Log.d("SimpleTest", "只有scheme: " + results3.size());
        for (ResolveInfo info : results3) {
            Log.d("SimpleTest", "应用: " + info.activityInfo.packageName);
        }
    }
}
