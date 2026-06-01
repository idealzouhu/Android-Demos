package com.example.memory.allocation.churn;

import android.util.Log;

/**
 * 单一热点：循环中用 + 拼接 {@link String}。每次迭代产生新 {@link String}，字符拷贝总量近似 O(n²)，
 * 极易在 Profiler 里看到分配峰值与 GC 抖动。正确写法应使用 {@link StringBuilder}（或预估长度的一次性构建）。
 */
final class AllocationChurnDemos {

    private static final String TAG = "AllocationChurn";

    private AllocationChurnDemos() {}

    static void stringConcatPlusInLoop(int iterations) {
        String s = "";
        for (int i = 0; i < iterations; i++) {
            s = s + i;
        }
        Log.d(TAG, "stringConcatPlusInLoop done, len=" + s.length());
    }
}
