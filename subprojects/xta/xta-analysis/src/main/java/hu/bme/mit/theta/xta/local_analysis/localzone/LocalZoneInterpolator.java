package hu.bme.mit.theta.xta.local_analysis.localzone;

import hu.bme.mit.theta.analysis.algorithm.lazy.itp.Interpolator;
import hu.bme.mit.theta.analysis.zone.DBM;
import hu.bme.mit.theta.analysis.zone.ZoneState;
import hu.bme.mit.theta.common.Tuple2;
import hu.bme.mit.theta.xta.XtaProcess;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public final class LocalZoneInterpolator implements Interpolator<LocalZoneState, ZoneState> {

    private static final LocalZoneInterpolator INSTANCE = new LocalZoneInterpolator();

    private LocalZoneInterpolator() {
    }

    public static LocalZoneInterpolator getInstance() {
        return INSTANCE;
    }

    @Override
    public ZoneState toItpDom(final LocalZoneState state) {
        var jointDBM = DBM.joinDbms(state.getDbmList().stream().map(dbm->new DBM.ProcessDbmPair("", dbm)).toList());
        return ZoneState.Builder.project(jointDBM).build();
    }

    @Override
    public LocalZoneState interpolate(final LocalZoneState lhs, final ZoneState rhs) {
        var globalInterpolant = ZoneState.interpolant(toItpDom(lhs), rhs);
        var jointDbmForSignature=DBM.joinDbms(lhs.getDbmList().stream().map(dbm->new DBM.ProcessDbmPair("", dbm)).toList());;
        var dbms = DBM.project(globalInterpolant.transform().getDbm(), jointDbmForSignature.signature).extractDbms(lhs.getDbmList().stream().map(dbm->new DBM.ProcessDbmPair("", dbm)).toList());
        Map<XtaProcess, DBM> mappedDbms = IntStream.range(0, lhs.getDbmList().size()).boxed()
                .map(i -> Tuple2.of(List.copyOf(lhs.getLocalDbms().keySet()).get(i), dbms.get(i)))
                .collect(Collectors.toMap(Tuple2::get1, Tuple2::get2));
        return new LocalZoneState(mappedDbms);
    }

    @Override
    public Collection<ZoneState> complement(final ZoneState zone) {
        return zone.complement();
    }

    @Override
    public boolean refutes(final LocalZoneState lhs, final ZoneState rhs) {
        return !toItpDom(lhs).isConsistentWith(rhs);
    }
}
