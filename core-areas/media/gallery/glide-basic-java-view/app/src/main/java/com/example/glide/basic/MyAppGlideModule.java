package com.example.glide.basic;

import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.module.AppGlideModule;

/**
 * 应用级 Glide 模块，用于启用 Generated API（GlideApp）。
 * 集成库（如 OkHttp）通过 LibraryGlideModule 自动注册，无需在此重复注册。
 */
@GlideModule
public final class MyAppGlideModule extends AppGlideModule {

    @Override
    public boolean isManifestParsingEnabled() {
        return false;
    }
}
