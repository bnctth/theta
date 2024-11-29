package hu.bme.mit.theta.xta.local_analysis.localzone;

import hu.bme.mit.theta.xta.XtaSystem;
import hu.bme.mit.theta.xta.XtaProcess;
import hu.bme.mit.theta.analysis.zone.DBM;

import java.util.Collections;
import java.util.Optional;

public class LocalZoneStateBottom extends LocalZoneState {

    private final static LocalZoneStateBottom INSTANCE = new LocalZoneStateBottom();

    private LocalZoneStateBottom() {
        super();
    }

    public static LocalZoneStateBottom getInstance() { return INSTANCE; }

    @Override
    public Optional<DBM> getDbmForProcess(XtaProcess proc) {
        return Optional.of(DBM.bottom(Collections.emptySet()));
    }
}
