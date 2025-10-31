package hu.bme.mit.theta.xta.local_analysis.localzone;

import hu.bme.mit.theta.xta.XtaSystem;
import hu.bme.mit.theta.xta.XtaProcess;
import hu.bme.mit.theta.analysis.zone.DBM;

import java.util.Collections;
import java.util.Optional;

public class LocalZoneStateTop extends LocalZoneState {
    private final static LocalZoneStateTop INSTANCE = new LocalZoneStateTop();

    private LocalZoneStateTop() {
        super();
    }

    @Override
    public boolean isTop() {
        return true;
    }

    @Override
    public boolean isBottom() {
        return false;
    }

    public static LocalZoneStateTop getInstance() { return INSTANCE; }

    @Override
    public Optional<DBM> getDbmForProcess(XtaProcess proc) {
        return Optional.of(DBM.top(Collections.emptySet()));
    }
}
