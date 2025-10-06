package hu.bme.mit.theta.xta.local_analysis;

import hu.bme.mit.theta.analysis.*;
import hu.bme.mit.theta.analysis.algorithm.ARG;
import hu.bme.mit.theta.analysis.algorithm.SearchStrategy;
import hu.bme.mit.theta.analysis.algorithm.cegar.Abstractor;
import hu.bme.mit.theta.analysis.algorithm.cegar.AbstractorResult;
import hu.bme.mit.theta.analysis.algorithm.lazy.LazyAbstractor;
import hu.bme.mit.theta.analysis.algorithm.lazy.LazyAnalysis;
import hu.bme.mit.theta.analysis.algorithm.lazy.LazyState;
import hu.bme.mit.theta.analysis.algorithm.lazy.LazyStrategy;
import hu.bme.mit.theta.analysis.algorithm.lazy.itp.*;
import hu.bme.mit.theta.analysis.expr.ExprState;
import hu.bme.mit.theta.analysis.prod2.Prod2State;
import hu.bme.mit.theta.core.utils.Lens;
import hu.bme.mit.theta.xta.XtaSystem;
import hu.bme.mit.theta.xta.analysis.XtaState;
import hu.bme.mit.theta.xta.analysis.lazy.ClockStrategy2;
import hu.bme.mit.theta.xta.analysis.lazy.LazyXtaLensUtils;
import hu.bme.mit.theta.xta.local_analysis.localzone.*;

import static com.google.common.base.Preconditions.checkArgument;
import static hu.bme.mit.theta.xta.analysis.lazy.LazyXtaLensUtils.createConcrProd2Lens;

public class XtaAndAnIntAbstractor {
    private final Abstractor<LazyState<XtaAndAnIntState<LocalZoneState>, XtaAndAnIntState<LocalZoneState>>, XtaAndAnIntAction, LocalZonePrec> lazyXtaAbstractor;
    private LocalZonePrec prec;
    private ARG<LazyState<XtaAndAnIntState<LocalZoneState>, XtaAndAnIntState<LocalZoneState>>, XtaAndAnIntAction> arg;

    public XtaAndAnIntAbstractor(XtaSystem system, SystemTypeFactory<LocalZoneState, LocalZonePrec> factory, final ClockStrategy2 clockStrategy, final SearchStrategy searchStrategy) {
        var lazyStrategy=createLazyStrategy(system, clockStrategy);

        lazyXtaAbstractor = new LazyAbstractor<>(
                XtaAndAnIntLts.create(system, factory),
                searchStrategy,
                lazyStrategy,
                LazyAnalysis.create(xtaAbstrPartialOrd, xtaConcrInitFunc, xtaConcrTransFunc, xtaInitAbstractor),
                s -> ((XtaAndAnIntState<?>) s).getState().isError(),
                createConcrProd2Lens() //TODO
        );
    }

    private LazyStrategy<LocalZoneState, LocalZoneState, LazyState<XtaAndAnIntState<LocalZoneState>, XtaAndAnIntState<LocalZoneState>>, XtaAndAnIntAction> createLazyStrategy(XtaSystem system, ClockStrategy2 clockStrategy) {
        checkArgument(clockStrategy.getZoneRepresentation() == ClockStrategy2.ZoneRepresentation.LocalSyncSub);
        final PartialOrd<LocalZoneState> partialOrd = LocalZoneSyncSubsumptionOrd.getInstance();

        final Lens<LazyState<XtaAndAnIntState<LocalZoneState>, XtaAndAnIntState<LocalZoneState>>, LazyState<LocalZoneState, LocalZoneState>>
                lens = LazyXtaLensUtils.createLazyClockLens();
        final Lattice<LocalZoneState> lattice = new LocalZoneLattice(partialOrd);
        final Interpolator<LocalZoneState, LocalZoneState> interpolator = LocalZoneInterpolator.getInstance();
        final Concretizer<LocalZoneState, LocalZoneState> concretizer = BasicConcretizer.create(partialOrd);
        final InvTransFunc<LocalZoneState, XtaAndAnIntAction, LocalZonePrec> zoneInvTransFunc = ActionConvertedInvTransFunc.of(XtaLocalZoneInvTransFunc.getInstance());
        prec = LocalZonePrec.of(system.getProcessClockMap());

        return switch (clockStrategy.getClockStrategy()) {
            case BWITP -> new BwItpStrategy<>(lens, lattice, interpolator, concretizer, zoneInvTransFunc, prec);
            case FWITP -> {
                final TransFunc<LocalZoneState, XtaAndAnIntAction, LocalZonePrec> zoneTransFunc = ActionConvertedTransFunc.of(XtaLocalTransFunc.getInstance());
                yield new FwItpStrategy<>(lens, lattice, interpolator, concretizer, zoneInvTransFunc, prec, zoneTransFunc, prec);
            }
            default -> throw new AssertionError();
        };
    }



    public AbstractorResult check() {
        arg = lazyXtaAbstractor.createArg();
        return lazyXtaAbstractor.check(arg, prec);
    }

    public ARG<LazyState<XtaAndAnIntState<LocalZoneState>, XtaAndAnIntState<LocalZoneState>>, XtaAndAnIntAction> getArg() {
        return arg;
    }
}
