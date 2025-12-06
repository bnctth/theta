package hu.bme.mit.theta.xta.local_analysis.localzone;

import hu.bme.mit.theta.analysis.InvTransFunc;
import hu.bme.mit.theta.analysis.zone.ZonePrec;
import hu.bme.mit.theta.analysis.zone.ZoneState;
import hu.bme.mit.theta.core.clock.constr.ClockConstr;
import hu.bme.mit.theta.core.clock.constr.ClockConstrs;
import hu.bme.mit.theta.core.decl.VarDecl;
import hu.bme.mit.theta.core.type.rattype.RatType;
import hu.bme.mit.theta.xta.analysis.XtaAction;
import hu.bme.mit.theta.xta.analysis.zone.XtaZoneInvTransFunc;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class XtaSynchronizedGlobalInvTransFunc implements InvTransFunc<ZoneState, XtaAction, ZonePrec> {
    private final InvTransFunc<ZoneState, XtaAction, ZonePrec> invTransFunc = XtaZoneInvTransFunc.getInstance();
    private final ClockConstr virtualGuard;

    public XtaSynchronizedGlobalInvTransFunc(Collection<VarDecl<RatType>> refClocks) {
        // necessary because Collection don't have random access
        VarDecl<RatType> prevClock = null;
        List<ClockConstr> virtualGuards = new ArrayList<>();
        for (var currClock : refClocks) {
            if (prevClock != null) {
                virtualGuards.add(ClockConstrs.Eq(prevClock, currClock, 0));
            }
            prevClock = currClock;
        }
        virtualGuard = ClockConstrs.And(virtualGuards);
    }

    @Override
    public Collection<? extends ZoneState> getPreStates(ZoneState state, XtaAction action, ZonePrec prec) {
        var preStates = invTransFunc.getPreStates(state, action, prec);
        return preStates.stream().map(preState -> preState.transform().and(virtualGuard).build()).toList();
    }
}
