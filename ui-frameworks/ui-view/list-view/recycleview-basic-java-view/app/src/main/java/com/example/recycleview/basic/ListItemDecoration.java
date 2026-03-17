package com.example.recycleview.basic;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * 自定义 ItemDecoration 示例：在列表项之间绘制分割线。
 * <p>
 * 通过 {@link #onDraw} 在每两项之间画线，通过 {@link #getItemOffsets} 为每项预留分割线高度，
 * 避免遮挡内容。RecyclerView 会为每个 item 调用这两个方法。
 */
public class ListItemDecoration extends RecyclerView.ItemDecoration {

    private final Drawable divider;
    private final int dividerHeight;

    /**
     * @param divider       分割线 drawable，建议高度在 drawable 中定义（如 1dp）
     * @param dividerHeight 为 getItemOffsets 使用的分割线高度（像素），通常与 drawable 高度一致
     */
    public ListItemDecoration(Drawable divider, int dividerHeight) {
        this.divider = divider;
        this.dividerHeight = dividerHeight;
    }

    @Override
    public void onDraw(@NonNull Canvas c, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        int left = parent.getPaddingLeft();
        int right = parent.getWidth() - parent.getPaddingRight();
        int childCount = parent.getChildCount();
        for (int i = 0; i < childCount - 1; i++) {
            View child = parent.getChildAt(i);
            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
            int top = child.getBottom() + params.bottomMargin;
            int bottom = top + dividerHeight;
            divider.setBounds(left, top, right, bottom);
            divider.draw(c);
        }
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view,
                               @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        outRect.set(0, 0, 0, dividerHeight);
    }
}
