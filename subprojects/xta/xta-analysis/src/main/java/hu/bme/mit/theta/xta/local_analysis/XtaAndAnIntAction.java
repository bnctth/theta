package hu.bme.mit.theta.xta.local_analysis;

import hu.bme.mit.theta.analysis.expr.StmtAction;
import hu.bme.mit.theta.core.stmt.Stmt;
import hu.bme.mit.theta.xta.XtaProcess;
import hu.bme.mit.theta.xta.analysis.XtaAction;

import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

public abstract class XtaAndAnIntAction extends StmtAction {
    protected final XtaAction action;
    protected final int r;

    protected XtaAndAnIntAction(XtaAction action, int r) {
        this.action = action;
        this.r = r;
    }

    public XtaAction getAction() {
        return action;
    }

    @Override
    public List<Stmt> getStmts() {
        return action.getStmts();
    }

    protected int getProcId(XtaProcess process) {
        return process.getSystem().getProcesses().indexOf(process);
    }

    protected int getProcIdFromEdge(XtaProcess.Edge edge) {
        return getProcId(edge.getSource().getProc());
    }

    protected List<Integer> involvedProcesses() {
        if (action.isBasic()) {
            var basicAction = this.action.asBasic();
            return Collections.singletonList(getProcIdFromEdge(basicAction.getEdge()));
        }
        if (action.isBinary()) {
            var binaryAction = action.asBinary();
            return Stream.of(binaryAction.getEmitEdge(), binaryAction.getRecvEdge())
                    .map(this::getProcIdFromEdge).toList();
        }
        if (action.isBroadcast()) {
            var broadcastAction = action.asBroadcast();
            return Stream.concat(
                    Stream.of(broadcastAction.getEmitEdge()),
                    broadcastAction.getRecvEdges().stream()
            ).map(this::getProcIdFromEdge).toList();
        }
        throw new IllegalArgumentException("Unknown action type");
    }

    public abstract boolean shouldKeep();

    public abstract int rPrime();
}
