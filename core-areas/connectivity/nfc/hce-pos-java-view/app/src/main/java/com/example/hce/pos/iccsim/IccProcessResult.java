package com.example.hce.pos.iccsim;

/** {@link IccScriptSession#process(byte[])} 的结果。 */
public final class IccProcessResult {
    public final boolean success;
    public final byte[] responseApdu;
    /** 当前步期望的 C-APDU（成功或失败时均可用于日志）。 */
    public final byte[] expectedCommandApdu;
    public final String failureReason;

    private IccProcessResult(
            boolean success,
            byte[] responseApdu,
            byte[] expectedCommandApdu,
            String failureReason) {
        this.success = success;
        this.responseApdu = responseApdu;
        this.expectedCommandApdu = expectedCommandApdu;
        this.failureReason = failureReason;
    }

    public static IccProcessResult ok(byte[] responseApdu, byte[] matchedExpectedCommand) {
        return new IccProcessResult(true, responseApdu, matchedExpectedCommand, null);
    }

    public static IccProcessResult fail(byte[] expectedCommandApdu, String reason) {
        return new IccProcessResult(false, null, expectedCommandApdu, reason);
    }
}
