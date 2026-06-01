package com.example.hce.pos.iccsim;

import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertThrows;

public class ApduHexTest {

    @Test
    public void parseHex_ignoresWhitespace_andCase() {
        byte[] out = ApduHex.parseHex(" 00 ff\nAb ");
        assertArrayEquals(new byte[]{0x00, (byte) 0xFF, (byte) 0xAB}, out);
    }

    @Test
    public void toHex_roundTrip() {
        byte[] data = new byte[]{0x00, (byte) 0xA4, 0x04, 0x00};
        assertArrayEquals(data, ApduHex.parseHex(ApduHex.toHex(data)));
    }

    @Test
    public void parseHex_rejectsOddLength() {
        assertThrows(IllegalArgumentException.class, () -> ApduHex.parseHex("0"));
    }
}
