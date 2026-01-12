package com.example.listview.basic;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;
import java.util.Locale;

/**
 * 适配器类，用于将数据源中的数据映射到列表项视图中
 */
public class FruitAdapter extends BaseAdapter {
    private Context context;
    private List<Fruit> fruitList;
    private LayoutInflater inflater;

    public FruitAdapter(Context context, List<Fruit> fruitList) {
        this.context = context;
        this.fruitList = fruitList;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return fruitList.size();
    }

    @Override
    public Object getItem(int position) {
        return fruitList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    /**
     * 获取指定位置的列表项视图
     *
     * @param position 列表项在数据源中的位置索引
     * @param convertView 之前创建的视图实例，可用于视图复用
     * @param parent 父级ViewGroup容器
     * @return 配置好数据的View对象，用于显示在列表中
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        // 视图复用逻辑：如果convertView为空，则创建新视图；否则复用已有视图
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_fruit, parent, false);
            holder = new ViewHolder();
            holder.fruitImage = convertView.findViewById(R.id.fruit_image);
            holder.fruitName = convertView.findViewById(R.id.fruit_name);
            holder.fruitDescription = convertView.findViewById(R.id.fruit_description);
            holder.fruitPrice = convertView.findViewById(R.id.fruit_price);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Fruit fruit = fruitList.get(position);

        // 设置数据
        holder.fruitImage.setImageResource(fruit.getImageId());
        holder.fruitName.setText(fruit.getName());
        holder.fruitDescription.setText(fruit.getDescription());
        holder.fruitPrice.setText(String.format(Locale.getDefault(), "¥%.2f/斤", fruit.getPrice()));

        return convertView;
    }

    // 更新数据
    public void updateData(List<Fruit> newFruitList) {
        this.fruitList.clear();
        this.fruitList.addAll(newFruitList);
        notifyDataSetChanged();
    }

    // 添加单个水果
    public void addFruit(Fruit fruit) {
        this.fruitList.add(fruit);
        notifyDataSetChanged();
    }

    // 删除水果
    public void removeFruit(int position) {
        if (position >= 0 && position < fruitList.size()) {
            this.fruitList.remove(position);
            notifyDataSetChanged();
        }
    }

    /**
     * ViewHolder模式的实现类，用于存储列表项中的视图组件引用
     * 通过缓存findViewById的结果来提高列表滚动性能
     */
    static class ViewHolder {
        ImageView fruitImage;
        TextView fruitName;
        TextView fruitDescription;
        TextView fruitPrice;
    }

}
