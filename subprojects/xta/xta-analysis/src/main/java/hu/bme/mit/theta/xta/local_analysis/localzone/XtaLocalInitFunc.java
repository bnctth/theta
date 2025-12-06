package hu.bme.mit.theta.xta.local_analysis.localzone;

import hu.bme.mit.theta.analysis.InitFunc;

import java.util.Collection;
import java.util.Collections;

import static com.google.common.base.Preconditions.checkNotNull;

public final class XtaLocalInitFunc implements InitFunc<LocalZoneState, LocalZonePrec> {

    private static final XtaLocalInitFunc INSTANCE = new XtaLocalInitFunc();

    private XtaLocalInitFunc() {
    }

    public static XtaLocalInitFunc getInstance() {
        return INSTANCE;
    }

    @Override
    public Collection<? extends LocalZoneState> getInitStates(LocalZonePrec prec) {
        checkNotNull(prec);
        return Collections.singleton(LocalZoneState.zero(prec.getMapping(), false).transform().up().build());
    }
}
