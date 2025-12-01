package hu.bme.mit.theta.xta.local_analysis;

import hu.bme.mit.theta.analysis.*;
import hu.bme.mit.theta.analysis.algorithm.SearchStrategy;
import hu.bme.mit.theta.analysis.algorithm.cegar.Abstractor;
import hu.bme.mit.theta.analysis.algorithm.lazy.*;
import hu.bme.mit.theta.analysis.algorithm.lazy.expl.ExplAnalysis;
import hu.bme.mit.theta.analysis.algorithm.lazy.expl.ExplTransFunc;
import hu.bme.mit.theta.analysis.algorithm.lazy.expr.ExprInvTransFunc;
import hu.bme.mit.theta.analysis.algorithm.lazy.expr.ExprTransFunc;
import hu.bme.mit.theta.analysis.algorithm.lazy.itp.*;
import hu.bme.mit.theta.analysis.expl.*;
import hu.bme.mit.theta.analysis.expr.*;
import hu.bme.mit.theta.analysis.expr.refinement.ExprTraceChecker;
import hu.bme.mit.theta.analysis.expr.refinement.ExprTraceSeqItpChecker;
import hu.bme.mit.theta.analysis.expr.refinement.ItpRefutation;
import hu.bme.mit.theta.analysis.prod2.Prod2Analysis;
import hu.bme.mit.theta.analysis.prod2.Prod2Prec;
import hu.bme.mit.theta.analysis.prod2.Prod2State;
import hu.bme.mit.theta.analysis.unit.UnitPrec;
import hu.bme.mit.theta.analysis.zone.ZoneState;
import hu.bme.mit.theta.common.Tuple3;
import hu.bme.mit.theta.core.utils.Lens;
import hu.bme.mit.theta.solver.ItpSolver;
import hu.bme.mit.theta.solver.Solver;
import hu.bme.mit.theta.solver.SolverFactory;
import hu.bme.mit.theta.solver.z3.Z3SolverFactory;
import hu.bme.mit.theta.xta.XtaSystem;
import hu.bme.mit.theta.xta.analysis.*;
import hu.bme.mit.theta.xta.analysis.expl.XtaExplUtils;
import hu.bme.mit.theta.xta.analysis.expr.XtaExprAnalysis;
import hu.bme.mit.theta.xta.analysis.lazy.ClockStrategy2;
import hu.bme.mit.theta.xta.analysis.lazy.DataStrategy2;
import hu.bme.mit.theta.xta.analysis.lazy.LazyXtaLensUtils;
import hu.bme.mit.theta.xta.analysis.lazy.LuZoneStrategy2;
import hu.bme.mit.theta.xta.analysis.zone.lu.LuZoneState;
import hu.bme.mit.theta.xta.local_analysis.localzone.*;

import java.util.function.Function;

import static com.google.common.base.Preconditions.checkArgument;
import static hu.bme.mit.theta.core.type.booltype.BoolExprs.True;
import static hu.bme.mit.theta.xta.analysis.lazy.LazyXtaLensUtils.createConcrProd2Lens;

@SuppressWarnings({"unchecked", "rawtypes"})
public final class LazyXtaAndAnIntAbstractorConfigFactory {

    private LazyXtaAndAnIntAbstractorConfigFactory() {
    }

    public static <DConcr extends State, CConcr extends State, DAbstr extends State, CAbstr extends State, DPrec extends Prec, CPrec extends Prec>
    LazyXtaAndAnIntAbstractorConfig<Prod2State<DConcr, CConcr>, Prod2State<DAbstr, CAbstr>, Prod2Prec<DPrec, CPrec>>
    create(final XtaSystem system, final DataStrategy2 dataStrategy, final ClockStrategy2 clockStrategy, final SearchStrategy searchStrategy, final ExprMeetStrategy meetStrategy, final SystemTypeFactory systemTypeFactory) {

        final Factory<DConcr, CConcr, DAbstr, CAbstr, DPrec, CPrec>
                factory = new Factory<>(system, dataStrategy, clockStrategy, searchStrategy, meetStrategy, systemTypeFactory);
        return factory.create();
    }

