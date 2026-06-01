package com.example.modular.feature.detail;

import androidx.annotation.Nullable;

import com.example.modular.common.service.ILastOpenedNoteProvider;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * ILastOpenedNoteProvider 的实现，由 feature-detail 提供并注册到 Hilt，
 * 供其他模块通过依赖注入获取「最近打开的笔记」。
 */
@Singleton
public class LastOpenedNoteProviderImpl implements ILastOpenedNoteProvider {

    private String lastOpenedNoteTitle;

    @Inject
    public LastOpenedNoteProviderImpl() {}

    @Nullable
    @Override
    public String getLastOpenedNoteTitle() {
        return lastOpenedNoteTitle;
    }

    @Override
    public void setLastOpenedNoteTitle(@Nullable String title) {
        this.lastOpenedNoteTitle = title;
    }
}
