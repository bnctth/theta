package hu.bme.mit.theta.xta.local_analysis;

import hu.bme.mit.theta.analysis.State;
import hu.bme.mit.theta.analysis.algorithm.lazy.LazyState;
import hu.bme.mit.theta.analysis.prod2.Prod2State;
import hu.bme.mit.theta.xta.analysis.XtaState;
import hu.bme.mit.theta.core.utils.Lens;

public class LazyXtaLensConverter<DConcr extends State, CConcr extends State, DAbstr extends State, CAbstr extends State> implements Lens<LazyState<XtaAndAnIntState<Prod2State<DConcr, CConcr>>, XtaAndAnIntState<Prod2State<DAbstr, CAbstr>>>, Prod2State<DConcr, CConcr>> {
    private final Lens<LazyState<XtaState<Prod2State<DConcr, CConcr>>, XtaState<Prod2State<DAbstr, CAbstr>>>, Prod2State<DConcr, CConcr>> lens;

    private LazyXtaLensConverter(Lens<LazyState<XtaState<Prod2State<DConcr, CConcr>>, XtaState<Prod2State<DAbstr, CAbstr>>>, Prod2State<DConcr, CConcr>> lens) {
        this.lens = lens;
    }

    public static <DConcr extends State, CConcr extends State, DAbstr extends State, CAbstr extends State> LazyXtaLensConverter<DConcr, CConcr, DAbstr, CAbstr>
    of(Lens<LazyState<XtaState<Prod2State<DConcr, CConcr>>, XtaState<Prod2State<DAbstr, CAbstr>>>, Prod2State<DConcr, CConcr>> lens) {
        return new LazyXtaLensConverter<>(lens);
    }

    private LazyState<XtaState<Prod2State<DConcr, CConcr>>, XtaState<Prod2State<DAbstr, CAbstr>>> extendedToNormal(LazyState<XtaAndAnIntState<Prod2State<DConcr, CConcr>>, XtaAndAnIntState<Prod2State<DAbstr, CAbstr>>> lazyState) {
        var concrState = lazyState.getConcrState().getState();
        var abstrState = lazyState.getAbstrState().getState();
        return LazyState.of(concrState, abstrState);
    }

    private LazyState<XtaAndAnIntState<Prod2State<DConcr, CConcr>>, XtaAndAnIntState<Prod2State<DAbstr, CAbstr>>> normalToExtended(
            LazyState<XtaAndAnIntState<Prod2State<DConcr, CConcr>>, XtaAndAnIntState<Prod2State<DAbstr, CAbstr>>> oldState,
            LazyState<XtaState<Prod2State<DConcr, CConcr>>, XtaState<Prod2State<DAbstr, CAbstr>>> newState
    ) {
        var concrState = XtaAndAnIntState.of(newState.getConcrState(), oldState.getConcrState().getR());
        var abstrState = XtaAndAnIntState.of(newState.getAbstrState(), oldState.getAbstrState().getR());

        return LazyState.of(concrState, abstrState);
    }

    @Override
    public Prod2State<DConcr, CConcr> get(LazyState<XtaAndAnIntState<Prod2State<DConcr, CConcr>>, XtaAndAnIntState<Prod2State<DAbstr, CAbstr>>> lazyState) {
        return lens.get(extendedToNormal(lazyState));
    }

    @Override
    public LazyState<XtaAndAnIntState<Prod2State<DConcr, CConcr>>, XtaAndAnIntState<Prod2State<DAbstr, CAbstr>>> set(LazyState<XtaAndAnIntState<Prod2State<DConcr, CConcr>>, XtaAndAnIntState<Prod2State<DAbstr, CAbstr>>> lazyState, Prod2State<DConcr, CConcr> dConcrCConcrProd2State) {
        var result = lens.set(extendedToNormal(lazyState), dConcrCConcrProd2State);
        return normalToExtended(lazyState, result);
    }
}