    public static <DConcr extends State, CConcr extends State, DAbstr extends State, CAbstr extends State, DPrec extends Prec, CPrec extends Prec>
    LazyXtaAndAnIntAbstractorConfig<Prod2State<DConcr, CConcr>, Prod2State<DAbstr, CAbstr>, Prod2Prec<DPrec, CPrec>>
    create(final XtaSystem system, final DataStrategy2 dataStrategy, final ClockStrategy2 clockStrategy, final SearchStrategy searchStrategy, final SystemTypeFactory systemTypeFactory) {
        return create(system, dataStrategy, clockStrategy, searchStrategy, ExprMeetStrategy.BASIC, systemTypeFactory);
    }

    private static class Factory<DConcr extends State, CConcr extends State, DAbstr extends State, CAbstr extends State, DPrec extends Prec, CPrec extends Prec> {

        private final XtaSystem system;
        private final DataStrategy2 dataStrategy;
        private final ClockStrategy2 clockStrategy;
        private final SearchStrategy searchStrategy;
        private final ExprMeetStrategy meetStrategy;
        private final SolverFactory solverFactory;
        private final SystemTypeFactory systemTypeFactory;

        public Factory(final XtaSystem system, final DataStrategy2 dataStrategy, final ClockStrategy2 clockStrategy,
                       final SearchStrategy searchStrategy, final ExprMeetStrategy meetStrategy,
                       final SystemTypeFactory systemTypeFactory) {
            this.system = system;
            this.dataStrategy = dataStrategy;
            this.clockStrategy = clockStrategy;
            this.searchStrategy = searchStrategy;
            this.meetStrategy = meetStrategy;
            this.systemTypeFactory = systemTypeFactory;
            solverFactory = Z3SolverFactory.getInstance();
        }

        public final LazyXtaAndAnIntAbstractorConfig<Prod2State<DConcr, CConcr>, Prod2State<DAbstr, CAbstr>, Prod2Prec<DPrec, CPrec>> create() {
            final LazyStrategy<Prod2State<DConcr, CConcr>, Prod2State<DAbstr, CAbstr>,
                    LazyState<XtaAndAnIntState<Prod2State<DConcr, CConcr>>, XtaAndAnIntState<Prod2State<DAbstr, CAbstr>>>, XtaAndAnIntAction>
                    lazyStrategy = createLazyStrategy(system, dataStrategy, clockStrategy);

            final PartialOrd<Prod2State<DAbstr, CAbstr>> abstrPartialOrd = lazyStrategy.getPartialOrd();
            final InitAbstractor<Prod2State<DConcr, CConcr>, Prod2State<DAbstr, CAbstr>> initAbstractor = lazyStrategy.getInitAbstractor();
            final LazyAnalysis<XtaAndAnIntState<Prod2State<DConcr, CConcr>>, XtaAndAnIntState<Prod2State<DAbstr, CAbstr>>, XtaAndAnIntAction, Prod2Prec<DPrec, CPrec>>
                    lazyAnalysis = createLazyAnalysis(abstrPartialOrd, initAbstractor);

            final Prod2Prec<DPrec, CPrec> prec = createConcrPrec();
            final Abstractor<LazyState<XtaAndAnIntState<Prod2State<DConcr, CConcr>>, XtaAndAnIntState<Prod2State<DAbstr, CAbstr>>>, XtaAndAnIntAction, Prod2Prec<DPrec, CPrec>>
                    abstractor = new LazyAbstractor(
                    XtaAndAnIntLts.create(system, systemTypeFactory),
                    searchStrategy,
                    lazyStrategy,
                    lazyAnalysis,
                    s -> ((XtaAndAnIntState<Prod2State<DConcr, CConcr>>) s).getState().isError(),
                    LazyXtaLensConverter.of(createConcrProd2Lens())
            );
            return new LazyXtaAndAnIntAbstractorConfig<>(abstractor, prec);
        }

