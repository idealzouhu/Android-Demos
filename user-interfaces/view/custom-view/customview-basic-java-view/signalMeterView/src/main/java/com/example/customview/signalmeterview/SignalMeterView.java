package com.example.customview.signalmeterview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import androidx.annotation.ColorInt;
import androidx.annotation.Nullable;

/**
 * 完全自定义 View 示例：半圆弧形信号强度 / VU 表风格仪表。
 * <p>
 * 继承 {@link View}，在 {@link #onDraw(Canvas)} 中用 {@link Canvas} 绘制弧段分区与指针，
 * 在 {@link #onMeasure(int, int)} 中给出默认尺寸；可通过 XML 属性或 setter 调整量程、
 * 当前值与安全/警告/危险区颜色。
 * </p>
 *
 * @see <a href="https://developer.android.google.cn/develop/ui/views/layout/custom-views/custom-components">创建自定义视图组件</a>
 */
public class SignalMeterView extends View {

    private static final float DEFAULT_MAX = 100f;
    private static final float DEFAULT_WARNING_FROM = 0.55f;
    private static final float DEFAULT_DANGER_FROM = 0.80f;

    /** 弧从左侧 (180°) 顺时针扫过 180° 到右侧，与 {@link Canvas#drawArc} 约定一致 */
    private static final float ARC_START_DEG = 180f;
    private static final float ARC_SWEEP_DEG = 180f;

    private float maxValue = DEFAULT_MAX;
    private float currentValue;

    @ColorInt
    private int safeColor;
    @ColorInt
    private int warningColor;
    @ColorInt
    private int dangerColor;

    private float warningFrom = DEFAULT_WARNING_FROM;
    private float dangerFrom = DEFAULT_DANGER_FROM;

    private float arcStrokeWidthPx;
    private float needleWidthPx;

    private boolean showValueText = true;
    private float valueTextSizePx;
    @ColorInt
    private int valueTextColor;

    private final Paint arcPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint needlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint hubPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint valuePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final RectF arcRect = new RectF();

    public SignalMeterView(Context context) {
        this(context, null);
    }

