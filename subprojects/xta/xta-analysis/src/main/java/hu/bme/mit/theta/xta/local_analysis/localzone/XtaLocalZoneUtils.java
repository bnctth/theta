package hu.bme.mit.theta.xta.local_analysis.localzone;

import com.google.common.collect.Lists;

import com.google.common.collect.Streams;
import hu.bme.mit.theta.common.Tuple2;
import hu.bme.mit.theta.core.clock.constr.ClockConstrs;
import hu.bme.mit.theta.analysis.zone.DBM;
import hu.bme.mit.theta.analysis.zone.ZoneState;
import hu.bme.mit.theta.core.clock.op.ResetOp;
import hu.bme.mit.theta.core.decl.VarDecl;
import hu.bme.mit.theta.core.type.rattype.RatType;
import hu.bme.mit.theta.xta.Guard;
import hu.bme.mit.theta.xta.Update;
import hu.bme.mit.theta.xta.XtaProcess.Edge;
import hu.bme.mit.theta.xta.XtaProcess.Loc;
import hu.bme.mit.theta.xta.XtaProcess.LocKind;
import hu.bme.mit.theta.xta.analysis.XtaAction;
import hu.bme.mit.theta.xta.analysis.XtaAction.BasicXtaAction;
import hu.bme.mit.theta.xta.analysis.XtaAction.BinaryXtaAction;
import hu.bme.mit.theta.xta.analysis.XtaAction.BroadcastXtaAction;

import java.util.*;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkNotNull;
import static hu.bme.mit.theta.core.clock.constr.ClockConstrs.Eq;

public final class XtaLocalZoneUtils {

    private XtaLocalZoneUtils() {
    }

    public static LocalZoneState post(final LocalZoneState state, final XtaAction action,
                                      final LocalZonePrec prec) {
        checkNotNull(state);
        checkNotNull(action);
        checkNotNull(prec);

        if (action.isBasic()) {
            return postForBasicAction(state, action.asBasic(), prec);
        } else if (action.isBinary()) {
            return postForBinaryAction(state, action.asBinary());
        } else if (action.isBroadcast()) {
            return postForBroadcastAction(state, action.asBroadcast(), prec);
        } else {
            throw new AssertionError();
        }
    }

    private static LocalZoneState postForBasicAction(final LocalZoneState state, final BasicXtaAction action,
                                                     final LocalZonePrec prec) {
        final LocalZoneState.Builder succStateBuilder = state.project(prec.getMapping());

        final List<Loc> sourceLocs = action.getSourceLocs();
        final Edge edge = action.getEdge();
        final List<Loc> targetLocs = action.getTargetLocs();

        applyInvariants(succStateBuilder, sourceLocs);
        applyGuards(succStateBuilder, edge);
        applyUpdates(succStateBuilder, edge);
        applyInvariants(succStateBuilder, targetLocs);

        List<Loc> involvedLocs = action.getTargetLocs();
        if (shouldApplyDelay(involvedLocs)) {
            applyDelay(succStateBuilder, involvedLocs);
        }

        return succStateBuilder.build();
    }

    private static LocalZoneState postForBinaryAction(final LocalZoneState state,
                                                      final BinaryXtaAction action) {
        final List<Loc> sourceLocs = action.getSourceLocs();
        final Edge emittingEdge = action.getEmitEdge();
        final Edge receivingEdge = action.getRecvEdge();
        final List<Loc> targetLocs = action.getTargetLocs();

        List<DBM.ProcessDbmPair> actionDbmList = fixOrderedDbmList(targetLocs, state);
        DBM jointDBM = DBM.joinDbms(actionDbmList);
        applyVirtualGuards(targetLocs, jointDBM, state);


        final ZoneState.Builder succStateBuilder = ZoneState.Builder.project(jointDBM);

        applySyncInvariants(succStateBuilder, sourceLocs);
        applySyncGuards(succStateBuilder, emittingEdge);
        applySyncGuards(succStateBuilder, receivingEdge);
        applySyncUpdates(succStateBuilder, emittingEdge);
        applySyncUpdates(succStateBuilder, receivingEdge);
        applySyncInvariants(succStateBuilder, targetLocs);

        if (shouldApplyDelay(action.getTargetLocs())) {
            applySyncDelay(succStateBuilder);
        }

        return constructNewZone(targetLocs, succStateBuilder.getDbm().extractDbms(actionDbmList), state);
    }

