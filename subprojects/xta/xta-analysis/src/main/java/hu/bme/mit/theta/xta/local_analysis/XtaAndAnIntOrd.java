package hu.bme.mit.theta.xta.local_analysis;

import hu.bme.mit.theta.analysis.PartialOrd;
import hu.bme.mit.theta.analysis.State;
import hu.bme.mit.theta.xta.analysis.XtaState;

import static com.google.common.base.Preconditions.checkNotNull;


public abstract class XtaAndAnIntOrd<S extends State> implements PartialOrd<XtaAndAnIntState<S>> {
    private final PartialOrd<XtaState<S>> partialOrd;

    protected XtaAndAnIntOrd(final PartialOrd<XtaState<S>> partialOrd) {
        this.partialOrd = checkNotNull(partialOrd);
    }

    @Override
    public final boolean isLeq(final XtaAndAnIntState<S> state1, final XtaAndAnIntState<S> state2) {
        checkNotNull(state1);
        checkNotNull(state2);
        return partialOrd.isLeq(state1.getState(), state2.getState()) && rIsLeq(state1, state2);
    }

    abstract protected boolean rIsLeq(final XtaAndAnIntState<S> state1, final XtaAndAnIntState<S> state2);
}
