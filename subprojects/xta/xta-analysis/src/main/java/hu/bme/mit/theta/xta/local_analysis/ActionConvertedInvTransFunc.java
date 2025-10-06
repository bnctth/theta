package hu.bme.mit.theta.xta.local_analysis;

import hu.bme.mit.theta.analysis.InvTransFunc;
import hu.bme.mit.theta.analysis.Prec;
import hu.bme.mit.theta.analysis.State;
import hu.bme.mit.theta.xta.analysis.XtaAction;

import java.util.Collection;

public final class ActionConvertedInvTransFunc<S extends State, P extends Prec> implements InvTransFunc<S, XtaAndAnIntAction, P> {
    final InvTransFunc<S, XtaAction, P> invTransFunc;

    private ActionConvertedInvTransFunc(final InvTransFunc<S, XtaAction, P> invTransFunc) {
        this.invTransFunc = invTransFunc;
    }

    public static <S extends State, P extends Prec> ActionConvertedInvTransFunc<S, P> of(final InvTransFunc<S, XtaAction, P> invTransFunc) {
        return new ActionConvertedInvTransFunc<>(invTransFunc);
    }

    @Override
    public Collection<? extends S> getPreStates(S state, XtaAndAnIntAction action, P prec) {
        return invTransFunc.getPreStates(state, action.getAction(), prec);
    }
}