    private static LocalZoneState postForBroadcastAction(final LocalZoneState state,
                                                         final BroadcastXtaAction action,
                                                         final LocalZonePrec prec) {
        final List<Loc> sourceLocs = action.getSourceLocs();
        final Edge emitEdge = action.getEmitEdge();
        final List<Edge> recvEdges = action.getRecvEdges();
        final List<Collection<Edge>> nonRecvEdgeCols = action.getNonRecvEdges();
        final List<Loc> targetLocs = action.getTargetLocs();

        List<DBM.ProcessDbmPair> actionDbmList = fixOrderedDbmList(targetLocs, state);
        DBM jointDBM = DBM.joinDbms(actionDbmList);
        applyVirtualGuards(targetLocs, jointDBM, state);

        final ZoneState.Builder succStateBuilder = ZoneState.Builder.project(jointDBM);
        applySyncInvariants(succStateBuilder, sourceLocs);
        applySyncGuards(succStateBuilder, emitEdge);

        if (recvEdges.stream().anyMatch(XtaLocalZoneUtils::hasClockGuards)) {
            throw new UnsupportedOperationException(
                    "Clock guards on edges with broadcast synchronization labels are not supported.");
        }

        if (nonRecvEdgeCols.stream()
                .anyMatch(c -> c.stream().anyMatch(XtaLocalZoneUtils::hasClockGuards))) {
            throw new UnsupportedOperationException(
                    "Clock guards on edges with broadcast synchronization labels are not supported.");
        }

        applySyncUpdates(succStateBuilder, emitEdge);
        recvEdges.stream().forEachOrdered(recvEdge -> applySyncUpdates(succStateBuilder, recvEdge));
        applySyncInvariants(succStateBuilder, targetLocs);
        if (shouldApplyDelay(action.getTargetLocs())) {
            applySyncDelay(succStateBuilder);
        }
        return constructNewZone(targetLocs, succStateBuilder.getDbm().extractDbms(actionDbmList), state);
    }


    private static boolean hasClockGuards(Edge edge) {
        return edge.getGuards().stream().anyMatch(Guard::isClockGuard);
    }

    private static List<DBM.ProcessDbmPair> fixOrderedDbmList(final List<Loc> targetLocs, final LocalZoneState zone) {
        List<DBM.ProcessDbmPair> targetProcDbmMap = new ArrayList<>();
        for (var loc : targetLocs)
            targetProcDbmMap.add(new DBM.ProcessDbmPair(
                    loc.getProc().getName(), zone.getDbmForProcess(loc.getProc()).orElseThrow()
            ));

        return targetProcDbmMap;
    }

    private static LocalZoneState constructNewZone(List<Loc> orderOfProcesses, List<DBM> changedDbms,
                                                   LocalZoneState state) {
        var newState = new LocalZoneState(new HashMap<>(state.getLocalDbms()));
        for (var loc : orderOfProcesses)
            newState.setDbmForProc(loc.getProc(), changedDbms.remove(0));
        return newState;
    }

    private static void applyVirtualGuards(final List<Loc> locs, DBM jointDBM, LocalZoneState state) {
        Integer index = 0;
        Integer slidingIndex = 1;

        for (; slidingIndex < locs.size(); ) {
            DBM firstDbm = state.getDbmForProcess(locs.get(index).getProc()).get();
            DBM secondDbm = state.getDbmForProcess(locs.get(slidingIndex).getProc()).get();

            jointDBM.and(ClockConstrs.Eq(firstDbm.getLastVarDecl(), secondDbm.getLastVarDecl(), 0));
            index++;
            slidingIndex++;
        }
    }

    /// /
    //
    // A horrible copy paste from XtaZoneUtils, but they are private functions and I don't have time to solve this.
    private static void applySyncInvariants(final ZoneState.Builder builder, final Collection<Loc> locs) {
        for (final Loc target : locs) {
            for (final Guard invar : target.getInvars()) {
                if (invar.isClockGuard()) {
                    builder.and(invar.asClockGuard().getClockConstr());
                }
            }
        }
    }

    private static void applySyncGuards(final ZoneState.Builder builder, final Edge edge) {
        for (final Guard guard : edge.getGuards()) {
            if (guard.isClockGuard()) {
                builder.and(guard.asClockGuard().getClockConstr());
            }
        }
    }

    private static void applySyncUpdates(final ZoneState.Builder builder, final Edge edge) {
        for (final Update update : edge.getUpdates()) {
            if (update.isClockUpdate()) {
                final ResetOp op = (ResetOp) update.asClockUpdate().getClockOp();
                final VarDecl<RatType> varDecl = op.getVar();
                final int value = op.getValue();
                builder.reset(varDecl, value);
            }
        }
    }


    private static void applySyncDelay(final ZoneState.Builder builder) {
        builder.nonnegative();
        builder.up();
    }

