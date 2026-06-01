package com.example.data.parse.parser;

import android.util.Xml;

import com.example.data.parse.model.RssItem;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * 使用 Android 内置的 Pull 解析器解析 RSS 2.0 XML。
 * 解析器由调用方主动控制（拉模型），可随时停止。
 */
public class RssPullParser {

    private static final String TAG_ITEM = "item";
    private static final String TAG_TITLE = "title";
    private static final String TAG_LINK = "link";
    private static final String TAG_DESCRIPTION = "description";
    private static final String TAG_PUBDATE = "pubDate";

    /**
     * 从输入流解析 RSS 中的 item 列表。
     *
     * @param inputStream XML 输入流，调用方负责关闭
     * @param charset     字符集，如 "UTF-8"
     * @return 解析得到的 RssItem 列表，失败或无数据时返回空列表
     */
    public static List<RssItem> parseItems(InputStream inputStream, String charset)
            throws XmlPullParserException, IOException {
        List<RssItem> items = new ArrayList<>();
        XmlPullParser parser = Xml.newPullParser();
        parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
        parser.setInput(inputStream, charset);

        int eventType = parser.getEventType();
        RssItem currentItem = null;

        while (eventType != XmlPullParser.END_DOCUMENT) {
            if (eventType == XmlPullParser.START_TAG) {
                String tagName = parser.getName();
                if (TAG_ITEM.equals(tagName)) {
                    currentItem = new RssItem();
                } else if (currentItem != null) {
                    switch (tagName) {
                        case TAG_TITLE:
                            currentItem.setTitle(parser.nextText());
                            break;
                        case TAG_LINK:
                            currentItem.setLink(parser.nextText());
                            break;
                        case TAG_DESCRIPTION:
                            currentItem.setDescription(parser.nextText());
                            break;
                        case TAG_PUBDATE:
                            currentItem.setPubDate(parser.nextText());
                            break;
                        default:
                            break;
                    }
                }
            } else if (eventType == XmlPullParser.END_TAG) {
                if (TAG_ITEM.equals(parser.getName()) && currentItem != null) {
                    items.add(currentItem);
                    currentItem = null;
                }
            }
            eventType = parser.next();
        }

        return items;
    }
}
