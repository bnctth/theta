package hu.bme.mit.theta.xta.local_analysis.localzone;

import hu.bme.mit.theta.analysis.algorithm.lazy.itp.Interpolator;
import hu.bme.mit.theta.analysis.zone.ZoneState;
import hu.bme.mit.theta.core.decl.VarDecl;
import hu.bme.mit.theta.core.type.rattype.RatType;

import java.util.Collection;
import java.util.List;

public final class LocalZoneSyncSubInterpolator implements Interpolator<LocalZoneState, ZoneState> {

    private final Interpolator<LocalZoneState, ZoneState> interpolator = LocalZoneInterpolator.getInstance();
    private final List<VarDecl<RatType>> refClocks;

    public LocalZoneSyncSubInterpolator(List<VarDecl<RatType>> refClocks) {
        this.refClocks = refClocks;
    }


    @Override
    public ZoneState toItpDom(final LocalZoneState state) {
        return interpolator.toItpDom(state);
    }

    @Override
    public LocalZoneState interpolate(final LocalZoneState lhs, final ZoneState rhs) {
        return interpolator.interpolate(lhs, rhs.sync(refClocks));
    }

    @Override
    public Collection<ZoneState> complement(final ZoneState zone) {
        return interpolator.complement(zone);
    }

    @Override
    public boolean refutes(final LocalZoneState lhs, final ZoneState rhs) {
        var syncedLhs = lhs.jointGlobalZone().sync(refClocks);
        var syncedRhs = rhs.sync(refClocks);

        return syncedLhs.isConsistentWith(syncedRhs);
    }
}
