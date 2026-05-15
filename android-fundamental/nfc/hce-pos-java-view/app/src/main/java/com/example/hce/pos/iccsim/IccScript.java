package com.example.hce.pos.iccsim;

import java.util.Collections;
import java.util.List;

/** 有序 ICC 脚本步骤列表（与 JSON 中 {@code APDU0}/{@code RPDU0}… 顺序一致）。 */
public final class IccScript {
    private final List<IccScriptStep> steps;

    public IccScript(List<IccScriptStep> steps) {
        this.steps = Collections.unmodifiableList(steps);
    }

    public List<IccScriptStep> getSteps() {
        return steps;
    }

    public int size() {
        return steps.size();
    }
}
