package hu.bme.mit.theta.xta.local_analysis;

import com.google.common.collect.ImmutableList;
import hu.bme.mit.theta.analysis.InvTransFunc;
import hu.bme.mit.theta.xta.analysis.XtaAction;
import hu.bme.mit.theta.xta.local_analysis.localzone.LocalZonePrec;
import hu.bme.mit.theta.xta.local_analysis.localzone.LocalZoneState;

import java.util.Collection;

public final class XtaLocalZoneInvTransFunc implements InvTransFunc<LocalZoneState, XtaAction, LocalZonePrec> {

    private final static XtaLocalZoneInvTransFunc INSTANCE = new XtaLocalZoneInvTransFunc();

    private XtaLocalZoneInvTransFunc() { }

    public static XtaLocalZoneInvTransFunc getInstance() { return INSTANCE; }

    @Override
    public Collection<? extends LocalZoneState> getPreStates(LocalZoneState state, XtaAction action, LocalZonePrec prec){
        final LocalZoneState preState = XtaLocalZoneUtils.pre(state, action, prec);
        return ImmutableList.of(preState);
    }
}
