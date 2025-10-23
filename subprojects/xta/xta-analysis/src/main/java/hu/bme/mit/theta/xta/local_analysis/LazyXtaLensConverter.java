package hu.bme.mit.theta.xta.local_analysis;

import hu.bme.mit.theta.analysis.State;
import hu.bme.mit.theta.analysis.algorithm.lazy.LazyState;
import hu.bme.mit.theta.xta.analysis.XtaState;
import hu.bme.mit.theta.core.utils.Lens;

public class LazyXtaLensConverter<SConcr extends State, SAbstr extends State, S extends State> implements Lens<LazyState<XtaAndAnIntState<SConcr>, XtaAndAnIntState<SAbstr>>, S> {
    private final Lens<LazyState<XtaState<SConcr>, XtaState<SAbstr>>, S> lens;

    private LazyXtaLensConverter(Lens<LazyState<XtaState<SConcr>, XtaState<SAbstr>>, S> lens) {
        this.lens = lens;
    }

    public static <SConcr extends State, SAbstr extends State, S extends State> LazyXtaLensConverter<SConcr, SAbstr, S>
    of(Lens<LazyState<XtaState<SConcr>, XtaState<SAbstr>>, S> lens) {
        return new LazyXtaLensConverter<>(lens);
    }

    private LazyState<XtaState<SConcr>, XtaState<SAbstr>> extendedToNormal(LazyState<XtaAndAnIntState<SConcr>, XtaAndAnIntState<SAbstr>> lazyState) {
        var concrState = lazyState.getConcrState().getState();
        var abstrState = concrState.isBottom() ? null : lazyState.getAbstrState().getState();
        return LazyState.of(concrState, abstrState);
    }

    private LazyState<XtaAndAnIntState<SConcr>, XtaAndAnIntState<SAbstr>> normalToExtended(
            LazyState<XtaAndAnIntState<SConcr>, XtaAndAnIntState<SAbstr>> oldState,
            LazyState<XtaState<SConcr>, XtaState<SAbstr>> newState
    ) {
        var concrState = XtaAndAnIntState.of(newState.getConcrState(), oldState.getConcrState().getR());
        var abstrState = XtaAndAnIntState.of(newState.getAbstrState(), oldState.getAbstrState().getR());

        return LazyState.of(concrState, abstrState);
    }

    @Override
    public S get(LazyState<XtaAndAnIntState<SConcr>, XtaAndAnIntState<SAbstr>> lazyState) {
        return lens.get(extendedToNormal(lazyState));
    }

    @Override
    public LazyState<XtaAndAnIntState<SConcr>, XtaAndAnIntState<SAbstr>> set(LazyState<XtaAndAnIntState<SConcr>, XtaAndAnIntState<SAbstr>> lazyState, S dConcrCConcrProd2State) {
        var result = lens.set(extendedToNormal(lazyState), dConcrCConcrProd2State);
        return normalToExtended(lazyState, result);
    }
}
