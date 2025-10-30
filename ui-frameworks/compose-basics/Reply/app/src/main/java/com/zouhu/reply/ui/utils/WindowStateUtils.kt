package com.zouhu.reply.ui.utils

/**
 * 定义适用于不同规格屏幕的导航栏类型
 */
enum class ReplyNavigationType {
    BOTTOM_NAVIGATION, NAVIGATION_RAIL, PERMANENT_NAVIGATION_DRAWER
}

/**
 * 定义大屏幕的规范布局
 */
enum class ReplyContentType {
    LIST_ONLY,
    LIST_AND_DETAIL // 适用列表视图
}