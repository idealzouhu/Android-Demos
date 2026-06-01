package com.example.glide.basic;

/**
 * 列表项数据：网络图片 URL + 标题
 */
public final class ImageItem {
    private final String imageUrl;
    private final String title;

    public ImageItem(String imageUrl, String title) {
        this.imageUrl = imageUrl;
        this.title = title;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getTitle() {
        return title;
    }
}