    public SignalMeterView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SignalMeterView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    private void init(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        float defaultStroke = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 14f, getResources().getDisplayMetrics());
        float defaultNeedle = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 3f, getResources().getDisplayMetrics());
        float defaultTextSize = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_SP, 14f, getResources().getDisplayMetrics());

        safeColor = 0xFF4CAF50;
        warningColor = 0xFFFFC107;
        dangerColor = 0xFFF44336;
        arcStrokeWidthPx = defaultStroke;
        needleWidthPx = defaultNeedle;
        valueTextSizePx = defaultTextSize;
        valueTextColor = 0xFF212121;

        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(
                    attrs, R.styleable.SignalMeterView, defStyleAttr, 0);
            try {
                maxValue = a.getFloat(R.styleable.SignalMeterView_smv_maxValue, DEFAULT_MAX);
                currentValue = a.getFloat(R.styleable.SignalMeterView_smv_currentValue, 0f);
                safeColor = a.getColor(R.styleable.SignalMeterView_smv_safeColor, safeColor);
                warningColor = a.getColor(R.styleable.SignalMeterView_smv_warningColor, warningColor);
                dangerColor = a.getColor(R.styleable.SignalMeterView_smv_dangerColor, dangerColor);
                warningFrom = a.getFloat(R.styleable.SignalMeterView_smv_warningFrom, DEFAULT_WARNING_FROM);
                dangerFrom = a.getFloat(R.styleable.SignalMeterView_smv_dangerFrom, DEFAULT_DANGER_FROM);
                arcStrokeWidthPx = a.getDimension(R.styleable.SignalMeterView_smv_arcStrokeWidth, arcStrokeWidthPx);
                needleWidthPx = a.getDimension(R.styleable.SignalMeterView_smv_needleWidth, needleWidthPx);
                showValueText = a.getBoolean(R.styleable.SignalMeterView_smv_showValueText, true);
                valueTextSizePx = a.getDimension(R.styleable.SignalMeterView_smv_valueTextSize, valueTextSizePx);
                valueTextColor = a.getColor(R.styleable.SignalMeterView_smv_valueTextColor, valueTextColor);
            } finally {
                a.recycle();
            }
        }

        normalizeZoneThresholds();
        clampCurrentValue();

        arcPaint.setStyle(Paint.Style.STROKE);
        arcPaint.setStrokeCap(Paint.Cap.ROUND);
        arcPaint.setStrokeWidth(arcStrokeWidthPx);

        needlePaint.setStyle(Paint.Style.STROKE);
        needlePaint.setStrokeCap(Paint.Cap.ROUND);
        needlePaint.setColor(0xFF37474F);
        needlePaint.setStrokeWidth(needleWidthPx);

        hubPaint.setStyle(Paint.Style.FILL);
        hubPaint.setColor(0xFF37474F);

        valuePaint.setTextAlign(Paint.Align.CENTER);
        valuePaint.setTextSize(valueTextSizePx);
        valuePaint.setColor(valueTextColor);
    }

    private void normalizeZoneThresholds() {
        warningFrom = clamp01(warningFrom);
        dangerFrom = clamp01(dangerFrom);
        if (dangerFrom <= warningFrom) {
            dangerFrom = Math.min(1f, warningFrom + 0.05f);
        }
    }

    private static float clamp01(float v) {
        if (v < 0f) {
            return 0f;
        }
        if (v > 1f) {
            return 1f;
        }
        return v;
    }

    private void clampCurrentValue() {
        if (maxValue < 0f) {
            maxValue = 0f;
        }
        if (currentValue < 0f) {
            currentValue = 0f;
        } else if (maxValue > 0f && currentValue > maxValue) {
            currentValue = maxValue;
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int padW = getPaddingLeft() + getPaddingRight();
        int padH = getPaddingTop() + getPaddingBottom();
        float density = getResources().getDisplayMetrics().density;
        int defaultW = (int) (200f * density + 0.5f) + padW;
        // 上半圆 + 底部数值区
        int defaultH = (int) (140f * density + 0.5f) + padH;

        int w = resolveSize(defaultW, widthMeasureSpec);
        int h = resolveSize(defaultH, heightMeasureSpec);
        setMeasuredDimension(w, h);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        float padL = getPaddingLeft();
        float padT = getPaddingTop();
        float padR = getPaddingRight();
        float padB = getPaddingBottom();
        float innerW = getWidth() - padL - padR;
        float innerH = getHeight() - padT - padB;

        float cx = padL + innerW / 2f;
        // 圆心在底部附近，弧向上张开
        float cy = padT + innerH - arcStrokeWidthPx * 0.5f - dp(4f);
        float maxR = Math.min(innerW, innerH * 2f) / 2f - arcStrokeWidthPx;
        if (maxR <= 0f) {
            return;
        }

        arcRect.set(cx - maxR, cy - maxR, cx + maxR, cy + maxR);

        // 三段弧对应安全 / 警告 / 危险区；若需弧向平滑过渡可改为 SweepGradient 等 Shader（见官方「实现自定义绘图」）
        float swSafe = ARC_SWEEP_DEG * warningFrom;
        float swWarn = ARC_SWEEP_DEG * (dangerFrom - warningFrom);
        float swDanger = ARC_SWEEP_DEG * (1f - dangerFrom);
        arcPaint.setColor(safeColor);
        canvas.drawArc(arcRect, ARC_START_DEG, swSafe, false, arcPaint);
        arcPaint.setColor(warningColor);
        canvas.drawArc(arcRect, ARC_START_DEG + swSafe, swWarn, false, arcPaint);
        arcPaint.setColor(dangerColor);
        canvas.drawArc(arcRect, ARC_START_DEG + swSafe + swWarn, swDanger, false, arcPaint);

        float t = maxValue > 0f ? currentValue / maxValue : 0f;
        t = clamp01(t);
        float needleAngleDeg = ARC_START_DEG + ARC_SWEEP_DEG * t;
        double rad = Math.toRadians(needleAngleDeg);
        float needleLen = maxR * 0.88f;
        float x1 = cx + (float) Math.cos(rad) * needleLen;
        float y1 = cy + (float) Math.sin(rad) * needleLen;
        canvas.drawLine(cx, cy, x1, y1, needlePaint);

        float hubR = Math.max(needleWidthPx * 1.8f, dp(5f));
        canvas.drawCircle(cx, cy, hubR, hubPaint);

        if (showValueText) {
            valuePaint.setTextSize(valueTextSizePx);
            valuePaint.setColor(valueTextColor);
            String text = formatValue(currentValue);
            float textY = Math.min(cy + maxR * 0.35f + valueTextSizePx, padT + innerH - dp(4f));
            canvas.drawText(text, cx, textY, valuePaint);
        }
    }

    private float dp(float dp) {
        return TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics());
    }

    private String formatValue(float v) {
        if (Math.abs(v - Math.round(v)) < 1e-3f) {
            return String.valueOf(Math.round(v));
        }
        return String.format(java.util.Locale.US, "%.1f", v);
    }

    public float getMaxValue() {
        return maxValue;
    }

    public void setMaxValue(float maxValue) {
        this.maxValue = maxValue;
        clampCurrentValue();
        invalidate();
    }

    public float getCurrentValue() {
        return currentValue;
    }

    public void setCurrentValue(float currentValue) {
        this.currentValue = currentValue;
        clampCurrentValue();
        invalidate();
    }

    @ColorInt
    public int getSafeColor() {
        return safeColor;
    }

    public void setSafeColor(@ColorInt int safeColor) {
        this.safeColor = safeColor;
        invalidate();
    }

    @ColorInt
    public int getWarningColor() {
        return warningColor;
    }

    public void setWarningColor(@ColorInt int warningColor) {
        this.warningColor = warningColor;
        invalidate();
    }

    @ColorInt
    public int getDangerColor() {
        return dangerColor;
    }

    public void setDangerColor(@ColorInt int dangerColor) {
        this.dangerColor = dangerColor;
        invalidate();
    }

    public float getWarningFrom() {
        return warningFrom;
    }

    public void setWarningFrom(float warningFrom) {
        this.warningFrom = warningFrom;
        normalizeZoneThresholds();
        invalidate();
    }

    public float getDangerFrom() {
        return dangerFrom;
    }

    public void setDangerFrom(float dangerFrom) {
        this.dangerFrom = dangerFrom;
        normalizeZoneThresholds();
        invalidate();
    }

    public boolean isShowValueText() {
        return showValueText;
    }

    public void setShowValueText(boolean showValueText) {
        this.showValueText = showValueText;
        invalidate();
    }
}
