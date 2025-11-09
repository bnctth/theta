package hu.bme.mit.theta.xta.local_analysis.localzone;

import hu.bme.mit.theta.analysis.algorithm.lazy.itp.Interpolator;

import java.util.Collection;

import static hu.bme.mit.theta.xta.local_analysis.localzone.XtaLocalZoneUtils.globalSync;

public final class LocalZoneSyncSubsumptionInterpolator implements Interpolator<LocalZoneState, LocalZoneState> {

    private static final LocalZoneSyncSubsumptionInterpolator INSTANCE = new LocalZoneSyncSubsumptionInterpolator();

    private LocalZoneSyncSubsumptionInterpolator() {
    }

    public static LocalZoneSyncSubsumptionInterpolator getInstance() {
        return INSTANCE;
    }

    @Override
    public LocalZoneState toItpDom(final LocalZoneState zone) {
        return zone;
    }

    @Override
    public LocalZoneState interpolate(final LocalZoneState lhs, final LocalZoneState rhs) {

        return LocalZoneState.interpolant(
                XtaLocalZoneUtils.syncedState(lhs),
                XtaLocalZoneUtils.syncedState(rhs)
        );
    }

    @Override
    public Collection<LocalZoneState> complement(final LocalZoneState zone) {
        return zone.complement();
    }

    @Override
    public boolean refutes(final LocalZoneState lhs, final LocalZoneState rhs) {
        return !globalSync(lhs).isConsistentWith(globalSync(rhs));
    }
}
