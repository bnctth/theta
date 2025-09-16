package hu.bme.mit.theta.xta.analysis;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

import hu.bme.mit.theta.xta.local_analysis.LocalZoneOrd;
import hu.bme.mit.theta.xta.local_analysis.XtaLocalAnalysis;
import hu.bme.mit.theta.xta.local_analysis.localzone.LocalZonePrec;
import hu.bme.mit.theta.xta.local_analysis.localzone.LocalZoneState;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

import hu.bme.mit.theta.analysis.Analysis;
import hu.bme.mit.theta.analysis.LTS;
import hu.bme.mit.theta.analysis.algorithm.ARG;
import hu.bme.mit.theta.analysis.algorithm.ArgBuilder;
import hu.bme.mit.theta.analysis.algorithm.cegar.Abstractor;
import hu.bme.mit.theta.analysis.algorithm.cegar.BasicAbstractor;
import hu.bme.mit.theta.analysis.expl.ExplState;
import hu.bme.mit.theta.analysis.impl.PrecMappingAnalysis;
import hu.bme.mit.theta.analysis.prod2.Prod2Analysis;
import hu.bme.mit.theta.analysis.prod2.Prod2Prec;
import hu.bme.mit.theta.analysis.prod2.Prod2State;
import hu.bme.mit.theta.analysis.unit.UnitPrec;
import hu.bme.mit.theta.analysis.zone.ZonePrec;
import hu.bme.mit.theta.analysis.zone.ZoneState;
import hu.bme.mit.theta.xta.XtaSystem;
import hu.bme.mit.theta.xta.analysis.expl.XtaExplAnalysis;
import hu.bme.mit.theta.xta.analysis.zone.XtaZoneAnalysis;
import hu.bme.mit.theta.xta.dsl.XtaDslManager;


@RunWith(Parameterized.class)
public final class XtaLocalZoneAbsTest {

    @Parameters(name = "{0}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{

                //{"/model/csma-2.xta"},

                //{"/model/fddi-2.xta"},

                //{"/model/fischer-2-32-64.xta"},

                {"/model/lynch-2-16.xta"},

                //{"/model/broadcast.xta"},

        });
    }

    @Parameter(0)
    public String filepath;

    @Test
    public void test() throws FileNotFoundException, IOException {
        final InputStream inputStream = getClass().getResourceAsStream(filepath);
        final XtaSystem system = XtaDslManager.createSystem(inputStream);

        final LTS<XtaState<?>, XtaAction> lts = XtaLts.create(system);
        final Analysis<ExplState, XtaAction, UnitPrec> explAnalysis = XtaExplAnalysis.create(system);
        final Analysis<LocalZoneState, XtaAction, LocalZonePrec> zoneAnalysis = new XtaLocalAnalysis(LocalZoneOrd.getInstance());
        final Analysis<Prod2State<ExplState, LocalZoneState>, XtaAction, Prod2Prec<UnitPrec, LocalZonePrec>> prodAnalysis = Prod2Analysis
                .create(explAnalysis, zoneAnalysis);
        final Analysis<Prod2State<ExplState, LocalZoneState>, XtaAction, LocalZonePrec> mappedAnalysis = PrecMappingAnalysis
                .create(prodAnalysis, z -> Prod2Prec.of(UnitPrec.getInstance(), z));
        final Analysis<XtaState<Prod2State<ExplState, LocalZoneState>>, XtaAction, LocalZonePrec> analysis = XtaAnalysis
                .create(system, mappedAnalysis);

        final LocalZonePrec prec = LocalZonePrec.of(system.getProcessClockMap());

        long startTime = System.currentTimeMillis();

        final ArgBuilder<XtaState<Prod2State<ExplState, LocalZoneState>>, XtaAction, LocalZonePrec> argBuilder = ArgBuilder
                .create(lts, analysis, s -> false);

        final Abstractor<XtaState<Prod2State<ExplState, LocalZoneState>>, XtaAction, LocalZonePrec> abstractor = BasicAbstractor
                .builder(argBuilder).projection(s -> s.getLocs()).build();

        final ARG<XtaState<Prod2State<ExplState, LocalZoneState>>, XtaAction> arg = abstractor.createArg();
        abstractor.check(arg, prec);

        long endTime = System.currentTimeMillis();

        System.out.println(arg.getNodes().collect(Collectors.toSet()));

        System.out.println(arg.getNodes().count());

        System.out.println(endTime - startTime);
    }

}
