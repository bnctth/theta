package hu.bme.mit.theta.xta.local_analysis.localzone;

import hu.bme.mit.theta.analysis.zone.DBM;
import hu.bme.mit.theta.xta.XtaProcess;

import java.util.Map;

public class LocalZoneStateTop extends LocalZoneState {

    public LocalZoneStateTop(Map<XtaProcess, DBM> processTopDbmMap) {
        super(processTopDbmMap);
    }

    @Override
    public boolean isTop() {
        return true;
    }

    @Override
    public boolean isBottom() {
        return false;
    }

}
