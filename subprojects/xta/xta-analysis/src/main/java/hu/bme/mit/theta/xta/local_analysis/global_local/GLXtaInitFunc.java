package hu.bme.mit.theta.xta.local_analysis.global_local;

import hu.bme.mit.theta.analysis.InitFunc;
import hu.bme.mit.theta.analysis.Prec;
import hu.bme.mit.theta.analysis.State;
import hu.bme.mit.theta.xta.analysis.XtaState;
import hu.bme.mit.theta.xta.local_analysis.XtaAndAnIntInitFunc;

public final class GLXtaInitFunc<S extends State, P extends Prec> extends XtaAndAnIntInitFunc<S, P> {
    public GLXtaInitFunc(InitFunc<XtaState<S>, P> initFunc) {
        super(initFunc);
    }

    @Override
    protected int defaultR() {
        return -1;
    }
}
