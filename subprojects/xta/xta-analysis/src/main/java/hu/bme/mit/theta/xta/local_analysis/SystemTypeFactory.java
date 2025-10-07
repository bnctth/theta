package hu.bme.mit.theta.xta.local_analysis;

import hu.bme.mit.theta.analysis.InitFunc;
import hu.bme.mit.theta.analysis.PartialOrd;
import hu.bme.mit.theta.analysis.Prec;
import hu.bme.mit.theta.analysis.State;
import hu.bme.mit.theta.xta.analysis.XtaAction;
import hu.bme.mit.theta.xta.analysis.XtaState;

public interface SystemTypeFactory {
    <S extends State> XtaAndAnIntOrd<S> createOrd(PartialOrd<XtaState<S>> partialOrd);

    <S extends State, P extends Prec> XtaAndAnIntInitFunc<S, P> createInitFunc(InitFunc<XtaState<S>, P> initFunc);

    XtaAndAnIntAction createAction(XtaAction action, int r);
}