        private Prod2Prec<DPrec, CPrec> createConcrPrec() {
            final Prec dataPrec = createConcrDataPrec();
            final Prec clockPrec = createConcrZonePrec();
            return (Prod2Prec<DPrec, CPrec>) Prod2Prec.of(dataPrec, clockPrec);
        }

        private Prec createConcrDataPrec() {
            return switch (dataStrategy.getConcrDom()) {
                case EXPL, EXPR -> UnitPrec.getInstance();
                default -> throw new AssertionError();
            };
        }

        private Prec createConcrZonePrec() {
            checkArgument(clockStrategy.getZoneRepresentation() == ClockStrategy2.ZoneRepresentation.Local ||
                    clockStrategy.getZoneRepresentation() == ClockStrategy2.ZoneRepresentation.LocalSyncSub);
            return switch (clockStrategy.getClockStrategy()) {
                case BWITP, FWITP, LU -> LocalZonePrec.of(system.getProcessClockMap());
                default -> throw new AssertionError();
            };
        }

        private LazyAnalysis<XtaAndAnIntState<Prod2State<DConcr, CConcr>>, XtaAndAnIntState<Prod2State<DAbstr, CAbstr>>, XtaAndAnIntAction, Prod2Prec<DPrec, CPrec>>
        createLazyAnalysis(final PartialOrd<Prod2State<DAbstr, CAbstr>> abstrPartialOrd,
                           final InitAbstractor<Prod2State<DConcr, CConcr>, Prod2State<DAbstr, CAbstr>> initAbstractor) {

            final XtaAndAnIntOrd<Prod2State<DAbstr, CAbstr>> xtaAbstrPartialOrd = systemTypeFactory.createOrd(XtaOrd.create(abstrPartialOrd));

            final Prod2Analysis<DConcr, CConcr, XtaAction, DPrec, CPrec> prod2ConcrAnalysis = createConcrAnalysis();
            final XtaAndAnIntAnalysis<Prod2State<DConcr, CConcr>, Prod2Prec<DPrec, CPrec>> xtaConcrAnalysis
                    = XtaAndAnIntAnalysis.create(XtaAnalysis.create(system, prod2ConcrAnalysis), systemTypeFactory);
            final InitFunc<XtaAndAnIntState<Prod2State<DConcr, CConcr>>, Prod2Prec<DPrec, CPrec>> xtaConcrInitFunc
                    = xtaConcrAnalysis.getInitFunc();
            final TransFunc<XtaAndAnIntState<Prod2State<DConcr, CConcr>>, XtaAndAnIntAction, Prod2Prec<DPrec, CPrec>> xtaConcrTransFunc
                    = xtaConcrAnalysis.getTransFunc();

            final XtaAndAnIntInitAbstractor<Prod2State<DConcr, CConcr>, Prod2State<DAbstr, CAbstr>> xtaInitAbstractor
                    = XtaAndAnIntInitAbstractor.create(XtaInitAbstractor.create(initAbstractor));

            return LazyAnalysis.create(xtaAbstrPartialOrd, xtaConcrInitFunc, xtaConcrTransFunc, xtaInitAbstractor);
        }

        private Prod2Analysis<DConcr, CConcr, XtaAction, DPrec, CPrec> createConcrAnalysis() {
            final Analysis<DConcr, XtaAction, DPrec> dataAnalysis = createConcrDataAnalysis();
            final Analysis<CConcr, XtaAction, CPrec> clockAnalysis = createConcrClockAnalysis();
            return Prod2Analysis.create(dataAnalysis, clockAnalysis);
        }

        private Analysis createConcrDataAnalysis() {
            return switch (dataStrategy.getConcrDom()) {
                case EXPL -> ExplAnalysis.create(system.getInitVal(), XtaExplUtils::post);
                case EXPR -> {
                    final Solver solver = solverFactory.createSolver();
                    yield XtaExprAnalysis.create(system, solver);
                }
                default -> throw new AssertionError();
            };
        }

