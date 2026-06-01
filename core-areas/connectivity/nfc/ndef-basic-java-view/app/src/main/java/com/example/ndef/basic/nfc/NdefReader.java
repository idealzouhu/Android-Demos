package com.example.ndef.basic.nfc;

import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.Build;
import android.os.Parcelable;

import androidx.annotation.Nullable;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Locale;

/**
 * 从 Intent 解析 NDEF：优先 {@link NfcAdapter#EXTRA_NDEF_MESSAGES}，否则尝试 {@link
 * Ndef#getCachedNdefMessage()}。
 */
public final class NdefReader {

    public static final class ParsedResult {
        public final boolean hasRecords;
        public final String displayText;

        private ParsedResult(boolean hasRecords, String displayText) {
            this.hasRecords = hasRecords;
            this.displayText = displayText;
        }

        public static ParsedResult of(String displayText) {
            return new ParsedResult(true, displayText);
        }

        public static ParsedResult empty(String reason) {
            return new ParsedResult(false, reason);
        }
    }

    private NdefReader() {}

    public static ParsedResult parse(Intent intent) {
        Tag tag = extractTag(intent);
        NdefMessage[] messages = extractNdefMessages(intent, tag);
        if (messages == null || messages.length == 0) {
            return ParsedResult.empty(
                    "无 NDEF 数据：Intent 未携带消息且标签无缓存 NDEF（或未格式化）。");
        }
        StringBuilder sb = new StringBuilder();
        for (int mi = 0; mi < messages.length; mi++) {
            if (messages.length > 1) {
                sb.append("【消息 ").append(mi + 1).append("】\n");
            }
            NdefRecord[] records = messages[mi].getRecords();
            for (int ri = 0; ri < records.length; ri++) {
                NdefRecord record = records[ri];
                if (records.length > 1) {
                    sb.append("  记录 ").append(ri + 1).append(":\n");
                }
                appendRecord(sb, record);
                sb.append("\n");
            }
        }
        return ParsedResult.of(sb.toString().trim());
    }

    @Nullable
    public static Tag extractTag(Intent intent) {
        if (intent == null) {
            return null;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            return intent.getParcelableExtra(NfcAdapter.EXTRA_TAG, Tag.class);
        }
        return intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
    }

    @Nullable
    private static NdefMessage[] extractNdefMessages(Intent intent, @Nullable Tag tag) {
        NdefMessage[] fromIntent = getNdefMessagesExtra(intent);
        if (fromIntent != null && fromIntent.length > 0) {
            return fromIntent;
        }
        if (tag == null) {
            return null;
        }
        Ndef ndef = Ndef.get(tag);
        if (ndef == null) {
            return null;
        }
        NdefMessage cached = ndef.getCachedNdefMessage();
        if (cached == null) {
            return null;
        }
        return new NdefMessage[] {cached};
    }

    @Nullable
    private static NdefMessage[] getNdefMessagesExtra(Intent intent) {
        if (intent == null) {
            return null;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            return intent.getParcelableArrayExtra(
                    NfcAdapter.EXTRA_NDEF_MESSAGES, NdefMessage.class);
        }
        Parcelable[] raw = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
        if (raw == null) {
            return null;
        }
        int n = 0;
        for (Parcelable p : raw) {
            if (p instanceof NdefMessage) {
                n++;
            }
        }
        if (n == 0) {
            return null;
        }
        NdefMessage[] out = new NdefMessage[n];
        int i = 0;
        for (Parcelable p : raw) {
            if (p instanceof NdefMessage) {
                out[i++] = (NdefMessage) p;
            }
        }
        return out;
    }

