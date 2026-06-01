package com.example.glide.basic;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

/**
 * 列表适配器：使用 Generated API（GlideApp）及自定义 @GlideOption listThumb() 加载网络图。
 */
public final class ImageListAdapter extends RecyclerView.Adapter<ImageListAdapter.ViewHolder> {

    private final List<ImageItem> items = new ArrayList<>();

    public void setItems(@NonNull List<ImageItem> newItems) {
        items.clear();
        items.addAll(newItems);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_image, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ImageItem item = items.get(position);
        holder.title.setText(item.getTitle());

        // 使用 GlideApp
        GlideApp.with(holder.image.getContext()) // 绑定 Activity 的生命周期
                .load(item.getImageUrl())
                .listThumb(120)  // 调用自定义的扩展选项
                .into(holder.image);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static final class ViewHolder extends RecyclerView.ViewHolder {
        final ImageView image;
        final TextView title;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.item_image);
            title = itemView.findViewById(R.id.item_title);
        }
    }
}
