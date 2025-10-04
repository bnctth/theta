package hu.bme.mit.theta.xta.local_analysis.server_client;

import hu.bme.mit.theta.analysis.InitFunc;
import hu.bme.mit.theta.analysis.Prec;
import hu.bme.mit.theta.analysis.State;
import hu.bme.mit.theta.xta.analysis.XtaState;
import hu.bme.mit.theta.xta.local_analysis.XtaAndAnIntInitFunc;

public final class SCXtaInitFunc<S extends State, P extends Prec> extends XtaAndAnIntInitFunc<S, P> {
    public SCXtaInitFunc(InitFunc<XtaState<S>, P> initFunc) {
        super(initFunc);
    }

    @Override
    protected int defaultR() {
        return 0;
    }
}
