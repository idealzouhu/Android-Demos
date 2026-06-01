package com.example.modular.feature.home;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.modular.common.model.NoteItem;

import java.util.List;

class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.VH> {

    private final List<NoteItem> list;
    private final OnItemClick onItemClick;

    interface OnItemClick {
        void onClick(NoteItem item);
    }

    HomeAdapter(List<NoteItem> list, OnItemClick onItemClick) {
        this.list = list;
        this.onItemClick = onItemClick;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.home_item_note, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        NoteItem item = list.get(position);
        holder.title.setText(item.getTitle());
        holder.summary.setText(item.getSummary());
        holder.itemView.setOnClickListener(v -> {
            if (onItemClick != null) onItemClick.onClick(item);
        });
    }

    @Override
    public int getItemCount() {
        return list != null ? list.size() : 0;
    }

    static class VH extends RecyclerView.ViewHolder {
        TextView title, summary;

        VH(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.home_item_title);
            summary = itemView.findViewById(R.id.home_item_summary);
        }
    }
}
