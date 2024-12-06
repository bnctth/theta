package hu.bme.mit.theta.xta.local_analysis;

import hu.bme.mit.theta.analysis.Analysis;
import hu.bme.mit.theta.analysis.InitFunc;
import hu.bme.mit.theta.analysis.PartialOrd;
import hu.bme.mit.theta.analysis.TransFunc;
import hu.bme.mit.theta.xta.analysis.XtaAction;
import hu.bme.mit.theta.xta.local_analysis.localzone.LocalZonePrec;
import hu.bme.mit.theta.xta.local_analysis.localzone.LocalZoneState;

public final class XtaLocalAnalysis implements Analysis<LocalZoneState, XtaAction, LocalZonePrec> {

    private static final XtaLocalAnalysis INSTANCE = new XtaLocalAnalysis();

    private XtaLocalAnalysis() {
    }

    public static XtaLocalAnalysis getInstance() {
        return INSTANCE;
    }

    @Override
    public PartialOrd<LocalZoneState> getPartialOrd() {
        return LocalZoneOrd.getInstance();
    }

    @Override
    public InitFunc<LocalZoneState, LocalZonePrec> getInitFunc() {
        return XtaLocalInitFunc.getInstance();
    }

    @Override
    public TransFunc<LocalZoneState, XtaAction, LocalZonePrec> getTransFunc() {
        return XtaLocalTransFunc.getInstance();
    }
}
