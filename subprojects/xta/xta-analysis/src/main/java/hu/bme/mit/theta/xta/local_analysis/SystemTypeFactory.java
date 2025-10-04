package hu.bme.mit.theta.xta.local_analysis;

import hu.bme.mit.theta.analysis.InitFunc;
import hu.bme.mit.theta.analysis.PartialOrd;
import hu.bme.mit.theta.analysis.Prec;
import hu.bme.mit.theta.analysis.State;
import hu.bme.mit.theta.xta.analysis.XtaState;

public interface SystemTypeFactory<S extends State, P extends Prec> {
    public XtaAndAnIntOrd<S> createOrd(PartialOrd<XtaState<S>> partialOrd);

    public XtaAndAnIntInitFunc<S, P> createInitFunc(InitFunc<XtaState<S>, P> initFunc);
}
