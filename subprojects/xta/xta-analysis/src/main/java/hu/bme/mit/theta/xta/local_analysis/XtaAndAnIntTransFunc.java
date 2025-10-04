package hu.bme.mit.theta.xta.local_analysis;

import hu.bme.mit.theta.analysis.Prec;
import hu.bme.mit.theta.analysis.State;
import hu.bme.mit.theta.analysis.TransFunc;
import hu.bme.mit.theta.xta.analysis.XtaTransFunc;

import java.util.Collection;

import static com.google.common.base.Preconditions.checkNotNull;

public final class XtaAndAnIntTransFunc<S extends State, P extends Prec> implements TransFunc<XtaAndAnIntState<S>, XtaAndAnIntAction, P> {

    private final XtaTransFunc<S, P> transFunc;

    private XtaAndAnIntTransFunc(final XtaTransFunc<S, P> transFunc) {
        this.transFunc = checkNotNull(transFunc);
    }

    public static <S extends State, P extends Prec> XtaAndAnIntTransFunc<S, P> create(
            final XtaTransFunc<S, P> transferFunc) {
        return new XtaAndAnIntTransFunc<>(transferFunc);
    }

    @Override
    public Collection<XtaAndAnIntState<S>> getSuccStates(final XtaAndAnIntState<S> state, final XtaAndAnIntAction action, final P prec) {
        return transFunc.getSuccStates(state.getState(), action.getAction(), prec)
                .stream()
                .map(xtaState -> new XtaAndAnIntState<>(xtaState, action.rPrime()))
                .toList();
    }

}
