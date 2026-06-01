package com.example.customview.labelededittext;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.annotation.StyleRes;

/**
 * 复合控件示例：横向 {@link LinearLayout} 组合标签 {@link TextView}、{@link EditText}
 * 与清除按钮 {@link ImageButton}；监听输入以控制清除按钮显隐，点击清除清空文本。
 *
 * @see <a href="https://developer.android.google.cn/develop/ui/views/layout/custom-views/custom-components">创建自定义视图组件</a>
 */
public class LabeledEditText extends LinearLayout {

    private final TextView labelView;
    private final EditText editText;
    private final ImageButton clearButton;

    private boolean showClearButton = true;

    private final TextWatcher clearVisibilityWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            updateClearButtonVisibility();
        }
    };

    public LabeledEditText(Context context) {
        this(context, null);
    }

    public LabeledEditText(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LabeledEditText(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setOrientation(HORIZONTAL);
        LayoutInflater.from(context).inflate(R.layout.labeled_edit_text, this, true);
        labelView = findViewById(R.id.let_label);
        editText = findViewById(R.id.let_input);
        clearButton = findViewById(R.id.let_clear);

        applyAttributes(context, attrs, defStyleAttr);

        editText.addTextChangedListener(clearVisibilityWatcher);
        clearButton.setOnClickListener(v -> {
            editText.setText("");
            editText.requestFocus();
        });

        updateClearButtonVisibility();
    }

    private void applyAttributes(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        if (attrs == null) {
            return;
        }
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.LabeledEditText, defStyleAttr, 0);
        try {
            if (a.hasValue(R.styleable.LabeledEditText_let_labelText)) {
                labelView.setText(a.getText(R.styleable.LabeledEditText_let_labelText));
            }
            if (a.hasValue(R.styleable.LabeledEditText_let_labelTextAppearance)) {
                int resId = a.getResourceId(R.styleable.LabeledEditText_let_labelTextAppearance, 0);
                if (resId != 0) {
                    labelView.setTextAppearance(resId);
                }
            }
            if (a.hasValue(R.styleable.LabeledEditText_let_labelMinWidth)) {
                int px = a.getDimensionPixelSize(R.styleable.LabeledEditText_let_labelMinWidth, 0);
                labelView.setMinWidth(px);
            }
            if (a.hasValue(R.styleable.LabeledEditText_android_hint)) {
                editText.setHint(a.getText(R.styleable.LabeledEditText_android_hint));
            }
            if (a.hasValue(R.styleable.LabeledEditText_android_inputType)) {
                editText.setInputType(a.getInt(R.styleable.LabeledEditText_android_inputType, editText.getInputType()));
            }
            if (a.hasValue(R.styleable.LabeledEditText_android_imeOptions)) {
                editText.setImeOptions(a.getInt(R.styleable.LabeledEditText_android_imeOptions, editText.getImeOptions()));
            }
            if (a.hasValue(R.styleable.LabeledEditText_android_maxLines)) {
                editText.setMaxLines(a.getInt(R.styleable.LabeledEditText_android_maxLines, 1));
            }
            if (a.hasValue(R.styleable.LabeledEditText_let_editTextAppearance)) {
                int resId = a.getResourceId(R.styleable.LabeledEditText_let_editTextAppearance, 0);
                if (resId != 0) {
                    editText.setTextAppearance(resId);
                }
            }
            showClearButton = a.getBoolean(R.styleable.LabeledEditText_let_showClearButton, true);
            if (a.hasValue(R.styleable.LabeledEditText_let_clearIcon)) {
                clearButton.setImageDrawable(a.getDrawable(R.styleable.LabeledEditText_let_clearIcon));
            }
            if (a.hasValue(R.styleable.LabeledEditText_let_clearContentDescription)) {
                CharSequence cd = a.getText(R.styleable.LabeledEditText_let_clearContentDescription);
                if (cd != null) {
                    clearButton.setContentDescription(cd);
                }
            }
        } finally {
            a.recycle();
        }
    }

    private void updateClearButtonVisibility() {
        if (!showClearButton) {
            clearButton.setVisibility(GONE);
            return;
        }
        boolean hasText = editText.getText() != null && editText.getText().length() > 0;
        clearButton.setVisibility(hasText ? VISIBLE : GONE);
    }

    public CharSequence getLabelText() {
        return labelView.getText();
    }

    public void setLabelText(@Nullable CharSequence text) {
        labelView.setText(text);
    }

    public void setLabelText(@StringRes int resId) {
        labelView.setText(resId);
    }

    public Editable getEditTextValue() {
        return editText.getText();
    }

    public void setEditTextValue(@Nullable CharSequence text) {
        editText.setText(text);
    }

    @Nullable
    public CharSequence getHint() {
        return editText.getHint();
    }

    public void setHint(@Nullable CharSequence hint) {
        editText.setHint(hint);
    }

    public EditText getEditText() {
        return editText;
    }

    public TextView getLabelView() {
        return labelView;
    }

    public boolean isShowClearButton() {
        return showClearButton;
    }

    public void setShowClearButton(boolean show) {
        showClearButton = show;
        updateClearButtonVisibility();
    }

    public void setClearIcon(@DrawableRes int resId) {
        clearButton.setImageResource(resId);
    }

    public void setClearContentDescription(@Nullable CharSequence description) {
        clearButton.setContentDescription(description);
    }

    public void setLabelTextAppearance(@StyleRes int resId) {
        labelView.setTextAppearance(resId);
    }
}
