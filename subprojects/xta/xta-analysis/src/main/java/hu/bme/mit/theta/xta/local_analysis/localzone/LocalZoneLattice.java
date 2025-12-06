package hu.bme.mit.theta.xta.local_analysis.localzone;

import hu.bme.mit.theta.analysis.Lattice;
import hu.bme.mit.theta.analysis.PartialOrd;
import hu.bme.mit.theta.analysis.zone.DBM;
import hu.bme.mit.theta.common.Tuple2;
import hu.bme.mit.theta.core.decl.VarDecl;
import hu.bme.mit.theta.core.type.rattype.RatType;
import hu.bme.mit.theta.xta.XtaProcess;

import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

public final class LocalZoneLattice implements Lattice<LocalZoneState> {

    private final PartialOrd<LocalZoneState> partialOrd;
    private final Map<XtaProcess, DBM> processTopDbmMap;

    public LocalZoneLattice(PartialOrd<LocalZoneState> partialOrd, Map<XtaProcess, Collection<VarDecl<RatType>>> processClockMap) {
        this.partialOrd = partialOrd;

        processTopDbmMap = processClockMap.entrySet().stream()
                .map(pair -> Tuple2.of(pair.getKey(), DBM.top(pair.getValue())))
                .collect(Collectors.toMap(Tuple2::get1, Tuple2::get2));
    }

    @Override
    public LocalZoneState top() {
        return new LocalZoneStateTop(Map.copyOf(processTopDbmMap));
    }

    @Override
    public LocalZoneState bottom() {
        return LocalZoneStateBottom.getInstance();
    }

    @Override
    public LocalZoneState meet(final LocalZoneState lhs, final LocalZoneState rhs) {
        return LocalZoneState.intersection(lhs, rhs);
    }

    @Override
    public LocalZoneState join(final LocalZoneState lhs, final LocalZoneState rhs) {
        return LocalZoneState.enclosure(lhs, rhs);
    }

    @Override
    public boolean isLeq(final LocalZoneState lhs, final LocalZoneState rhs) {
        return partialOrd.isLeq(lhs, rhs);
    }
}
