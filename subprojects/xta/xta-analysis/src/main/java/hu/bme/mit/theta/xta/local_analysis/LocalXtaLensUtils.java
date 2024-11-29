package hu.bme.mit.theta.xta.local_analysis;

import hu.bme.mit.theta.analysis.State;
import hu.bme.mit.theta.analysis.algorithm.lazy.LazyState;
import hu.bme.mit.theta.analysis.expr.ExprState;
import hu.bme.mit.theta.analysis.prod2.Prod2State;
import hu.bme.mit.theta.core.utils.Lens;
import hu.bme.mit.theta.xta.analysis.XtaState;

public class LocalXtaLensUtils {

    public static <CConcr extends State, CAbstr extends ExprState> Lens<LazyState<XtaState<Prod2State<?, CConcr>>, XtaState<Prod2State<?, CAbstr>>>, LazyState<CConcr, CAbstr>>
    createLazyClockLens() {
        final Lens<LazyState<XtaState<Prod2State<?, CConcr>>, XtaState<Prod2State<?, CAbstr>>>, CConcr> concrLens = createConcrClockLens();
        final Lens<LazyState<XtaState<Prod2State<?, CConcr>>, XtaState<Prod2State<?, CAbstr>>>, CAbstr> abstrLens = createAbstrClockLens();
        return new Lens<>() {
            @Override
            public LazyState<CConcr, CAbstr> get(LazyState<XtaState<Prod2State<?, CConcr>>, XtaState<Prod2State<?, CAbstr>>> state) {
                final CConcr concrState = concrLens.get(state);
                if (concrState.isBottom()) {
                    return LazyState.bottom(concrState);
                } else {
                    final CAbstr abstrState = abstrLens.get(state);
                    return LazyState.of(concrState, abstrState);
                }
            }
            @Override
            public LazyState<XtaState<Prod2State<?, CConcr>>, XtaState<Prod2State<?, CAbstr>>> set(
                    LazyState<XtaState<Prod2State<?, CConcr>>, XtaState<Prod2State<?, CAbstr>>> lazyState,
                    LazyState<CConcr, CAbstr> newItpDataState) {
                final XtaState<Prod2State<?, CConcr>> newConcrState = concrLens.set(lazyState, newItpDataState.getConcrState()).getConcrState();
                final XtaState<Prod2State<?, CAbstr>> newAbstrState = abstrLens.set(lazyState, newItpDataState.getAbstrState()).getAbstrState();
                return LazyState.of(newConcrState, newAbstrState);
            }
        };
    }

    public static <CConcr extends State, CAbstr extends State> Lens<LazyState<XtaState<Prod2State<?, CConcr>>, XtaState<Prod2State<?, CAbstr>>>, CConcr>
    createConcrClockLens() {
        return new Lens<>() {
            @Override
            public CConcr get(LazyState<XtaState<Prod2State<?, CConcr>>, XtaState<Prod2State<?, CAbstr>>> state) {
                return state.getConcrState().getState().getState2();
            }
            @Override
            public LazyState<XtaState<Prod2State<?, CConcr>>, XtaState<Prod2State<?, CAbstr>>> set(
                    LazyState<XtaState<Prod2State<?, CConcr>>, XtaState<Prod2State<?, CAbstr>>> lazyState,
                    CConcr newConcrClockState) {
                final XtaState<Prod2State<?, CConcr>> concrXtaState = lazyState.getConcrState();
                final Prod2State<?, CConcr> concrState = concrXtaState.getState();
                final Prod2State<?, CConcr> newConcrState = concrState.with2(newConcrClockState);
                final XtaState<Prod2State<?, CConcr>> newConcrXtaState = concrXtaState.withState(newConcrState);
                return lazyState.withConcrState(newConcrXtaState);
            }
        };
    }

    public static <CConcr extends State, CAbstr extends State> Lens<LazyState<XtaState<Prod2State<?, CConcr>>, XtaState<Prod2State<?, CAbstr>>>, CAbstr>
    createAbstrClockLens() {
        return new Lens<>() {
            @Override
            public CAbstr get(LazyState<XtaState<Prod2State<?, CConcr>>, XtaState<Prod2State<?, CAbstr>>> state) {
                return state.getAbstrState().getState().getState2();
            }
            @Override
            public LazyState<XtaState<Prod2State<?, CConcr>>, XtaState<Prod2State<?, CAbstr>>> set(
                    LazyState<XtaState<Prod2State<?, CConcr>>, XtaState<Prod2State<?, CAbstr>>> lazyState,
                    CAbstr newAbstrClockState) {
                final XtaState<Prod2State<?, CAbstr>> abstrXtaState = lazyState.getAbstrState();
                final Prod2State<?, CAbstr> abstrState = abstrXtaState.getState();
                final Prod2State<?, CAbstr> newAbstrState = abstrState.with2(newAbstrClockState);
                final XtaState<Prod2State<?, CAbstr>> newAbstrXtaState = abstrXtaState.withState(newAbstrState);
                return lazyState.withAbstrState(newAbstrXtaState);
            }
        };
    }
}
