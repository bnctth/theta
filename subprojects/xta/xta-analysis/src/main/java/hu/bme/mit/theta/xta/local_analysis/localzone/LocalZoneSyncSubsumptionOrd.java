package hu.bme.mit.theta.xta.local_analysis.localzone;

import hu.bme.mit.theta.analysis.PartialOrd;

public final class LocalZoneSyncSubsumptionOrd implements PartialOrd<LocalZoneState> {
    private static final LocalZoneSyncSubsumptionOrd INSTANCE = new LocalZoneSyncSubsumptionOrd();

    private LocalZoneSyncSubsumptionOrd() {
    }

    public static LocalZoneSyncSubsumptionOrd getInstance() {
        return INSTANCE;
    }

    @Override
    public boolean isLeq(final LocalZoneState lhs, final LocalZoneState rhs) {
        return XtaLocalZoneUtils.globalSync(lhs).isLeq(XtaLocalZoneUtils.globalSync(rhs));
    }
}
