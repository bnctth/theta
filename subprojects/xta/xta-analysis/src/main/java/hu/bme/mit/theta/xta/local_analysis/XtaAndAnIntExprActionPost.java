package hu.bme.mit.theta.xta.local_analysis;

import hu.bme.mit.theta.analysis.algorithm.lazy.expr.ExprActionPost;
import hu.bme.mit.theta.analysis.expr.BasicExprState;
import hu.bme.mit.theta.xta.analysis.XtaAction;
import hu.bme.mit.theta.xta.analysis.expr.XtaExprActionPost;

public class XtaAndAnIntExprActionPost implements ExprActionPost<XtaAndAnIntAction> {
    private final ExprActionPost<XtaAction> exprActionPost;

    private XtaAndAnIntExprActionPost() {
        exprActionPost = XtaExprActionPost.create();
    }

    public static XtaAndAnIntExprActionPost create() {
        return new XtaAndAnIntExprActionPost();
    }

    @Override
    public BasicExprState post(BasicExprState state, XtaAndAnIntAction action) {
        return exprActionPost.post(state, action.getAction());
    }
}
