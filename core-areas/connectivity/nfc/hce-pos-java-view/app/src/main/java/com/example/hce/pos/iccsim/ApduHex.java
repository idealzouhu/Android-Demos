package com.example.hce.pos.iccsim;

import java.util.Locale;

/**
 * APDU 十六进制字符串与字节数组互转；忽略空白，大小写不敏感。
 */
public final class ApduHex {

    private ApduHex() {
    }

    public static byte[] parseHex(String hex) {
        if (hex == null) {
            throw new IllegalArgumentException("hex is null");
        }
        String compact = hex.replaceAll("\\s+", "").toUpperCase(Locale.ROOT);
        if ((compact.length() & 1) != 0) {
            throw new IllegalArgumentException("hex length must be even");
        }
        int n = compact.length() / 2;
        byte[] out = new byte[n];
        for (int i = 0; i < n; i++) {
            int hi = digit(compact.charAt(i * 2));
            int lo = digit(compact.charAt(i * 2 + 1));
            out[i] = (byte) ((hi << 4) | lo);
        }
        return out;
    }

    public static String toHex(byte[] data) {
        if (data == null) {
            return "null";
        }
        StringBuilder sb = new StringBuilder(data.length * 2);
        for (byte b : data) {
            sb.append(String.format(Locale.ROOT, "%02X", b));
        }
        return sb.toString();
    }

    private static int digit(char c) {
        if (c >= '0' && c <= '9') {
            return c - '0';
        }
        if (c >= 'A' && c <= 'F') {
            return 10 + (c - 'A');
        }
        throw new IllegalArgumentException("invalid hex digit: " + c);
    }
}
