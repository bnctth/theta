package hu.bme.mit.theta.xta.local_analysis.global_local;

import hu.bme.mit.theta.analysis.InitFunc;
import hu.bme.mit.theta.analysis.PartialOrd;
import hu.bme.mit.theta.analysis.Prec;
import hu.bme.mit.theta.analysis.State;
import hu.bme.mit.theta.xta.analysis.XtaState;
import hu.bme.mit.theta.xta.local_analysis.SystemTypeFactory;
import hu.bme.mit.theta.xta.local_analysis.XtaAndAnIntInitFunc;
import hu.bme.mit.theta.xta.local_analysis.XtaAndAnIntOrd;

public class GLSystemTypeFactory<S extends State, P extends Prec> implements SystemTypeFactory<S, P> {
    private GLSystemTypeFactory() {
    }

    public static <S extends State, P extends Prec> GLSystemTypeFactory<S, P> create() {
        return new GLSystemTypeFactory<>();
    }

    @Override
    public XtaAndAnIntOrd<S> createOrd(PartialOrd<XtaState<S>> partialOrd) {
        return new GLXtaOrd<>(partialOrd);
    }

    @Override
    public XtaAndAnIntInitFunc<S, P> createInitFunc(InitFunc<XtaState<S>, P> initFunc) {
        return new GLXtaInitFunc<>(initFunc);
    }
}