    private static void applySyncInverseUpdates(final ZoneState.Builder builder, final Edge edge) {
        for (final Update update : Lists.reverse(edge.getUpdates())) {
            if (update.isClockUpdate()) {
                final ResetOp op = (ResetOp) update.asClockUpdate().getClockOp();
                final VarDecl<RatType> varDecl = op.getVar();
                final int value = op.getValue();
                builder.and(Eq(varDecl, value));
                builder.free(varDecl);
            }
        }
    }

    private static void applySyncInverseDelay(final ZoneState.Builder builder) {
        builder.down();
        builder.nonnegative();
    }


    /// /
    //
    public static LocalZoneState pre(final LocalZoneState state, final XtaAction action,
                                     final LocalZonePrec prec) {
        checkNotNull(state);
        checkNotNull(action);
        checkNotNull(prec);

        if (action.isBasic()) {
            return preForBasicAction(state, action.asBasic(), prec);
        } else if (action.isBinary()) {
            return preForBinaryAction(state, action.asBinary(), prec);
        } else if (action.isBroadcast()) {
            return preForBroadcastAction(state, action.asBroadcast(), prec);
        } else {
            throw new AssertionError();
        }
    }

    private static LocalZoneState preForBasicAction(final LocalZoneState state, final BasicXtaAction action,
                                                    final LocalZonePrec prec) {
        final LocalZoneState.Builder preStateBuilder = state.project(prec.getMapping());

        final List<Loc> sourceLocs = action.getSourceLocs();
        final Edge edge = action.getEdge();
        final List<Loc> targetLocs = action.getTargetLocs();

        List<Loc> involvedLocs = action.getTargetLocs();
        if (shouldApplyDelay(involvedLocs)) {
            applyInverseDelay(preStateBuilder, involvedLocs);
        }
        applyInvariants(preStateBuilder, targetLocs);
        applyInverseUpdates(preStateBuilder, edge);
        applyGuards(preStateBuilder, edge);
        applyInvariants(preStateBuilder, sourceLocs);

        return preStateBuilder.build();
    }

    private static LocalZoneState preForBinaryAction(LocalZoneState state, final BinaryXtaAction action,
                                                     final LocalZonePrec prec) {
        state=state.project(prec.getMapping()).build();
        final List<Loc> sourceLocs = action.getSourceLocs();
        final Edge emittingEdge = action.getEmitEdge();
        final Edge receivingEdge = action.getRecvEdge();
        final List<Loc> targetLocs = action.getTargetLocs();

        List<DBM.ProcessDbmPair> actionDbmList = fixOrderedDbmList(sourceLocs, state);
        DBM jointDBM = DBM.joinDbms(actionDbmList);

        final ZoneState.Builder preStateBuilder = ZoneState.Builder.project(jointDBM);

        List<Loc> involvedLocs = action.getTargetLocs();
        if (shouldApplyDelay(involvedLocs)) {
            applySyncInverseDelay(preStateBuilder);
        }
        applySyncInvariants(preStateBuilder, targetLocs);
        applySyncInverseUpdates(preStateBuilder, receivingEdge);
        applySyncInverseUpdates(preStateBuilder, emittingEdge);
        applySyncGuards(preStateBuilder, receivingEdge);
        applySyncGuards(preStateBuilder, emittingEdge);
        applySyncInvariants(preStateBuilder, sourceLocs);

        jointDBM = preStateBuilder.getDbm();
        applyVirtualGuards(targetLocs, jointDBM, state);

        return constructNewZone(targetLocs, jointDBM.extractDbms(actionDbmList), state);
    }

