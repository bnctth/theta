package hu.bme.mit.theta.xta.local_analysis;

import hu.bme.mit.theta.analysis.Lattice;
import hu.bme.mit.theta.analysis.PartialOrd;
import hu.bme.mit.theta.xta.local_analysis.localzone.LocalZoneState;
import hu.bme.mit.theta.xta.local_analysis.localzone.LocalZoneStateBottom;
import hu.bme.mit.theta.xta.local_analysis.localzone.LocalZoneStateTop;

public final class LocalZoneLattice implements Lattice<LocalZoneState> {

    private final PartialOrd<LocalZoneState> partialOrd;

    private static final LocalZoneLattice INSTANCE = new LocalZoneLattice();

    private LocalZoneLattice() { partialOrd = LocalZoneOrd.getInstance(); }

    public static LocalZoneLattice getInstance() { return INSTANCE; }

    @Override
    public LocalZoneState top() { return LocalZoneStateTop.getInstance(); }

    @Override
    public LocalZoneState bottom() { return LocalZoneStateBottom.getInstance(); }

    @Override
    public LocalZoneState meet(final LocalZoneState lhs, final LocalZoneState rhs) {
        return LocalZoneState.intersection(lhs, rhs);
    }

    @Override
    public LocalZoneState join(final LocalZoneState lhs, final LocalZoneState rhs) {
        return LocalZoneState.enclosure(lhs, rhs);
    }

    @Override
    public boolean isLeq(final LocalZoneState lhs, final LocalZoneState rhs) {
        return partialOrd.isLeq(lhs, rhs);
    }
}
