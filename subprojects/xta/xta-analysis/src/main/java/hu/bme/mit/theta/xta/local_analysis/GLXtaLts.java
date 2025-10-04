package hu.bme.mit.theta.xta.local_analysis;

import hu.bme.mit.theta.analysis.LTS;
import hu.bme.mit.theta.xta.XtaSystem;
import hu.bme.mit.theta.xta.analysis.XtaLts;

import java.util.Collection;

import static com.google.common.base.Preconditions.checkNotNull;

public class GLXtaLts implements LTS<XtaAndAnIntState<?>, XtaAndAnIntAction> {
    final XtaLts lts;
    final ActionFactory factory;

    public GLXtaLts(final XtaSystem system, ActionFactory factory) {
        this.lts = XtaLts.create(checkNotNull(system));
        this.factory = checkNotNull(factory);
    }

    @Override
    public Collection<XtaAndAnIntAction> getEnabledActionsFor(XtaAndAnIntState<?> state) {
        return lts.getEnabledActionsFor(state.getState())
                .stream()
                .map(action -> factory.createAction(action, state.getR()))
                .filter(XtaAndAnIntAction::shouldKeep)
                .toList();
    }
}
