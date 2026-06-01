package com.example.ndef.basic.nfc;

import android.nfc.NdefRecord;

import java.nio.charset.StandardCharsets;
import java.util.Locale;

/**
 * 构造常用 {@link NdefRecord}。文本记录按 NFC Forum Well Known Type RTD_TEXT：status 字节 + 语言码 + UTF-8 正文。
 */
public final class NdefPayloads {

    private NdefPayloads() {}

    /**
     * 手写 RTD_TEXT payload：status 字节第 7 位为 0 表示 UTF-8；低 6 位为语言码（ASCII）长度。
     *
     * @param locale 用于生成 IETF 语言标签（如 zh、en）；长度须能被 6 位容纳（通常 2–5）
     * @param text 正文
     */
    public static NdefRecord textRecord(Locale locale, String text) {
        String lang = locale.toLanguageTag();
        byte[] langBytes = lang.getBytes(StandardCharsets.US_ASCII);
        if (langBytes.length > 63) {
            lang = locale.getLanguage();
            langBytes = lang.getBytes(StandardCharsets.US_ASCII);
        }
        if (langBytes.length > 63) {
            langBytes = Locale.ENGLISH.getLanguage().getBytes(StandardCharsets.US_ASCII);
        }
        byte[] textBytes = text.getBytes(StandardCharsets.UTF_8);
        byte[] payload = new byte[1 + langBytes.length + textBytes.length];
        payload[0] = (byte) langBytes.length;
        System.arraycopy(langBytes, 0, payload, 1, langBytes.length);
        System.arraycopy(textBytes, 0, payload, 1 + langBytes.length, textBytes.length);
        return new NdefRecord(
                NdefRecord.TNF_WELL_KNOWN, NdefRecord.RTD_TEXT, new byte[0], payload);
    }

    /**
     * 使用系统 {@link NdefRecord#createUri(String)}，内部会对 http/https 等 URI 做前缀压缩。
     */
    public static NdefRecord uriRecord(String uri) {
        return NdefRecord.createUri(uri);
    }
}
