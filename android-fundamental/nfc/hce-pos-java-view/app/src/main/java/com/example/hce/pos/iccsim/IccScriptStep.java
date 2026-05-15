package com.example.hce.pos.iccsim;

import java.util.Arrays;

/** 脚本中的一步：期望的 C-APDU 与对应的 R-APDU。 */
public final class IccScriptStep {
    public final byte[] commandApdu;
    public final byte[] responseApdu;

    public IccScriptStep(byte[] commandApdu, byte[] responseApdu) {
        this.commandApdu = commandApdu;
        this.responseApdu = responseApdu;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        IccScriptStep that = (IccScriptStep) o;
        return Arrays.equals(commandApdu, that.commandApdu)
                && Arrays.equals(responseApdu, that.responseApdu);
    }

    @Override
    public int hashCode() {
        int result = Arrays.hashCode(commandApdu);
        result = 31 * result + Arrays.hashCode(responseApdu);
        return result;
    }
}
