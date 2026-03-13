package com.example.modular.common.service;

import androidx.annotation.Nullable;

/**
 * 组件间通信接口：由 common 定义，由具体业务组件（如 feature-detail）实现并注入 Hilt，
 * 其他组件（如 feature-home）通过 Hilt 注入此接口获取「最近打开的笔记」信息，实现跨模块数据共享。
 */
public interface ILastOpenedNoteProvider {

    /** 获取最近一次打开的笔记标题，未打开过返回 null */
    @Nullable
    String getLastOpenedNoteTitle();

    /** 记录最近打开的笔记标题（由详情页在打开时调用） */
    void setLastOpenedNoteTitle(@Nullable String title);
}
