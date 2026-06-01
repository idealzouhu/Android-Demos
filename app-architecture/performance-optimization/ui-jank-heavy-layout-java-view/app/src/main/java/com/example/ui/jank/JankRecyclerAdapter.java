package com.example.ui.jank;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * 故意在 {@link #onBindViewHolder} 中做重 CPU 工作，模拟列表滑动时主线程卡顿。
 */
class JankRecyclerAdapter extends RecyclerView.Adapter<JankRecyclerAdapter.Holder> {

    private static final int ITEM_COUNT = 80;

    /** 构造超长字符串的字符重复次数（越大越卡）。 */
    private static final int HEAVY_STRING_REPEAT = 12_000;

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View row =
                LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_jank_row, parent, false);
        return new Holder(row);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        StringBuilder sb = new StringBuilder(HEAVY_STRING_REPEAT + 32);
        for (int i = 0; i < HEAVY_STRING_REPEAT; i++) {
            sb.append((char) ('a' + (i % 26)));
        }
        holder.text.setText(
                holder.itemView.getContext().getString(R.string.jank_row_label, position, sb.length()));
    }

    @Override
    public int getItemCount() {
        return ITEM_COUNT;
    }

    static final class Holder extends RecyclerView.ViewHolder {
        final TextView text;

        Holder(@NonNull View itemView) {
            super(itemView);
            text = itemView.findViewById(R.id.text);
        }
    }
}
