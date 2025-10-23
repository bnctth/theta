package hu.bme.mit.theta.xta.local_analysis.server_client;

import hu.bme.mit.theta.analysis.InitFunc;
import hu.bme.mit.theta.analysis.PartialOrd;
import hu.bme.mit.theta.analysis.Prec;
import hu.bme.mit.theta.analysis.State;
import hu.bme.mit.theta.xta.analysis.XtaAction;
import hu.bme.mit.theta.xta.analysis.XtaState;
import hu.bme.mit.theta.xta.local_analysis.SystemTypeFactory;
import hu.bme.mit.theta.xta.local_analysis.XtaAndAnIntAction;
import hu.bme.mit.theta.xta.local_analysis.XtaAndAnIntInitFunc;
import hu.bme.mit.theta.xta.local_analysis.XtaAndAnIntOrd;

public class SCSystemTypeFactory implements SystemTypeFactory{
    private SCSystemTypeFactory() {
    }

    public static SCSystemTypeFactory create() {
        return new SCSystemTypeFactory();
    }

    @Override
    public<S extends State>  XtaAndAnIntOrd<S> createOrd(PartialOrd<XtaState<S>> partialOrd) {
        return new SCXtaOrd<>(partialOrd);
    }

    @Override
    public <S extends State, P extends Prec> XtaAndAnIntInitFunc<S, P> createInitFunc(InitFunc<XtaState<S>, P> initFunc) {
        return new SCXtaInitFunc<>(initFunc);
    }

    @Override
    public XtaAndAnIntAction createAction(XtaAction action, int r) {
        return new SCXtaAction(action, r);
    }

    @Override
    public String toString() {
        return "Server-client factory";
    }
}
