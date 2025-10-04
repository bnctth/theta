package hu.bme.mit.theta.xta.local_analysis;

import hu.bme.mit.theta.xta.analysis.XtaAction;

public interface ActionFactory {
    XtaAndAnIntAction createAction(XtaAction action, int r);
}
