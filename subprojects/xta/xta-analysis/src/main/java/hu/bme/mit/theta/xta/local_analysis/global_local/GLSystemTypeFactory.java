package hu.bme.mit.theta.xta.local_analysis.global_local;

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
import hu.bme.mit.theta.xta.local_analysis.server_client.SCXtaAction;

public class GLSystemTypeFactory implements SystemTypeFactory {
    private GLSystemTypeFactory() {
    }

    public static GLSystemTypeFactory create() {
        return new GLSystemTypeFactory();
    }

    @Override
    public <S extends State> XtaAndAnIntOrd<S> createOrd(PartialOrd<XtaState<S>> partialOrd) {
        return new GLXtaOrd<>(partialOrd);
    }

    @Override
    public <S extends State, P extends Prec>  XtaAndAnIntInitFunc<S, P> createInitFunc(InitFunc<XtaState<S>, P> initFunc) {
        return new GLXtaInitFunc<>(initFunc);
    }

    @Override
    public XtaAndAnIntAction createAction(XtaAction action, int r) {
        return new SCXtaAction(action, r);
    }
}
