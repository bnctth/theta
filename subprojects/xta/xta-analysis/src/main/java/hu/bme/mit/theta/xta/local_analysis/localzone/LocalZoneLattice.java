package hu.bme.mit.theta.xta.local_analysis.localzone;

import hu.bme.mit.theta.analysis.Lattice;
import hu.bme.mit.theta.analysis.PartialOrd;

public final class LocalZoneLattice implements Lattice<LocalZoneState> {

    private final PartialOrd<LocalZoneState> partialOrd;

    public LocalZoneLattice(PartialOrd<LocalZoneState> partialOrd) {
        this.partialOrd = partialOrd;
    }

    @Override
    public LocalZoneState top() {
        return LocalZoneStateTop.getInstance();
    }

    @Override
    public LocalZoneState bottom() {
        return LocalZoneStateBottom.getInstance();
    }

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
