package hu.bme.mit.theta.xta.local_analysis.localzone;

import hu.bme.mit.theta.analysis.PartialOrd;
import hu.bme.mit.theta.analysis.algorithm.lazy.itp.Concretizer;

public final class LocalZoneConcretizer implements Concretizer<LocalZoneState, LocalZoneState> {
    private final PartialOrd<LocalZoneState> ord;

    public LocalZoneConcretizer(PartialOrd<LocalZoneState> ord) {
        this.ord = ord;
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
