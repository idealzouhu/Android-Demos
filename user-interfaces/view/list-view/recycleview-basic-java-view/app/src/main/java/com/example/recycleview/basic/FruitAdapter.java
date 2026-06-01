package com.example.recycleview.basic;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Locale;

/**
 * 列表适配器：基于 RecyclerView.Adapter，将数据映射到列表项视图。
 * 与 ListView 的 BaseAdapter 对应，列表项数量等于数据源大小，可正常滚到末尾。
 */
public class FruitAdapter extends RecyclerView.Adapter<FruitAdapter.ViewHolder> {

    private final Context context;
    private List<Fruit> fruitList;
    private final LayoutInflater inflater;

    private OnItemClickListener itemClickListener;
    private OnItemLongClickListener itemLongClickListener;

    public interface OnItemClickListener {
        void onItemClick(View view, int realPosition);
    }

    public interface OnItemLongClickListener {
        boolean onItemLongClick(View view, int realPosition);
    }

    public FruitAdapter(Context context, List<Fruit> fruitList) {
        this.context = context;
        this.fruitList = fruitList;
        this.inflater = LayoutInflater.from(context);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.itemClickListener = listener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener listener) {
        this.itemLongClickListener = listener;
    }

    @Override
    public int getItemCount() {
        return fruitList == null ? 0 : fruitList.size();
    }

    /**
     * 创建并返回 ViewHolder 实例
     *
     * @param parent   父视图组，用于将布局添加到其中
     * @param viewType 视图类型标识符
     * @return 新的 ViewHolder 实例，用于持有 item 视图
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_fruit, parent, false);
        return new ViewHolder(view);
    }

    /**
     * 绑定数据到 ViewHolder，设置水果的各项信息并注册点击和长按事件
     *
     * @param holder   要绑定数据的 ViewHolder
     * @param position 数据在列表中的位置索引
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Fruit fruit = fruitList.get(position);

        holder.fruitImage.setImageResource(fruit.getImageId());
        holder.fruitName.setText(fruit.getName());
        holder.fruitDescription.setText(fruit.getDescription());
        holder.fruitPrice.setText(String.format(Locale.getDefault(), "¥%.2f/斤", fruit.getPrice()));

        holder.itemView.setOnClickListener(v -> {
            int pos = holder.getBindingAdapterPosition();
            if (pos != RecyclerView.NO_POSITION && itemClickListener != null) {
                itemClickListener.onItemClick(v, pos);
            }
        });
        holder.itemView.setOnLongClickListener(v -> {
            int pos = holder.getBindingAdapterPosition();
            if (pos != RecyclerView.NO_POSITION && itemLongClickListener != null) {
                return itemLongClickListener.onItemLongClick(v, pos);
            }
            return false;
        });
    }

    public void updateData(List<Fruit> newFruitList) {
        int oldSize = fruitList.size();
        this.fruitList.clear();
        this.fruitList.addAll(newFruitList);
        int newSize = fruitList.size();
        if (oldSize > 0) notifyItemRangeRemoved(0, oldSize);
        if (newSize > 0) notifyItemRangeInserted(0, newSize);
    }

    public void addFruit(Fruit fruit) {
        int position = fruitList.size();
        this.fruitList.add(fruit);
        notifyItemInserted(position);
    }

    public void removeFruit(int realPosition) {
        if (realPosition >= 0 && realPosition < fruitList.size()) {
            this.fruitList.remove(realPosition);
            notifyItemRemoved(realPosition);
        }
    }

    public List<Fruit> getFruitList() {
        return fruitList;
    }

    /**
     * ViewHolder 静态内部类，用于缓存 item 布局中的各个视图组件
     * 避免每次滚动时重复调用 findViewById，提升列表性能
     */
    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView fruitImage;
        TextView fruitName;
        TextView fruitDescription;
        TextView fruitPrice;

        ViewHolder(View itemView) {
            super(itemView);
            fruitImage = itemView.findViewById(R.id.fruit_image);
            fruitName = itemView.findViewById(R.id.fruit_name);
            fruitDescription = itemView.findViewById(R.id.fruit_description);
            fruitPrice = itemView.findViewById(R.id.fruit_price);
        }
    }
}
