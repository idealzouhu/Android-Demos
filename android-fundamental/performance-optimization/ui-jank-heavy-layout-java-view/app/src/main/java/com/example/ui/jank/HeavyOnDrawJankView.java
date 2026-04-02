package com.example.ui.jank;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

/**
 * 故意反模式：在 {@link #onDraw} 中分配对象并做大量绘制，滑动经过时易掉帧。
 * 仅用于演示，生产代码应缓存 {@link Paint}/{@link Path} 并减少绘制次数。
 */
public class HeavyOnDrawJankView extends View {

    public HeavyOnDrawJankView(Context context) {
        super(context);
    }

    public HeavyOnDrawJankView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public HeavyOnDrawJankView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int w = getWidth();
        int h = getHeight();
        if (w <= 0 || h <= 0) {
            return;
        }

        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        for (int i = 0; i < 420; i++) {
            float x = (i * 37f) % w;
            float y = (i * 53f) % h;
            paint.setColor(0xFF000000 | ((i * 99_991) & 0xFFFFFF));
            canvas.drawCircle(x, y, 16f, paint);
        }

        Path path = new Path();
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(2f);
        for (int i = 0; i < 60; i++) {
            path.reset();
            path.moveTo(0, i * 18f);
            path.lineTo(w, i * 18f + 12f);
            canvas.drawPath(path, paint);
        }
    }
}
