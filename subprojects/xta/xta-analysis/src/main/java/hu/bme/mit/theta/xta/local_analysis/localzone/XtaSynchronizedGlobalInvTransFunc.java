package hu.bme.mit.theta.xta.local_analysis.localzone;

import hu.bme.mit.theta.analysis.InvTransFunc;
import hu.bme.mit.theta.analysis.zone.ZonePrec;
import hu.bme.mit.theta.analysis.zone.ZoneState;
import hu.bme.mit.theta.core.decl.VarDecl;
import hu.bme.mit.theta.core.type.rattype.RatType;
import hu.bme.mit.theta.xta.XtaProcess;
import hu.bme.mit.theta.xta.analysis.XtaAction;
import hu.bme.mit.theta.xta.analysis.zone.XtaZoneInvTransFunc;

import java.util.Collection;
import java.util.Map;

public class XtaSynchronizedGlobalInvTransFunc implements InvTransFunc<ZoneState, XtaAction, ZonePrec> {
    private final InvTransFunc<ZoneState, XtaAction, ZonePrec> invTransFunc = XtaZoneInvTransFunc.getInstance();
    private final Map<XtaProcess, VarDecl<RatType>> refClocks;

    public XtaSynchronizedGlobalInvTransFunc(Map<XtaProcess, VarDecl<RatType>> virtualGuard) {
        this.refClocks = virtualGuard;
    }

    @Override
    public Collection<? extends ZoneState> getPreStates(ZoneState state, XtaAction action, ZonePrec prec) {
        var preStates = invTransFunc.getPreStates(state, action, prec);

        var involvedRefClocks = XtaLocalZoneUtils.involvedProcesses(action).stream().map(refClocks::get).toList();
        return preStates.stream().map(preState -> preState.sync(involvedRefClocks)).toList();
    }
}
