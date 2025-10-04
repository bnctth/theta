package hu.bme.mit.theta.xta.local_analysis.server_client;

import hu.bme.mit.theta.analysis.PartialOrd;
import hu.bme.mit.theta.analysis.State;
import hu.bme.mit.theta.xta.analysis.XtaState;
import hu.bme.mit.theta.xta.local_analysis.XtaAndAnIntOrd;
import hu.bme.mit.theta.xta.local_analysis.XtaAndAnIntState;

public class SCXtaOrd<S extends State> extends XtaAndAnIntOrd<S> {
    protected SCXtaOrd(PartialOrd<XtaState<S>> partialOrd) {
        super(partialOrd);
    }

    @Override
    protected boolean rIsLeq(XtaAndAnIntState<S> state1, XtaAndAnIntState<S> state2) {
        return state2.getR() == state1.getR() || state2.getR() == 0;
    }
}
