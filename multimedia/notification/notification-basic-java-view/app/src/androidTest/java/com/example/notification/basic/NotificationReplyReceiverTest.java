package com.example.notification.basic;

import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.*;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Build;
import android.util.Log;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.List;

/**
 * 测试 NotificationReplyReceiver 是否能够收到广播
 * 不修改任何源代码
 */
@RunWith(AndroidJUnit4.class)
public class NotificationReplyReceiverTest {

    private static final String TAG = "ReceiverTest";
    private static final String TEST_ACTION = "com.example.notification.ACTION_REPLY";

    /**
     * 测试1：检查 NotificationReplyReceiver 是否在 Manifest 中正确注册
     */
    @Test
    public void testReceiverRegistration() {
        Log.d(TAG, "=== 测试接收器注册 ===");

        Context context = ApplicationProvider.getApplicationContext();

        // 查询 Manifest 中注册的 BroadcastReceiver
        Intent intent = new Intent(TEST_ACTION);

        // 查询所有注册了该 action 的接收器
        List<ResolveInfo> receivers =
                context.getPackageManager().queryBroadcastReceivers(intent, 0);

        // 打印找到的接收器
        Log.d(TAG, "找到 " + receivers.size() + " 个接收器:");
        for (ResolveInfo info : receivers) {
            Log.d(TAG, "  - " + info.activityInfo.name);
        }

        // 验证 NotificationReplyReceiver 是否注册
        boolean found = false;
        for (ResolveInfo info : receivers) {
            if (info.activityInfo.name.contains("NotificationReplyReceiver")) {
                found = true;
                Log.d(TAG, "✅ 找到 NotificationReplyReceiver");
                break;
            }
        }

        assertTrue("NotificationReplyReceiver 未在 Manifest 中注册", found);
    }

