package com.example.hce.pos.iccsim;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

public class IccScriptLoaderTest {

    private static final String SAMPLE_JSON = "{"
            + "\"APDU_CMDS\":{"
            + "\"APDU0\":\"00A40400\","
            + "\"RPDU0\":\"9000\","
            + "\"APDU1\":\"00A40401\","
            + "\"RPDU1\":\"9100\""
            + "}"
            + "}";

    @Test
    public void parseJson_loadsOrderedSteps() {
        IccScript script = IccScriptLoader.parseJson(SAMPLE_JSON);
        assertEquals(2, script.size());
        assertEquals(3, script.getSteps().get(0).commandApdu.length);
        assertEquals(2, script.getSteps().get(0).responseApdu.length);
        assertEquals((byte) 0x91, script.getSteps().get(1).responseApdu[0]);
    }

    @Test
    public void parseJson_requiresMatchingRpdu() {
        String bad = "{\"APDU_CMDS\":{\"APDU0\":\"9000\"}}";
        assertThrows(IllegalArgumentException.class, () -> IccScriptLoader.parseJson(bad));
    }
}
