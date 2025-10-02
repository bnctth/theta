package hu.bme.mit.theta.xta.local_analysis.localzone;

import hu.bme.mit.theta.analysis.PartialOrd;

public final class LocalZoneOrd implements PartialOrd<LocalZoneState> {
    private static final LocalZoneOrd INSTANCE = new LocalZoneOrd();

    private LocalZoneOrd() {}

    public static LocalZoneOrd getInstance() { return INSTANCE; }

    @Override
    public boolean isLeq( final LocalZoneState lhs, final LocalZoneState rhs ) {
        return lhs.isLeq(rhs);
    }
}