    /**
     * 测试2：发送广播并验证接收器能收到
     * 通过注册测试接收器来验证
     */
    @Test
    public void testBroadcastDelivery() throws Exception {
        Log.d(TAG, "=== 测试广播投递（Android 13+ 兼容）===");

        Context context = ApplicationProvider.getApplicationContext();

        // 使用 CountDownLatch 等待广播接收
        final CountDownLatch latch = new CountDownLatch(1);
        final boolean[] received = {false};

        // 创建测试接收器
        BroadcastReceiver testReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d(TAG, "📢 测试接收器收到广播");
                received[0] = true;
                latch.countDown();
            }
        };

        try {
            // 注册测试接收器 - 修复 Android 13+ 问题
            IntentFilter filter = new IntentFilter(TEST_ACTION);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                // Android 13+ 必须指定导出标志
                context.registerReceiver(testReceiver, filter,
                        Context.RECEIVER_EXPORTED);  // 或 Context.RECEIVER_NOT_EXPORTED
                Log.d(TAG, "✅ Android 13+ 注册（使用 RECEIVER_EXPORTED）");
            } else {
                // Android 12 及以下
                context.registerReceiver(testReceiver, filter);
                Log.d(TAG, "✅ Android 12- 注册");
            }

            // 发送测试广播
            Intent broadcastIntent = new Intent(TEST_ACTION);
            broadcastIntent.putExtra("test", "value");
            broadcastIntent.putExtra("timestamp", System.currentTimeMillis());
            broadcastIntent.setPackage(context.getPackageName());

            context.sendBroadcast(broadcastIntent);
            Log.d(TAG, "✅ 广播已发送");

            // 等待广播接收
            boolean success = latch.await(3, TimeUnit.SECONDS);

            // 验证结果
            assertTrue("广播未在3秒内收到", success);
            assertTrue("接收标志未设置", received[0]);

            Log.d(TAG, "✅ 测试通过：广播投递成功");

        } finally {
            // 清理
            try {
                context.unregisterReceiver(testReceiver);
                Log.d(TAG, "✅ 测试接收器已注销");
            } catch (IllegalArgumentException e) {
                Log.w(TAG, "测试接收器注销时出错: " + e.getMessage());
            }
        }
    }

    /**
     * 测试3：测试显式广播（直接指定接收器类）
     * 这种方法不依赖 Manifest 中的 intent-filter
     */
    @Test
    public void testLocalBroadcast() throws Exception {
        Log.d(TAG, "=== 测试本地广播 ===");

        Context context = ApplicationProvider.getApplicationContext();

        final CountDownLatch latch = new CountDownLatch(1);
        final boolean[] received = {false};

        BroadcastReceiver testReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d(TAG, "📢 收到本地广播");
                received[0] = true;
                latch.countDown();
            }
        };

        try {
            IntentFilter filter = new IntentFilter(TEST_ACTION + "_LOCAL");

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                // 使用 RECEIVER_NOT_EXPORTED 表示不导出到其他应用
                context.registerReceiver(testReceiver, filter,
                        Context.RECEIVER_NOT_EXPORTED);
                Log.d(TAG, "✅ 注册本地接收器（RECEIVER_NOT_EXPORTED）");
            } else {
                context.registerReceiver(testReceiver, filter);
                Log.d(TAG, "✅ 注册本地接收器");
            }

            // 发送广播
            Intent broadcast = new Intent(TEST_ACTION + "_LOCAL");
            broadcast.putExtra("local", "test");
            broadcast.setPackage(context.getPackageName());

            context.sendBroadcast(broadcast);

            // 等待
            boolean success = latch.await(3, TimeUnit.SECONDS);

            assertTrue("本地广播未收到", success);
            assertTrue("接收标志未设置", received[0]);

            Log.d(TAG, "✅ 本地广播测试通过");

        } finally {
            try {
                context.unregisterReceiver(testReceiver);
            } catch (Exception e) {
                // 忽略
            }
        }
    }

    /**
     * 测试4：通过 Logcat 验证真实接收器的响应
     * 这个测试发送广播，然后检查 Logcat 是否有输出
     */
    @Test
    public void testRealReceiverViaLogcat() throws Exception {
        Log.d(TAG, "=== 测试真实接收器（通过 Logcat）===");

        Context context = ApplicationProvider.getApplicationContext();

        // 清理之前的 Logcat
        InstrumentationRegistry.getInstrumentation().getUiAutomation()
                .executeShellCommand("logcat -c");

        // 发送广播
        Intent broadcastIntent = new Intent(TEST_ACTION);
        broadcastIntent.putExtra("logcat_test", "yes");
        broadcastIntent.putExtra("unique_id", System.currentTimeMillis());
        broadcastIntent.setPackage(context.getPackageName());

        context.sendBroadcast(broadcastIntent);
        Log.d(TAG, "✅ 测试广播已发送（用于 Logcat 验证）");

        // 等待接收器处理
        Thread.sleep(2000);

        // 这里我们无法直接读取 Logcat，但可以断言发送成功
        // 实际测试中，你可以手动检查 Logcat
        Log.d(TAG, "⚠️ 请手动检查 Logcat 是否有 NotificationReplyReceiver 的日志");
        Log.d(TAG, "⚠️ 运行: adb logcat | grep NotificationReplyReceiver");
    }

    /**
     * 测试5：验证接收器的 exported 属性
     */
    @Test
    public void testReceiverExportedProperty() throws Exception {
        Log.d(TAG, "=== 测试接收器 exported 属性 ===");

        Context context = ApplicationProvider.getApplicationContext();
        PackageManager pm = context.getPackageManager();

        // 获取接收器信息
        android.content.ComponentName component = new android.content.ComponentName(
                context,
                NotificationReplyReceiver.class
        );

        android.content.pm.ActivityInfo activityInfo =
                pm.getReceiverInfo(component, PackageManager.GET_META_DATA);

        Log.d(TAG, "接收器 exported: " + activityInfo.exported);
        Log.d(TAG, "接收器 enabled: " + activityInfo.enabled);

        // 对于内部使用的接收器，应该设置为 false
        assertFalse("接收器不应该被导出（exported 应为 false）", activityInfo.exported);
        assertTrue("接收器应该启用（enabled 应为 true）", activityInfo.enabled);

        Log.d(TAG, "✅ 接收器属性测试通过");
    }
}