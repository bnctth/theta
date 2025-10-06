package hu.bme.mit.theta.xta.local_analysis.server_client;

import hu.bme.mit.theta.xta.analysis.XtaAction;
import hu.bme.mit.theta.xta.local_analysis.XtaAndAnIntAction;

import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;

public final class SCXtaAction extends XtaAndAnIntAction {
    public SCXtaAction(XtaAction action, int r) {
        super(action, r);
    }

    @Override
    public boolean shouldKeep() {
        checkArgument(!action.isBroadcast());

        return r == 0 || involvedProcesses().contains(r);
    }

    @Override
    public int rPrime() {
        List<Integer> involvedProcesses = involvedProcesses();
        if (involvedProcesses.contains(0)) {
            return 0;
        }
        assert involvedProcesses.size() == 1 && involvedProcesses.get(0) > 0;
        return involvedProcesses.get(0);
    }
}
