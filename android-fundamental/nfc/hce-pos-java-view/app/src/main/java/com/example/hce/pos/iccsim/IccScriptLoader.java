package com.example.hce.pos.iccsim;

import android.content.Context;
import android.content.res.AssetManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * 从 assets 或任意 {@link InputStream} 读取 ICC 脚本 JSON，解析
 * {@code APDU_CMDS} 中的 {@code APDU0..n} / {@code RPDU0..n}。
 * <p>
 * 格式适用于各类 EMV/内核测试向量（银联、外卡、MIR 等）：只要约定同一结构即可替换脚本模拟银行卡交易会话。
 */
public final class IccScriptLoader {

    /** 默认内置示例脚本；联调时可替换为自家导出 JSON（文件名须与此常量一致或改代码指向）。 */
    public static final String ASSET_FILE_NAME = "icc_apdu_script.json";

    private IccScriptLoader() {
    }

    public static IccScript loadFromAssets(Context context) throws IOException {
        AssetManager assets = context.getAssets();
        try (InputStream in = assets.open(ASSET_FILE_NAME)) {
            return load(in);
        }
    }

    public static IccScript load(InputStream in) throws IOException {
        String json = readUtf8Fully(in);
        return parseJson(json);
    }

    static IccScript parseJson(String json) {
        try {
            JSONObject root = new JSONObject(json);
            JSONObject cmds = root.getJSONObject("APDU_CMDS");
            List<IccScriptStep> steps = new ArrayList<>();
            for (int i = 0; ; i++) {
                String apduKey = "APDU" + i;
                String rpduKey = "RPDU" + i;
                if (!cmds.has(apduKey)) {
                    break;
                }
                if (!cmds.has(rpduKey)) {
                    throw new IllegalArgumentException("missing " + rpduKey);
                }
                String apduHex = cmds.getString(apduKey);
                String rpduHex = cmds.getString(rpduKey);
                steps.add(new IccScriptStep(ApduHex.parseHex(apduHex), ApduHex.parseHex(rpduHex)));
            }
            if (steps.isEmpty()) {
                throw new IllegalArgumentException("no APDU steps in APDU_CMDS");
            }
            return new IccScript(steps);
        } catch (JSONException e) {
            throw new IllegalArgumentException("invalid ICC script JSON", e);
        }
    }

    private static String readUtf8Fully(InputStream in) throws IOException {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(in, StandardCharsets.UTF_8))) {
            char[] buf = new char[4096];
            int n;
            while ((n = reader.read(buf)) >= 0) {
                sb.append(buf, 0, n);
            }
        }
        return sb.toString();
    }
}
