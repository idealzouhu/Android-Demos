package com.example.hce.pos.iccsim;

import java.util.Arrays;
import java.util.List;

/**
 * 按脚本顺序逐条比对 C-APDU（与 POS 下发字节完全一致）；命中则返回对应 R-APDU 并前进一步。
 */
public final class IccScriptSession {
    private final List<IccScriptStep> steps;
    private int stepIndex;

    public IccScriptSession(IccScript script) {
        this.steps = script.getSteps();
        this.stepIndex = 0;
    }

    public void reset() {
        stepIndex = 0;
    }

    /** 下一步期望的命令序号（0..size），已完成全部步骤时为 size。 */
    public int getNextStepIndex() {
        return stepIndex;
    }

    public int getTotalSteps() {
        return steps.size();
    }

    public IccProcessResult process(byte[] commandApdu) {
        if (commandApdu == null) {
            return IccProcessResult.fail(null, "commandApdu is null");
        }
        if (stepIndex >= steps.size()) {
            return IccProcessResult.fail(null, "no more steps in script");
        }
        IccScriptStep expected = steps.get(stepIndex);
        if (!Arrays.equals(commandApdu, expected.commandApdu)) {
            return IccProcessResult.fail(expected.commandApdu, "C-APDU mismatch at step " + stepIndex);
        }
        stepIndex++;
        return IccProcessResult.ok(expected.responseApdu, expected.commandApdu);
    }
}
