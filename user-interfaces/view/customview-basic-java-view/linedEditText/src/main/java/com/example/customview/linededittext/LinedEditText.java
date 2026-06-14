package com.example.customview.linededittext;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.TypedValue;

import androidx.annotation.ColorInt;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatEditText;

/**
 * 继承 {@link android.widget.EditText}，在 {@link #onDraw(Canvas)} 中先完成默认文字与光标绘制，
 * 再按行绘制等间距横线（可实线 / 虚线），用于「横格纸」类笔记样式。
 *
 * @see <a href="https://developer.android.google.cn/develop/ui/views/layout/custom-views/custom-components">创建自定义视图组件</a>
 */
public class LinedEditText extends AppCompatEditText {

    private final Paint linePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Rect lineRect = new Rect();
    private final Path linePath = new Path();

    @ColorInt
    private int lineColor = 0x6687899C;
    private float lineStrokeWidthPx;
    /** 0 表示使用 {@link #getLineHeight()} */
    private int customLineSpacingPx;
    private float dashWidthPx;
    private float dashGapPx;

    public LinedEditText(Context context) {
        this(context, null);
    }

    public LinedEditText(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, androidx.appcompat.R.attr.editTextStyle);
    }

    public LinedEditText(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        lineStrokeWidthPx = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 1f, getResources().getDisplayMetrics());
        readAttrs(context, attrs, defStyleAttr);
        applyPaint();
    }

    private void readAttrs(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        if (attrs == null) {
            return;
        }
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.LinedEditText, defStyleAttr, 0);
        try {
            lineColor = a.getColor(R.styleable.LinedEditText_led_lineColor, lineColor);
            lineStrokeWidthPx = a.getDimension(R.styleable.LinedEditText_led_lineStrokeWidth, lineStrokeWidthPx);
            customLineSpacingPx = a.getDimensionPixelSize(R.styleable.LinedEditText_led_lineSpacing, 0);
            dashWidthPx = a.getDimension(R.styleable.LinedEditText_led_dashWidth, 0f);
            dashGapPx = a.getDimension(R.styleable.LinedEditText_led_dashGap, 0f);
        } finally {
            a.recycle();
        }
    }

    private void applyPaint() {
        linePaint.setStyle(Paint.Style.STROKE);
        linePaint.setStrokeWidth(lineStrokeWidthPx);
        linePaint.setColor(lineColor);
        if (dashWidthPx > 0f) {
            float gap = dashGapPx > 0f ? dashGapPx : dashWidthPx;
            linePaint.setPathEffect(new DashPathEffect(new float[]{dashWidthPx, gap}, 0f));
        } else {
            linePaint.setPathEffect(null);
        }
    }

    private int resolveLineStep() {
        if (customLineSpacingPx > 0) {
            return customLineSpacingPx;
        }
        int lh = getLineHeight();
        return lh > 0 ? lh : (int) (getTextSize() * 1.15f);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        final int left = getCompoundPaddingLeft();
        final int right = getWidth() - getCompoundPaddingRight();
        final int padTop = getCompoundPaddingTop();
        final int padBottom = getCompoundPaddingBottom();
        final int innerBottom = getHeight() - padBottom;
        final int step = resolveLineStep();

        if (right <= left || innerBottom <= padTop) {
            return;
        }

        int lines = getLineCount();
        if (lines > 0) {
            for (int i = 0; i < lines; i++) {
                getLineBounds(i, lineRect);
                drawHorizontalLine(canvas, left, right, lineRect.bottom);
            }
            getLineBounds(lines - 1, lineRect);
            for (int y = lineRect.bottom + step; y < innerBottom; y += step) {
                drawHorizontalLine(canvas, left, right, y);
            }
        } else {
            for (int y = padTop + step; y < innerBottom; y += step) {
                drawHorizontalLine(canvas, left, right, y);
            }
        }
    }

    private void drawHorizontalLine(Canvas canvas, int left, int right, int y) {
        if (linePaint.getPathEffect() != null) {
            linePath.reset();
            linePath.moveTo(left, y);
            linePath.lineTo(right, y);
            canvas.drawPath(linePath, linePaint);
        } else {
            canvas.drawLine(left, y, right, y, linePaint);
        }
    }

    @ColorInt
    public int getLineColor() {
        return lineColor;
    }

    public void setLineColor(@ColorInt int lineColor) {
        this.lineColor = lineColor;
        linePaint.setColor(lineColor);
        invalidate();
    }

    public float getLineStrokeWidthPx() {
        return lineStrokeWidthPx;
    }

    public void setLineStrokeWidthPx(float lineStrokeWidthPx) {
        this.lineStrokeWidthPx = lineStrokeWidthPx;
        linePaint.setStrokeWidth(lineStrokeWidthPx);
        invalidate();
    }

    /** @return 自定义行距像素，0 表示使用 {@link #getLineHeight()} */
    public int getCustomLineSpacingPx() {
        return customLineSpacingPx;
    }

    public void setCustomLineSpacingPx(int customLineSpacingPx) {
        this.customLineSpacingPx = Math.max(0, customLineSpacingPx);
        invalidate();
    }

    public void setDash(float dashWidthPx, float dashGapPx) {
        this.dashWidthPx = Math.max(0f, dashWidthPx);
        this.dashGapPx = Math.max(0f, dashGapPx);
        applyPaint();
        invalidate();
    }
}
