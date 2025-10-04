package hu.bme.mit.theta.xta.local_analysis.global_local;

import hu.bme.mit.theta.analysis.PartialOrd;
import hu.bme.mit.theta.analysis.State;
import hu.bme.mit.theta.xta.analysis.XtaState;
import hu.bme.mit.theta.xta.local_analysis.XtaAndAnIntOrd;
import hu.bme.mit.theta.xta.local_analysis.XtaAndAnIntState;

public final class GLXtaOrd<S extends State> extends XtaAndAnIntOrd<S> {
    public GLXtaOrd(PartialOrd<XtaState<S>> partialOrd) {
        super(partialOrd);
    }

    @Override
    protected boolean rIsLeq(XtaAndAnIntState<S> state1, XtaAndAnIntState<S> state2) {
        return state2.getR() <= state1.getR();
    }
}
