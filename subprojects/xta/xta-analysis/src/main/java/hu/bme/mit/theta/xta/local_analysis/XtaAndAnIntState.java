package hu.bme.mit.theta.xta.local_analysis;

import hu.bme.mit.theta.analysis.State;
import hu.bme.mit.theta.analysis.expr.ExprState;
import hu.bme.mit.theta.core.type.Expr;
import hu.bme.mit.theta.core.type.booltype.BoolType;
import hu.bme.mit.theta.xta.analysis.XtaState;

import java.util.Objects;

public final class XtaAndAnIntState<S extends State> implements ExprState {
    private static final int HASH_SEED = 8291;
    private volatile int hashCode = 0;

    private final int r;
    private final XtaState<S> state;

    public XtaAndAnIntState(XtaState<S> state, int r) {
        this.state = state;
        this.r = r;
    }

    public int getR() {
        return r;
    }

    public XtaState<S> getState() {
        return state;
    }

    public static <S extends State> XtaAndAnIntState<S> of(final XtaState<S> state, int r) {
        return new XtaAndAnIntState<>(state, r);
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

    @Override
    public String toString() {
        return "XtaAndAnIntState{" +
                "state=" + state +
                ", r=" + r +
                '}';
    }
}
