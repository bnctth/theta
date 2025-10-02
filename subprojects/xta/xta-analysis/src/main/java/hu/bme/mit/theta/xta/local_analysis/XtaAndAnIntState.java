package hu.bme.mit.theta.xta.local_analysis;

import hu.bme.mit.theta.analysis.State;
import hu.bme.mit.theta.analysis.expr.ExprState;
import hu.bme.mit.theta.core.model.ImmutableValuation;
import hu.bme.mit.theta.core.model.Valuation;
import hu.bme.mit.theta.core.type.Expr;
import hu.bme.mit.theta.core.type.booltype.BoolType;
import hu.bme.mit.theta.xta.XtaProcess;
import hu.bme.mit.theta.xta.analysis.XtaState;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

public class XtaAndAnIntState<S extends State> implements ExprState {
    private static final int HASH_SEED = 8291;
    private volatile int hashCode = 0;

    private int r;
    private XtaState<S> state;

    public XtaAndAnIntState(XtaState<S> state, int r) {
        this.state = state;
        this.r = r;
    }

    public static <S extends State> XtaAndAnIntState<S> of(final List<XtaProcess.Loc> locs, final S state, int r) {
        return new XtaAndAnIntState<>(XtaState.of(locs, state, ImmutableValuation.empty()), r);
    }

    public static <S extends State> XtaAndAnIntState<S> of(final List<XtaProcess.Loc> locs, final S state, Valuation valuation, int r) {
        return new XtaAndAnIntState<>(XtaState.of(locs, state, valuation), r);
    }

    public static <S extends State> Collection<XtaAndAnIntState<S>> collectionOf(final List<XtaProcess.Loc> locs,
                                                                                 final Collection<? extends S> states, int r) {
        return collectionOf(locs, states, ImmutableValuation.empty(), r);
    }

    public static <S extends State> Collection<XtaAndAnIntState<S>> collectionOf(final List<XtaProcess.Loc> locs,
                                                                                 final Collection<? extends S> states,
                                                                                 final Valuation valuation, int r) {
        final Collection<XtaAndAnIntState<S>> result = new ArrayList<>();
        for (final S state : states) {
            final XtaAndAnIntState<S> initXtaState = new XtaAndAnIntState<>(XtaState.of(locs, state, valuation), r);
            result.add(initXtaState);
        }
        return result;
    }

    @Override
    public Expr<BoolType> toExpr() {
        return state.toExpr();
    }

    @Override
    public boolean isBottom() {
        return state.isBottom();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        XtaAndAnIntState<?> that = (XtaAndAnIntState<?>) o;
        return r == that.r && Objects.equals(state, that.state);
    }

    @Override
    public int hashCode() {
        int result = hashCode;
        if (result == 0) {
            result = HASH_SEED;
            result = 31 * result + state.hashCode();
            hashCode = result;
        }
        return result;    }
}
