package hu.bme.mit.theta.xta.local_analysis;

import com.google.common.collect.ImmutableList;
import hu.bme.mit.theta.analysis.TransFunc;
import hu.bme.mit.theta.xta.analysis.XtaAction;
import hu.bme.mit.theta.xta.local_analysis.localzone.LocalZonePrec;
import hu.bme.mit.theta.xta.local_analysis.localzone.LocalZoneState;

import java.util.Collection;

public final class XtaLocalTransFunc implements TransFunc<LocalZoneState, XtaAction, LocalZonePrec> {

    private final static XtaLocalTransFunc INSTANCE = new XtaLocalTransFunc();

    private XtaLocalTransFunc() {
    }

    public static XtaLocalTransFunc getInstance() {
        return INSTANCE;
    }

    @Override
    public Collection<LocalZoneState> getSuccStates(final LocalZoneState state, final XtaAction action, final LocalZonePrec prec) {
        final LocalZoneState succState = XtaLocalZoneUtils.post(state, action, prec);
        return ImmutableList.of(succState);
    }

}
