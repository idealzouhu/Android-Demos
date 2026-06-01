## 一、XML 数据解析

### 1.1 XML 数据解析

在 Android/Java 中，**SAX** 和 **Pull** 都是基于事件驱动的流式 XML 解析器，但在编程模型、控制权和实现方式上有本质区别。

| 维度                 | **SAX (Simple API for XML)**                              | **Pull 解析 (XmlPullParser)**                               |
| :------------------- | :-------------------------------------------------------- | :---------------------------------------------------------- |
| **控制模型**         | **推模型 (Push)**<br>解析器主动推送事件，调用你的回调方法 | **拉模型 (Pull)**<br>**你主动控制**解析过程，按需获取事件   |
| **编程范式**         | 事件回调（观察者模式）<br>实现 `ContentHandler` 等接口    | 迭代器模式<br>调用 `next()`、`nextTag()` 等方法推进         |
| **控制流**           | 被动接收事件<br>解析一旦开始，必须完整读完整个文档        | **完全主动控制**<br>可以随时停止解析（`break` 或 `return`） |
| **内存占用**         | 极低（流式解析，不构建 DOM）                              | 极低（流式解析，不构建 DOM）                                |
| **Android 原生支持** | 需引入 `org.xml.sax` 包                                   | **系统内置**（`android.util.Xml`）<br>推荐使用              |
| **代码复杂度**       | 回调方法分离，状态需手动维护                              | 代码线性，状态清晰，更符合直觉                              |

一般而言，推荐使用 Pull 解析方式，更加灵活一些。



### 1.2 SAX 解析方式

解析器**控制**解析流程。读取 XML 时，每遇到一个节点（如开始标签、文本、结束标签），就会自动调用你预先注册的回调方法。

```java
// SAX 示例：你实现接口，解析器调用你
public class MyHandler extends DefaultHandler {
    @Override
    public void startElement(String uri, String localName, 
                             String qName, Attributes attributes) {
        // 解析器遇到开始标签时自动调用
    }
    
    @Override
    public void characters(char[] ch, int start, int length) {
        // 解析器遇到文本时自动调用
    }
}
// 解析过程：parser.parse(xml, handler) ← 解析器驱动
```



### 1.3 PULL 解析方式

**你控制**解析流程。通过调用 `parser.next()`等方法，**主动**从流中“拉取”下一个事件，按需处理。

```java
// Pull 示例：你主动控制解析流程
XmlPullParser parser = Xml.newPullParser();
parser.setInput(stream, "UTF-8");

int eventType = parser.getEventType();
while (eventType != XmlPullParser.END_DOCUMENT) {
    if (eventType == XmlPullParser.START_TAG) {
        // 你决定何时处理开始标签
        String tagName = parser.getName();
        if ("target".equals(tagName)) {
            String value = parser.nextText(); // 主动获取文本
            // 处理完后可以直接 break; ← 随时停止！
        }
    }
    eventType = parser.next(); // 你主动推进解析
}
```





## 二、JSON 数据解析

JSON 数据解析方式主要有：

- 官方提供的 JSONObject
- Google 开源的 GJSON
- 第三方开源库 Jackson 、Fastjson