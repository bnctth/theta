package hu.bme.mit.theta.xta.local_analysis;

import hu.bme.mit.theta.analysis.State;
import hu.bme.mit.theta.analysis.algorithm.lazy.LazyState;
import hu.bme.mit.theta.analysis.expr.ExprState;
import hu.bme.mit.theta.analysis.prod2.Prod2State;
import hu.bme.mit.theta.core.utils.Lens;
import hu.bme.mit.theta.xta.analysis.XtaState;

public class XtaAndAnIntLens<SConcr extends State, SAbstr extends ExprState> implements
    final Lens<LazyState<XtaState<Prod2State<DConcr, CConcr>>, XtaState<Prod2State<DAbstr, CAbstr>>>, Prod2State<DConcr, CConcr>>

    @Override
    public LazyState<SConcr, SAbstr> get(LazyState<XtaAndAnIntState<SConcr>, XtaAndAnIntState<SAbstr>> xtaAndAnIntStateXtaAndAnIntStateLazyState) {
        return null;
    }

    @Override
    public LazyState<XtaAndAnIntState<SConcr>, XtaAndAnIntState<SAbstr>> set(LazyState<XtaAndAnIntState<SConcr>, XtaAndAnIntState<SAbstr>> xtaAndAnIntStateXtaAndAnIntStateLazyState, LazyState<SConcr, SAbstr> sConcrSAbstrLazyState) {
        return null;
    }
}
