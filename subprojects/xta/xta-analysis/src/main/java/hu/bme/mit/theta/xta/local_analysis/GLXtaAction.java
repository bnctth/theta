package hu.bme.mit.theta.xta.local_analysis;

import hu.bme.mit.theta.xta.analysis.XtaAction;

import static com.google.common.base.Preconditions.checkArgument;

public class GLXtaAction extends XtaAndAnIntAction {
    public GLXtaAction(XtaAction action, int r) {
        super(action, r);
    }

    public static ActionFactory factory() {
        return GLXtaAction::new;
    }

    @Override
    boolean shouldKeep() {
        checkArgument(!action.isBinary());

        var involvedProcesses = involvedProcesses();
        if (involvedProcesses.size() == 1) {
            return r <= involvedProcesses.get(0);
        }
        return action.getSourceLocs().size() == involvedProcesses.size();
    }

    @Override
    int rPrime() {
        checkArgument(!action.isBinary());

        if (action.isBasic()) {
            return involvedProcesses().get(0);
        }

        return -1;
    }
}
