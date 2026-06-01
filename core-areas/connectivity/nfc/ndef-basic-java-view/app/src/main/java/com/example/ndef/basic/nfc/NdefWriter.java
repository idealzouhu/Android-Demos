package com.example.ndef.basic.nfc;

import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;

import androidx.annotation.Nullable;

import java.io.IOException;

/**
 * 统一封装 {@link Ndef} 与 {@link NdefFormatable} 写入；空白标签首次写入走 {@link
 * NdefFormatable#format(android.nfc.NdefMessage)}。
 */
public final class NdefWriter {

    public enum Status {
        OK,
        NOT_WRITABLE,
        SIZE_EXCEEDED,
        IO_ERROR,
        FORMAT_ERROR
    }

    public static final class WriteResult {
        public final Status status;
        @Nullable public final String detail;

        public WriteResult(Status status, @Nullable String detail) {
            this.status = status;
            this.detail = detail;
        }
    }

    private NdefWriter() {}

    public static WriteResult write(Tag tag, NdefMessage message) {
        if (tag == null || message == null) {
            return new WriteResult(Status.NOT_WRITABLE, null);
        }

        Ndef ndef = Ndef.get(tag);
        if (ndef != null) {
            try {
                ndef.connect();
                if (!ndef.isWritable()) {
                    return new WriteResult(Status.NOT_WRITABLE, null);
                }
                int max = ndef.getMaxSize();
                if (message.getByteArrayLength() > max) {
                    return new WriteResult(Status.SIZE_EXCEEDED, null);
                }
                ndef.writeNdefMessage(message);
                return new WriteResult(Status.OK, null);
            } catch (FormatException e) {
                return new WriteResult(Status.FORMAT_ERROR, e.getMessage());
            } catch (IOException e) {
                return new WriteResult(Status.IO_ERROR, e.getMessage());
            } finally {
                try {
                    ndef.close();
                } catch (IOException ignored) {
                }
            }
        }

        NdefFormatable formatable = NdefFormatable.get(tag);
        if (formatable != null) {
            try {
                formatable.connect();
                formatable.format(message);
                return new WriteResult(Status.OK, null);
            } catch (FormatException e) {
                return new WriteResult(Status.FORMAT_ERROR, e.getMessage());
            } catch (IOException e) {
                return new WriteResult(Status.IO_ERROR, e.getMessage());
            } finally {
                try {
                    formatable.close();
                } catch (IOException ignored) {
                }
            }
        }

        return new WriteResult(Status.NOT_WRITABLE, null);
    }
}
