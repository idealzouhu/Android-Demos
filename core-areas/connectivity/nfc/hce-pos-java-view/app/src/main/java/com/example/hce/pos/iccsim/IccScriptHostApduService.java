package com.example.hce.pos.iccsim;

import android.nfc.cardemulation.HostApduService;
import android.os.Bundle;
import android.util.Log;

import java.io.IOException;

/**
 * 按 ICC 脚本（JSON 向量）响应读卡器 APDU，用于在授权环境下模拟银行卡联机/非接交易会话。
 * C-APDU 须与脚本字节完全一致；更换脚本即可适配不同内核或案例。
 */
public class IccScriptHostApduService extends HostApduService {

    private static final String TAG = "IccScriptHostApduService";
    /** 不匹配或其它脚本错误时返回的 SW。 */
    private static final byte[] SW_MISMATCH = new byte[]{(byte) 0x6F, 0x00};

    private IccScriptSession session;

    @Override
    public void onCreate() {
        super.onCreate();
        try {
            IccScript script = IccScriptLoader.loadFromAssets(this);
            session = new IccScriptSession(script);
            IccHceDebugState.setScriptLoaded(true, null);
            IccHceDebugState.updateSessionProgress(session.getNextStepIndex(), session.getTotalSteps());
        } catch (IOException e) {
            Log.e(TAG, "Failed to load ICC script from assets", e);
            IccHceDebugState.setScriptLoaded(false, e.getMessage());
            session = null;
        }
    }

    @Override
    public byte[] processCommandApdu(byte[] commandApdu, Bundle extras) {
        if (session == null) {
            Log.e(TAG, "Session not initialized; command=" + ApduHex.toHex(commandApdu));
            return SW_MISMATCH;
        }
        IccProcessResult result = session.process(commandApdu);
        IccHceDebugState.updateSessionProgress(session.getNextStepIndex(), session.getTotalSteps());
        if (result.success) {
            return result.responseApdu;
        }
        logMismatch(commandApdu, result);
        return SW_MISMATCH;
    }

    private void logMismatch(byte[] commandApdu, IccProcessResult result) {
        String actual = ApduHex.toHex(commandApdu);
        String expected = result.expectedCommandApdu != null
                ? ApduHex.toHex(result.expectedCommandApdu)
                : "(none)";
        Log.e(TAG, result.failureReason
                + "; actual=" + actual
                + "; expected=" + expected);
    }

    @Override
    public void onDeactivated(int reason) {
        if (session != null) {
            session.reset();
            IccHceDebugState.updateSessionProgress(session.getNextStepIndex(), session.getTotalSteps());
        }
    }
}
