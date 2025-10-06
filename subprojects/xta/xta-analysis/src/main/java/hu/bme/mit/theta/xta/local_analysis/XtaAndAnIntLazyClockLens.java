package hu.bme.mit.theta.xta.local_analysis;

import hu.bme.mit.theta.analysis.State;
import hu.bme.mit.theta.analysis.algorithm.lazy.LazyState;
import hu.bme.mit.theta.analysis.expr.ExprState;
import hu.bme.mit.theta.analysis.prod2.Prod2State;
import hu.bme.mit.theta.core.utils.Lens;
import hu.bme.mit.theta.xta.analysis.XtaState;

public class XtaAndAnIntLazyClockLens<CConcr extends State, CAbstr extends ExprState> implements Lens<LazyState<XtaAndAnIntState<CConcr>, XtaAndAnIntState<CAbstr>>, LazyState<CConcr, CAbstr>> {
    final Lens<LazyState<XtaState<Prod2State<?, CConcr>>, XtaState<Prod2State<?, CAbstr>>>, LazyState<CConcr, CAbstr>> lens;

    private XtaAndAnIntLazyClockLens(Lens<LazyState<XtaState<Prod2State<?, CConcr>>, XtaState<Prod2State<?, CAbstr>>>, LazyState<CConcr, CAbstr>> lens) {
        this.lens = lens;
    }

    public static <CConcr extends State, CAbstr extends ExprState> XtaAndAnIntLazyClockLens<CConcr, CAbstr> of(Lens<LazyState<XtaState<Prod2State<?, CConcr>>, XtaState<Prod2State<?, CAbstr>>>, LazyState<CConcr, CAbstr>> lens) {
        return new XtaAndAnIntLazyClockLens<>(lens);
    }

    private LazyState<XtaState<Prod2State<?, CConcr>>, XtaState<Prod2State<?, CAbstr>>> extendedToNormal(LazyState<XtaAndAnIntState<CConcr>, XtaAndAnIntState<CAbstr>> lazyState) {
        var concrState = lazyState.getConcrState().getState();
        var abstrState = lazyState.getAbstrState().getState();
        return LazyState.of(XtaState.of(concrState.getLocs(), Prod2State.of(null, concrState.getState())), XtaState.of(abstrState.getLocs(), Prod2State.of(null, abstrState.getState())));
    }

    private <S extends State> XtaAndAnIntState<S> normalXtaToExtended(XtaAndAnIntState<S> origState, XtaState<Prod2State<?, S>> newState) {
        var locs = origState.getState().getLocs();
        var innerState = newState.getState().getState2();
        var r = origState.getR();
        return XtaAndAnIntState.of(XtaState.of(locs, innerState), r);
    }

    private LazyState<XtaAndAnIntState<CConcr>, XtaAndAnIntState<CAbstr>> normalToExtended(LazyState<XtaAndAnIntState<CConcr>, XtaAndAnIntState<CAbstr>> origState, LazyState<XtaState<Prod2State<?, CConcr>>, XtaState<Prod2State<?, CAbstr>>> newState) {
        return LazyState.of(
                normalXtaToExtended(origState.getConcrState(), newState.getConcrState()),
                normalXtaToExtended(origState.getAbstrState(), newState.getAbstrState())
        );
    }

    @Override
    public LazyState<CConcr, CAbstr> get(LazyState<XtaAndAnIntState<CConcr>, XtaAndAnIntState<CAbstr>> lazyState) {
        return lens.get(extendedToNormal(lazyState));
    }

    @Override
    public LazyState<XtaAndAnIntState<CConcr>, XtaAndAnIntState<CAbstr>> set(LazyState<XtaAndAnIntState<CConcr>, XtaAndAnIntState<CAbstr>> lazyState, LazyState<CConcr, CAbstr> newItpDataState) {
        var newState = lens.set(extendedToNormal(lazyState), newItpDataState);
        return normalToExtended(lazyState, newState);
    }
}
