package hu.bme.mit.theta.xta.local_analysis;

import hu.bme.mit.theta.analysis.Prec;
import hu.bme.mit.theta.analysis.State;
import hu.bme.mit.theta.analysis.TransFunc;
import hu.bme.mit.theta.xta.analysis.XtaAction;

import java.util.Collection;

public final class ActionConvertedTransFunc<S extends State, P extends Prec> implements TransFunc<S, XtaAndAnIntAction, P> {
    final TransFunc<S, XtaAction, P> transFunc;

    private ActionConvertedTransFunc(final TransFunc<S, XtaAction, P> transFunc) {
        this.transFunc = transFunc;
    }

    public static <S extends State, P extends Prec> ActionConvertedTransFunc<S, P> of(final TransFunc<S, XtaAction, P> transFunc) {
        return new ActionConvertedTransFunc<>(transFunc);
    }

    @Override
    public Collection<? extends S> getSuccStates(S state, XtaAndAnIntAction action, P prec) {
        return transFunc.getSuccStates(state, action.getAction(), prec);
    }
}
