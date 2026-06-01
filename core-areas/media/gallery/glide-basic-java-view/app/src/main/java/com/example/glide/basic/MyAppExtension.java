package com.example.glide.basic;

import android.graphics.Bitmap;

import androidx.annotation.NonNull;

import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.annotation.GlideExtension;
import com.bumptech.glide.annotation.GlideOption;
import com.bumptech.glide.annotation.GlideType;
import com.bumptech.glide.load.resource.bitmap.BitmapTransitionOptions;
import com.bumptech.glide.request.BaseRequestOptions;
import com.bumptech.glide.request.RequestOptions;

/**
 * 扩展 Generated API：提供 @GlideOption 与 @GlideType 示例。
 * 编译后可通过 GlideApp 使用 listThumb()、asBitmapWithDefaults()。
 */
@GlideExtension
public final class MyAppExtension {

    /** 列表缩略图默认边长（px） */
    private static final int DEFAULT_LIST_THUMB_SIZE = 320;

    private MyAppExtension() {
        // 工具类，禁止实例化
    }

    /**
     * @GlideOption：列表项缩略图通用配置（居中缩放、固定尺寸、占位/错误图）
     */
    @NonNull
    @GlideOption
    public static BaseRequestOptions<?> listThumb(BaseRequestOptions<?> options) {
        return options
                .centerCrop()
                .override(DEFAULT_LIST_THUMB_SIZE)
                .placeholder(R.drawable.placeholder_image)
                .error(R.drawable.placeholder_error);
    }

    /**
     * @GlideOption 带参数：可指定缩略图边长
     */
    @NonNull
    @GlideOption
    public static BaseRequestOptions<?> listThumb(BaseRequestOptions<?> options, int sizePx) {
        return options
                .centerCrop()
                .override(sizePx)
                .placeholder(R.drawable.placeholder_image)
                .error(R.drawable.placeholder_error);
    }

    /**
     * @GlideType：以 Bitmap 加载时的默认选项（如过渡动画、默认占位）
     */
    @NonNull
    @GlideType(Bitmap.class)
    public static RequestBuilder<Bitmap> asBitmapWithDefaults(RequestBuilder<Bitmap> requestBuilder) {
        return requestBuilder
                .apply(new RequestOptions()
                        .placeholder(R.drawable.placeholder_image)
                        .error(R.drawable.placeholder_error))
                .transition(BitmapTransitionOptions.withCrossFade());
    }
}
