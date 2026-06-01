package com.example.memory.allocation.churn;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

/**
 * 演示在 {@link #onDraw} 中每帧 new Paint / Rect 的经典错误（应提升为成员变量复用）。
 */
public class BadAllocationOnDrawView extends View {

    private boolean churnInOnDraw;

    private final Paint reusedPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Rect reusedRect = new Rect();

    /**
     * 演示用「逐帧重绘」：{@link #invalidate} 触发一次 {@link #onDraw}；{@link #postOnAnimation}
     * 再把本 Runnable 排到下一帧，形成与屏幕刷新同步的循环。静止 View 默认不会每帧 onDraw，这里主动驱动以便
     * 在 Profiler 里观察每帧在 onDraw 内分配的效果。关闭 {@link #churnInOnDraw} 后不再 post，循环停止。
     */
    private final Runnable chaseFrame =
            new Runnable() {
                @Override
                public void run() {
                    invalidate();
                    if (churnInOnDraw) {
                        postOnAnimation(this);
                    }
                }
            };

    public BadAllocationOnDrawView(Context context) {
        super(context);
        init();
    }

    public BadAllocationOnDrawView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BadAllocationOnDrawView(
            Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        reusedPaint.setStyle(Paint.Style.FILL);
    }

    /** 开启后每帧在 onDraw 内分配；关闭后使用成员 Paint/Rect 绘制。 */
    public void setChurnInOnDraw(boolean enabled) {
        if (this.churnInOnDraw == enabled) {
            return;
        }
        this.churnInOnDraw = enabled;
        if (enabled) {
            postOnAnimation(chaseFrame);
        } else {
            removeCallbacks(chaseFrame);
        }
        invalidate();
    }

    public boolean isChurnInOnDraw() {
        return churnInOnDraw;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int w = getWidth();
        int h = getHeight();
        if (w <= 0 || h <= 0) {
            return;
        }

        if (churnInOnDraw) {
            Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
            paint.setStyle(Paint.Style.FILL);
            paint.setColor(0x66FF5252);
            Rect rect = new Rect(0, 0, w, h / 2);
            canvas.drawRect(rect, paint);
        } else {
            reusedPaint.setColor(0x664CAF50);
            reusedRect.set(0, 0, w, h / 2);
            canvas.drawRect(reusedRect, reusedPaint);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        removeCallbacks(chaseFrame);
        super.onDetachedFromWindow();
    }
}
