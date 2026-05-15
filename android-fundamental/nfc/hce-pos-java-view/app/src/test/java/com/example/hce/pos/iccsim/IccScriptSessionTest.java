package com.example.hce.pos.iccsim;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class IccScriptSessionTest {

    private IccScriptSession session;

    @Before
    public void setUp() {
        IccScript script = IccScriptLoader.parseJson("{"
                + "\"APDU_CMDS\":{"
                + "\"APDU0\":\"00\","
                + "\"RPDU0\":\"9000\","
                + "\"APDU1\":\"01\","
                + "\"RPDU1\":\"9100\""
                + "}"
                + "}");
        session = new IccScriptSession(script);
    }

    @Test
    public void process_advancesOnExactMatch() {
        IccProcessResult r0 = session.process(ApduHex.parseHex("00"));
        assertTrue(r0.success);
        assertEquals(1, session.getNextStepIndex());

        IccProcessResult r1 = session.process(ApduHex.parseHex("01"));
        assertTrue(r1.success);
        assertEquals(2, session.getNextStepIndex());
    }

    @Test
    public void process_failsOnMismatch() {
        IccProcessResult r = session.process(ApduHex.parseHex("FF"));
        assertFalse(r.success);
        assertEquals(0, session.getNextStepIndex());
    }

    @Test
    public void reset_restartsSequence() {
        session.process(ApduHex.parseHex("00"));
        session.reset();
        assertEquals(0, session.getNextStepIndex());
    }
}