        private Analysis createConcrClockAnalysis() {
            return switch (clockStrategy.getClockStrategy()) {
                case FWITP, BWITP, LU -> switch (clockStrategy.getZoneRepresentation()) {
                    case Local -> new XtaLocalAnalysis(LocalZoneOrd.getInstance());
                    case LocalSyncSub -> new XtaLocalAnalysis(LocalZoneSyncSubsumptionOrd.getInstance());
                    default -> throw new AssertionError("Only sync subsumption may be used for lazy POR");
                };
                default -> throw new AssertionError();
            };
        }

        private LazyStrategy<Prod2State<DConcr, CConcr>, Prod2State<DAbstr, CAbstr>, LazyState<XtaAndAnIntState<Prod2State<DConcr, CConcr>>, XtaAndAnIntState<Prod2State<DAbstr, CAbstr>>>, XtaAndAnIntAction>
        createLazyStrategy(final XtaSystem system, final DataStrategy2 dataStrategy, final ClockStrategy2 clockStrategy) {
            final LazyStrategy<DConcr, DAbstr, LazyState<XtaAndAnIntState<Prod2State<DConcr, CConcr>>, XtaAndAnIntState<Prod2State<DAbstr, CAbstr>>>, XtaAndAnIntAction>
                    dataLazyStrategy = createDataStrategy2(system, dataStrategy);
            final LazyStrategy<CConcr, CAbstr, LazyState<XtaAndAnIntState<Prod2State<DConcr, CConcr>>, XtaAndAnIntState<Prod2State<DAbstr, CAbstr>>>, XtaAndAnIntAction>
                    clockLazyStrategy = createClockStrategy(system, clockStrategy);
            final Function<LazyState<XtaAndAnIntState<Prod2State<DConcr, CConcr>>, XtaAndAnIntState<Prod2State<DAbstr, CAbstr>>>, ?> projection = s -> Tuple3.of(
                    s.getConcrState().getState().getLocs(),
                    dataLazyStrategy.getProjection().apply(s),
                    clockLazyStrategy.getProjection().apply(s)
            );
            final Lens<LazyState<XtaAndAnIntState<Prod2State<DConcr, CConcr>>, XtaAndAnIntState<Prod2State<DAbstr, CAbstr>>>, Prod2State<DConcr, CConcr>>
                    lens = LazyXtaLensConverter.of(createConcrProd2Lens());
            return new Prod2LazyStrategy<>(lens, dataLazyStrategy, clockLazyStrategy, projection);
        }

        private LazyStrategy<DConcr, DAbstr, LazyState<XtaAndAnIntState<Prod2State<DConcr, CConcr>>, XtaAndAnIntState<Prod2State<DAbstr, CAbstr>>>, XtaAndAnIntAction> createDataStrategy2(final XtaSystem system, final DataStrategy2 dataStrategy) {
            final DataStrategy2.ConcrDom concrDom = dataStrategy.getConcrDom();
            final DataStrategy2.AbstrDom abstrDom = dataStrategy.getAbstrDom();
            final DataStrategy2.ItpStrategy itpStrategy = dataStrategy.getItpStrategy();

            final Lens lens = createDataLens(abstrDom);
            final Concretizer concretizer = createDataConcretizer(concrDom, abstrDom);
            if (itpStrategy == DataStrategy2.ItpStrategy.NONE) {
                return new IdentityAbstractionLazyStrategy<>(lens, concretizer);
            }
            final Lattice abstrLattice = createDataLattice(abstrDom);
            if (itpStrategy == DataStrategy2.ItpStrategy.SEQ) {
                final Function<XtaAndAnIntAction, StmtAction> actionTransform = a -> XtaDataAction.of(a.getAction());
                final Solver solver = solverFactory.createSolver();
                final ItpSolver itpSolver = solverFactory.createItpSolver();
                final ExprTraceChecker<ItpRefutation> traceChecker = ExprTraceSeqItpChecker.create(True(), True(), itpSolver);
                return new ExprSeqItpStrategy<>(lens, actionTransform, abstrLattice, concretizer, solver, traceChecker);
            }
            final Interpolator interpolator = createDataInterpolator(abstrDom);
            final InvTransFunc invTransFunc = createDataInvTransFunc();
            final UnitPrec prec = UnitPrec.getInstance();
            if (itpStrategy == DataStrategy2.ItpStrategy.BIN_BW) {
                return new BwItpStrategy<>(lens, abstrLattice, interpolator, concretizer, invTransFunc, prec);
            }
            final TransFunc abstrTransFunc = createDataAbstrTransFunc(abstrDom);
            if (itpStrategy == DataStrategy2.ItpStrategy.BIN_FW) {
                return new FwItpStrategy<>(lens, abstrLattice, interpolator, concretizer, invTransFunc, prec, abstrTransFunc, prec);
            }
            throw new AssertionError();
        }

