package hu.bme.mit.theta.xta.local_analysis;

import hu.bme.mit.theta.analysis.algorithm.lazy.itp.Interpolator;
import hu.bme.mit.theta.xta.local_analysis.localzone.LocalZoneState;

import java.util.Collection;

public final class LocalZoneInterpolator implements Interpolator<LocalZoneState, LocalZoneState> {

    private static final LocalZoneInterpolator INSTANCE = new LocalZoneInterpolator();

    private LocalZoneInterpolator() { }

    public static LocalZoneInterpolator getInstance() { return INSTANCE; }

    @Override
    public LocalZoneState toItpDom(final LocalZoneState zone) { return zone; }

    @Override
    public LocalZoneState interpolate(final LocalZoneState lhs, final LocalZoneState rhs) {
        return LocalZoneState.interpolant(lhs, rhs);
    }

    @Override
    public Collection<LocalZoneState> complement(final LocalZoneState zone) {
        return zone.complement();
    }

    @Override
    public boolean refutes(final LocalZoneState lhs, final LocalZoneState rhs) {
        return !lhs.isConsistentWith(rhs);
    }
}