    private static void appendRecord(StringBuilder sb, NdefRecord record) {
        if (record.getTnf() == NdefRecord.TNF_WELL_KNOWN) {
            if (Arrays.equals(record.getType(), NdefRecord.RTD_TEXT)) {
                sb.append("类型: 文本 (RTD_TEXT)\n");
                sb.append(decodeRtdText(record.getPayload()));
                return;
            }
            if (Arrays.equals(record.getType(), NdefRecord.RTD_URI)) {
                sb.append("类型: URI (RTD_URI)\n");
                String parsedUri = String.valueOf(record.toUri());
                sb.append("URI: ").append(parsedUri != null ? parsedUri : "(无法解析)");
                return;
            }
        }
        String asUri = String.valueOf(record.toUri());
        if (asUri != null) {
            sb.append("类型: URI (推断)\n");
            sb.append("URI: ").append(asUri);
            return;
        }
        if (record.getTnf() == NdefRecord.TNF_MIME_MEDIA) {
            sb.append("类型: MIME\n");
            sb.append("MIME: ").append(safeMime(record)).append("\n");
            sb.append("Payload (hex): ").append(bytesToHex(record.getPayload()));
            return;
        }
        sb.append("TNF: ").append(tnfName(record.getTnf())).append("\n");
        byte[] type = record.getType();
        if (type != null && type.length > 0) {
            sb.append("Type: ");
            appendHexOrAscii(sb, type);
            sb.append("\n");
        }
        byte[] id = record.getId();
        if (id != null && id.length > 0) {
            sb.append("Id (hex): ").append(bytesToHex(id)).append("\n");
        }
        sb.append("Payload (hex): ").append(bytesToHex(record.getPayload()));
    }

    private static String safeMime(NdefRecord record) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            byte[] t = record.getType();
            return t != null ? bytesToHex(t) : "(无类型)";
        }
        try {
            String mime = record.toMimeType();
            return mime != null ? mime : "(未知)";
        } catch (IllegalArgumentException e) {
            return "(无效 MIME)";
        }
    }

    private static String decodeRtdText(byte[] payload) {
        if (payload == null || payload.length < 1) {
            return "(空 payload)";
        }
        int status = payload[0] & 0xFF;
        int langLength = status & 0x3F;
        boolean utf16 = (status & 0x80) != 0;
        if (1 + langLength > payload.length) {
            return "(RTD_TEXT 格式无效)";
        }
        String lang =
                new String(payload, 1, langLength, StandardCharsets.US_ASCII);
        Charset charset = utf16 ? StandardCharsets.UTF_16BE : StandardCharsets.UTF_8;
        String body =
                new String(
                        payload,
                        1 + langLength,
                        payload.length - 1 - langLength,
                        charset);
        return "语言: "
                + lang
                + "\n正文: "
                + body;
    }

    private static String tnfName(short tnf) {
        switch (tnf) {
            case NdefRecord.TNF_EMPTY:
                return "EMPTY";
            case NdefRecord.TNF_WELL_KNOWN:
                return "WELL_KNOWN";
            case NdefRecord.TNF_MIME_MEDIA:
                return "MIME_MEDIA";
            case NdefRecord.TNF_ABSOLUTE_URI:
                return "ABSOLUTE_URI";
            case NdefRecord.TNF_EXTERNAL_TYPE:
                return "EXTERNAL_TYPE";
            case NdefRecord.TNF_UNKNOWN:
                return "UNKNOWN";
            case NdefRecord.TNF_UNCHANGED:
                return "UNCHANGED";
            default:
                return String.valueOf((int) tnf);
        }
    }

    private static void appendHexOrAscii(StringBuilder sb, byte[] data) {
        if (data == null) {
            sb.append("null");
            return;
        }
        boolean printable = true;
        for (byte b : data) {
            if (b < 32 || b > 126) {
                printable = false;
                break;
            }
        }
        if (printable) {
            sb.append(new String(data, StandardCharsets.UTF_8));
        } else {
            sb.append(bytesToHex(data));
        }
    }

    private static String bytesToHex(byte[] bytes) {
        if (bytes == null) {
            return "";
        }
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        for (byte b : bytes) {
            sb.append(String.format(Locale.US, "%02X", b));
        }
        return sb.toString();
    }
}
