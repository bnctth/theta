package hu.bme.mit.theta.xta.local_analysis.global_local;

import hu.bme.mit.theta.xta.analysis.XtaAction;
import hu.bme.mit.theta.xta.local_analysis.XtaAndAnIntAction;

import static com.google.common.base.Preconditions.checkArgument;

public final class GLXtaAction extends XtaAndAnIntAction {
    public GLXtaAction(XtaAction action, int r) {
        super(action, r);
    }

    @Override
    public boolean shouldKeep() {
        checkArgument(!action.isBinary());

        var involvedProcesses = involvedProcesses();
        if (involvedProcesses.size() == 1) {
            return r <= involvedProcesses.get(0);
        }
        return action.getSourceLocs().size() == involvedProcesses.size();
    }

    @Override
    public int rPrime() {
        checkArgument(!action.isBinary());

        if (action.isBasic()) {
            return involvedProcesses().get(0);
        }

        return -1;
    }
}
