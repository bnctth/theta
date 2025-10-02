package hu.bme.mit.theta.xta.local_analysis.localzone;

import hu.bme.mit.theta.analysis.PartialOrd;
import hu.bme.mit.theta.analysis.algorithm.lazy.itp.Concretizer;

public final class LocalZoneConcretizer implements Concretizer<LocalZoneState, LocalZoneState> {
    private final static LocalZoneConcretizer INSTANCE = new LocalZoneConcretizer();

    private final PartialOrd<LocalZoneState> ord;

    private LocalZoneConcretizer() {
        ord = LocalZoneOrd.getInstance();
    }

    @Override
    public LocalZoneState concretize(final LocalZoneState state) {
        return state;
    }

    @Override
    public boolean proves (LocalZoneState lhs, LocalZoneState rhs) {
        return ord.isLeq(lhs, rhs);
    }

    @Override
    public boolean inconsistentConcrState(LocalZoneState state) { return state.isBottom(); }

}
