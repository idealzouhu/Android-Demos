package com.example.data.parse.model;

/**
 * RSS 条目数据模型，对应 XML 中的 &lt;item&gt; 节点。
 */
public class RssItem {
    private String title;
    private String link;
    private String description;
    private String pubDate;

    public RssItem() {
    }

    public RssItem(String title, String link, String description, String pubDate) {
        this.title = title;
        this.link = link;
        this.description = description;
        this.pubDate = pubDate;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPubDate() {
        return pubDate;
    }

    public void setPubDate(String pubDate) {
        this.pubDate = pubDate;
    }

    @Override
    public String toString() {
        return "RssItem{" +
                "title='" + title + '\'' +
                ", link='" + link + '\'' +
                ", description='" + (description != null ? (description.length() > 80 ? description.substring(0, 80) + "..." : description) : "") + '\'' +
                ", pubDate='" + pubDate + '\'' +
                '}';
    }
}