    private static LocalZoneState preForBroadcastAction(LocalZoneState state,
                                                        final BroadcastXtaAction action,
                                                        final LocalZonePrec prec) {
        state=state.project(prec.getMapping()).build();
        final List<Loc> sourceLocs = action.getSourceLocs();
        final Edge emitEdge = action.getEmitEdge();
        final List<Edge> reverseRecvEdges = Lists.reverse(action.getRecvEdges());
        final List<Collection<Edge>> nonRecvEdgeCols = action.getNonRecvEdges();
        final List<Loc> targetLocs = action.getTargetLocs();
        List<Loc> involvedLocs = action.getTargetLocs();

        List<DBM.ProcessDbmPair> actionDbmList = fixOrderedDbmList(sourceLocs, state);
        DBM jointDBM = DBM.joinDbms(actionDbmList);
        jointDBM.close();

        final ZoneState.Builder preStateBuilder = ZoneState.Builder.project(jointDBM);
        if (shouldApplyDelay(involvedLocs)) {
            applySyncInverseDelay(preStateBuilder);
        }
        applySyncInvariants(preStateBuilder, targetLocs);
        reverseRecvEdges.stream()
                .forEachOrdered(recvEdge -> applySyncInverseUpdates(preStateBuilder, recvEdge));
        applySyncInverseUpdates(preStateBuilder, emitEdge);

        if (nonRecvEdgeCols.stream()
                .anyMatch(c -> c.stream().anyMatch(XtaLocalZoneUtils::hasClockGuards))) {
            throw new UnsupportedOperationException(
                    "Clock guards on edges with broadcast synchronization labels are not supported.");
        }

        if (reverseRecvEdges.stream().anyMatch(XtaLocalZoneUtils::hasClockGuards)) {
            throw new UnsupportedOperationException(
                    "Clock guards on edges with broadcast synchronization labels are not supported.");
        }

        applySyncGuards(preStateBuilder, emitEdge);
        applySyncInvariants(preStateBuilder, sourceLocs);

        jointDBM = preStateBuilder.getDbm();

        applyVirtualGuards(targetLocs, jointDBM, state);

        return constructNewZone(targetLocs, jointDBM.extractDbms(actionDbmList), state);
    }

    /// /

    private static boolean shouldApplyDelay(final List<Loc> locs) {
        return locs.stream().allMatch(l -> l.getKind() == LocKind.NORMAL);
    }

    // This needs altering
    private static void applyDelay(final LocalZoneState.Builder builder, List<Loc> involvedLocs) {
        builder.nonnegative();
        for (var loc : involvedLocs) {
            builder.localUp(loc.getProc());
        }
    }

    // This needs altering
    private static void applyInverseDelay(final LocalZoneState.Builder builder, List<Loc> involvedLocs) {
        for (var loc : involvedLocs) {
            builder.localDown(loc.getProc());
        }
        builder.nonnegative();
    }

    private static void applyInvariants(final LocalZoneState.Builder builder,
                                        final Collection<Loc> locs) {
        for (final Loc target : locs) {
            for (final Guard invar : target.getInvars()) {
                if (invar.isClockGuard()) {
                    builder.and(invar.asClockGuard().getClockConstr());
                }
            }
        }
    }

    private static void applyUpdates(final LocalZoneState.Builder builder, final Edge edge) {
        for (final Update update : edge.getUpdates()) {
            if (update.isClockUpdate()) {
                final ResetOp op = (ResetOp) update.asClockUpdate().getClockOp();
                final VarDecl<RatType> varDecl = op.getVar();
                final int value = op.getValue();
                builder.reset(varDecl, value);
            }
        }
    }

    private static void applyInverseUpdates(final LocalZoneState.Builder builder, final Edge edge) {
        for (final Update update : Lists.reverse(edge.getUpdates())) {
            if (update.isClockUpdate()) {
                final ResetOp op = (ResetOp) update.asClockUpdate().getClockOp();
                final VarDecl<RatType> varDecl = op.getVar();
                final int value = op.getValue();
                builder.and(Eq(varDecl, value));
                builder.free(varDecl);
            }
        }
    }

    private static void applyGuards(final LocalZoneState.Builder builder, final Edge edge) {
        for (final Guard guard : edge.getGuards()) {
            if (guard.isClockGuard()) {
                builder.and(guard.asClockGuard().getClockConstr());
            }
        }
    }

    /**
     * Call global(sync([all process DBMs in the state])), as described in Govind 2021
     *
     * @param state source of process DBMs
     * @return DBM that is a global zone
     */
    public static DBM globalSync(LocalZoneState state) {
        var processDbmPairs = state.getLocalDbms().entrySet().stream()
                .map(entry -> new DBM.ProcessDbmPair(entry.getKey().getName(), entry.getValue()))
                .toList();

        return DBM.global(processDbmPairs, DBM.sync(processDbmPairs));
    }

    public static LocalZoneState syncedState(final LocalZoneState state) {
        var dbmMap = state.getLocalDbms().entrySet().stream().toList();
        var dbms = dbmMap.stream().map(entry -> new DBM.ProcessDbmPair(entry.getKey().getName(), entry.getValue()))
                .toList();


        var syncedDbms = DBM.sync(dbms).extractDbms(dbms).stream();
        var syncedMap = Streams.zip(dbmMap.stream(), syncedDbms,
                (pair, newDbm) -> Tuple2.of(pair.getKey(), newDbm)
        ).collect(Collectors.toMap(Tuple2::get1, Tuple2::get2));

        return new LocalZoneState(syncedMap);
    }


}
