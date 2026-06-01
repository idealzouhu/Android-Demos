package com.example.modular.base;

/**
 * 路由路径常量，供各业务模块通过路径跳转，避免模块间直接依赖。
 */
public final class RouterPath {

    private RouterPath() {}

    /** 首页（Activity，可独立打开） */
    public static final String HOME = "/home/home";
    /** 发现 */
    public static final String DISCOVER = "/discover/discover";
    /** 发布 */
    public static final String PUBLISH = "/publish/publish";
    /** 消息 */
    public static final String MESSAGE = "/message/message";
    /** 我的 */
    public static final String PROFILE = "/profile/profile";
    /** 笔记/内容详情 */
    public static final String NOTE_DETAIL = "/detail/detail";

    /** 首页 Fragment（在 MainActivity 容器内展示，底部栏常驻） */
    public static final String HOME_FRAGMENT = "/home/fragment";
    public static final String DISCOVER_FRAGMENT = "/discover/fragment";
    public static final String PUBLISH_FRAGMENT = "/publish/fragment";
    public static final String MESSAGE_FRAGMENT = "/message/fragment";
    public static final String PROFILE_FRAGMENT = "/profile/fragment";
}
