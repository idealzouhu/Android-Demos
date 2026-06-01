package com.example.modular.common.model;

import java.io.Serializable;

/**
 * 业务公共层：笔记/内容卡片模型，供首页、发现、详情等模块共用。
 */
public class NoteItem implements Serializable {

    private String id;
    private String title;
    private String summary;
    private String coverUrl;

    public NoteItem() {}

    public NoteItem(String id, String title, String summary, String coverUrl) {
        this.id = id;
        this.title = title;
        this.summary = summary;
        this.coverUrl = coverUrl;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }
    public String getCoverUrl() { return coverUrl; }
    public void setCoverUrl(String coverUrl) { this.coverUrl = coverUrl; }
}