        private Lens createDataLens(final DataStrategy2.AbstrDom abstrDom) {
            if (abstrDom == DataStrategy2.AbstrDom.NONE) {
                return LazyXtaLensConverter.of(LazyXtaLensUtils.createConcrDataLens());
            }
            return LazyXtaLensConverter.of(LazyXtaLensUtils.createLazyDataLens());
        }

        private Concretizer createDataConcretizer(final DataStrategy2.ConcrDom concrDom, final DataStrategy2.AbstrDom abstrDom) {
            final Solver solver;
            switch (concrDom) {
                case EXPL:
                    switch (abstrDom) {
                        case NONE:
                        case EXPL:
                            final PartialOrd<ExplState> partialOrd = ExplOrd.getInstance();
                            return BasicConcretizer.create(partialOrd);
                        case EXPR:
                            solver = solverFactory.createSolver();
                            return ExplExprConcretizer.create(solver);
                    }
                case EXPR:
                    if (abstrDom == DataStrategy2.AbstrDom.EXPR) {
                        solver = solverFactory.createSolver();
                        return IndexedExprConcretizer.create(solver);
                    }
                    throw new AssertionError();
                default:
                    throw new AssertionError();
            }
        }

        private Lattice createDataLattice(final DataStrategy2.AbstrDom abstrDom) {
            switch (abstrDom) {
                case EXPL:
                    return ExplLattice.getInstance();
                case EXPR:
                    final Solver solver = solverFactory.createSolver();
                    switch (meetStrategy) {
                        case BASIC:
                            return ExprLattice.create(solver, BasicExprMeetStrategy.getInstance());
                        case SYNTACTIC:
                            return ExprLattice.create(solver, SyntacticExprMeetStrategy.getInstance());
                        case SEMANTIC:
                            return ExprLattice.create(solver, SemanticExprMeetStrategy.create(solver));
                    }
                default:
                    throw new AssertionError();
            }
        }

        private Interpolator createDataInterpolator(final DataStrategy2.AbstrDom abstrDom) {
            switch (abstrDom) {
                case EXPL:
                    return ExplExprInterpolator.getInstance();
                case EXPR:
                    final Solver solver = solverFactory.createSolver();
                    final ItpSolver itpSolver = solverFactory.createItpSolver();
                    return ExprInterpolator.create(solver, itpSolver);
                default:
                    throw new AssertionError();
            }
        }

        private InvTransFunc<BasicExprState, XtaAndAnIntAction, UnitPrec> createDataInvTransFunc() {
            return ExprInvTransFunc.create((expr, action) -> XtaExplUtils.pre(expr, action.getAction()));
        }

