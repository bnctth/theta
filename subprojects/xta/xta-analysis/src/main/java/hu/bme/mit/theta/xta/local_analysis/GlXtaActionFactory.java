package hu.bme.mit.theta.xta.local_analysis;

import hu.bme.mit.theta.xta.analysis.XtaAction;

public class GlXtaActionFactory implements ActionFactory {
    private static final GlXtaActionFactory instance = new GlXtaActionFactory();

    private GlXtaActionFactory() {
    }

    public static GlXtaActionFactory getInstance() {
        return instance;
    }

    @Override
    public XtaAndAnIntAction createAction(XtaAction action, int r) {
        return new GLXtaAction(action, r);
    }
}
