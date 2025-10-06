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
import hu.bme.mit.theta.xta.local_analysis.global_local.GLXtaAction;

public class SCSystemTypeFactory<S extends State, P extends Prec> implements SystemTypeFactory<S, P> {
    private SCSystemTypeFactory() {
    }

    public static <S extends State, P extends Prec> SCSystemTypeFactory<S, P> create() {
        return new SCSystemTypeFactory<>();
    }

    @Override
    public XtaAndAnIntOrd<S> createOrd(PartialOrd<XtaState<S>> partialOrd) {
        return new SCXtaOrd<>(partialOrd);
    }

    @Override
    public XtaAndAnIntInitFunc<S, P> createInitFunc(InitFunc<XtaState<S>, P> initFunc) {
        return new SCXtaInitFunc<>(initFunc);
    }

    @Override
    public XtaAndAnIntAction createAction(XtaAction action, int r) {
        return new GLXtaAction(action, r);
    }
}