        private TransFunc<? extends State, XtaAndAnIntAction, ? extends Prec> createDataAbstrTransFunc(final DataStrategy2.AbstrDom abstrDom) {
            switch (abstrDom) {
                case EXPL:
                    return ExplTransFunc.create((val, action) -> XtaExplUtils.post(val, action.getAction()));
                case EXPR:
                    return ExprTransFunc.create(XtaAndAnIntExprActionPost.create());
                default:
                    throw new AssertionError();
            }
        }

        private LazyStrategy createClockStrategy(final XtaSystem system, final ClockStrategy2 clockStrategy) {
            return switch (clockStrategy.getClockStrategy()) {
                case BWITP, FWITP -> switch (clockStrategy.getZoneRepresentation()) {
                    case Local, LocalSyncSub -> createLazyLocalZoneStrategy(system, clockStrategy);
                    default -> throw new AssertionError("Only sync subsumption may be used for this algorithm");
                };
                case LU -> {
                    final Lens<LazyState<XtaState<Prod2State<?, ZoneState>>, XtaState<Prod2State<?, LuZoneState>>>, LuZoneState>
                            lens = LazyXtaLensUtils.createAbstrClockLens();
                    yield new LuZoneStrategy2<>(lens);
                }
                default -> throw new AssertionError();
            };
        }

        private LazyStrategy<LocalZoneState, LocalZoneState, LazyState<XtaAndAnIntState<Prod2State<?, LocalZoneState>>, XtaAndAnIntState<Prod2State<?, LocalZoneState>>>, XtaAndAnIntAction>
        createLazyLocalZoneStrategy(final XtaSystem system, final ClockStrategy2 clockStrategy) {
            throw new UnsupportedOperationException();
        }

//            final PartialOrd<LocalZoneState> partialOrd = switch (clockStrategy.getZoneRepresentation()) {
//                case Global ->
//                        throw new AssertionError("This method must not be called with global zone representation");
//                case Local -> LocalZoneOrd.getInstance();
//                case LocalSyncSub -> LocalZoneSyncSubsumptionOrd.getInstance();
//            };
//
//            final Lens<LazyState<XtaAndAnIntState<Prod2State<?, LocalZoneState>>, XtaAndAnIntState<Prod2State<?, LocalZoneState>>>, LazyState<LocalZoneState, LocalZoneState>>
//                    lens = LazyXtaLensConverter.of(LazyXtaLensUtils.createLazyClockLens());
//            final Lattice<LocalZoneState> lattice = new LocalZoneLattice(partialOrd);
//            final Interpolator<LocalZoneState, ZoneState> interpolator = switch (clockStrategy.getZoneRepresentation()) {
//                case LocalSyncSub -> LocalZoneSyncSubsumptionInterpolator.getInstance();
//                default -> LocalZoneInterpolator.getInstance();
//            };
//            final Concretizer<LocalZoneState, LocalZoneState> concretizer = BasicConcretizer.create(partialOrd);
//            final InvTransFunc<LocalZoneState, XtaAndAnIntAction, LocalZonePrec> zoneInvTransFunc = (LocalZoneState state, XtaAndAnIntAction action, LocalZonePrec prec) -> XtaLocalZoneInvTransFunc.getInstance().getPreStates(state, action.getAction(), prec);
//            final LocalZonePrec prec = LocalZonePrec.of(system.getProcessClockMap());
//
//            switch (clockStrategy.getClockStrategy()) {
//                case BWITP:
//                    return new BwItpStrategy<>(lens, lattice, interpolator, concretizer, zoneInvTransFunc, prec);
//                case FWITP:
//                    final TransFunc<LocalZoneState, XtaAndAnIntAction, LocalZonePrec> zoneTransFunc = (LocalZoneState state, XtaAndAnIntAction action, LocalZonePrec p) -> XtaLocalTransFunc.getInstance().getSuccStates(state, action.getAction(), p);
//                    return new FwItpStrategy<>(lens, lattice, interpolator, concretizer, zoneInvTransFunc, prec, zoneTransFunc, prec);
//                default:
//                    throw new AssertionError();
//            }
//        }
    }
}
