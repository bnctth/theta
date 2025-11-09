// this is not a test!

package hu.bme.mit.theta.xta.analysis;

import hu.bme.mit.theta.analysis.expr.ExprMeetStrategy;
import hu.bme.mit.theta.common.Either;
import hu.bme.mit.theta.xta.XtaSystem;
import hu.bme.mit.theta.xta.analysis.lazy.ClockStrategy2;
import hu.bme.mit.theta.xta.analysis.lazy.DataStrategy2;
import hu.bme.mit.theta.xta.analysis.lazy.LazyXtaAbstractorConfig;
import hu.bme.mit.theta.xta.analysis.lazy.LazyXtaAbstractorConfigFactory;
import hu.bme.mit.theta.xta.dsl.XtaDslManager;
import hu.bme.mit.theta.xta.local_analysis.LazyXtaAndAnIntAbstractorConfig;
import hu.bme.mit.theta.xta.local_analysis.LazyXtaAndAnIntAbstractorConfigFactory;
import hu.bme.mit.theta.xta.local_analysis.SystemTypeFactory;
import hu.bme.mit.theta.xta.local_analysis.global_local.GLSystemTypeFactory;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static hu.bme.mit.theta.analysis.algorithm.SearchStrategy.BFS;

@RunWith(Parameterized.class)
public class EvaluateTest {
    private static final String MODEL_TEMPLATE = "/model/gl/gl-%d.xta";
    private static final int MIN_PROCESS = 2;
    private static final int MAX_PROCESS = 10;
    private static final String OUTPUT_TEMPLATE = "./src/test/resources/result/new/gl-%s.csv";
    private static final SystemTypeFactory SYSTEM_TYPE_FACTORY = GLSystemTypeFactory.create();

    @BeforeClass
    public static void beforeClass() throws Exception {
        for (var t : List.of("Global-NOPOR", "Local-NOPOR", "Local-POR", "LocalSyncSub-NOPOR", "LocalSyncSub-POR")) {
            FileWriter fw = new FileWriter(String.format(OUTPUT_TEMPLATE, t), false);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write("process_count,node_count\n");
            bw.close();
            fw.close();
        }
    }

    @Parameterized.Parameter(0)
    public String filepath;

    @Parameterized.Parameter(1)
    public ClockStrategy2.ZoneRepresentation zoneRepresentation;

    @Parameterized.Parameter(2)
    public boolean isPor;

    @Parameterized.Parameter(3)
    public int processCount;

    @Parameterized.Parameters(name = "model: {0}, zone: {1}, isPor: {2}")
    public static Collection<Object[]> data() {
        var result = new ArrayList<Object[]>();
        for (int i = MIN_PROCESS; i <= MAX_PROCESS; i++) {
            for (var zoneRep : List.of(ClockStrategy2.ZoneRepresentation.Global, ClockStrategy2.ZoneRepresentation.Local, ClockStrategy2.ZoneRepresentation.LocalSyncSub)) {
                result.add(new Object[]{String.format(MODEL_TEMPLATE, i), zoneRep, false, i});
            }
            result.add(new Object[]{String.format(MODEL_TEMPLATE, i), ClockStrategy2.ZoneRepresentation.Local, true, i});
            result.add(new Object[]{String.format(MODEL_TEMPLATE, i), ClockStrategy2.ZoneRepresentation.LocalSyncSub, true, i});
        }
        return result;
    }

    private Either<LazyXtaAbstractorConfig<?, ?, ?>, LazyXtaAndAnIntAbstractorConfig<?, ?, ?>> abstractor;

    @Before
    public void initialize() throws IOException {
        final InputStream inputStream = getClass().getResourceAsStream(filepath);
        final XtaSystem system = XtaDslManager.createSystem(inputStream);
        if (isPor) {
            abstractor = Either.Right(LazyXtaAndAnIntAbstractorConfigFactory.create(system, DataStrategy2.getValidStrategies().iterator().next(), new ClockStrategy2(ClockStrategy2.ClockStrategy.BWITP, zoneRepresentation), BFS, ExprMeetStrategy.SYNTACTIC, SYSTEM_TYPE_FACTORY));
        } else {
            abstractor = Either.Left(LazyXtaAbstractorConfigFactory.create(system, DataStrategy2.getValidStrategies().iterator().next(), new ClockStrategy2(ClockStrategy2.ClockStrategy.BWITP, zoneRepresentation), BFS, ExprMeetStrategy.SYNTACTIC));
        }
    }

    @Test
    public void evaluate() throws IOException {
        int n;
        if (isPor) {
            abstractor.right().check();
            n = abstractor.right().getArg().getNodes().toList().size();
        } else {
            abstractor.left().check();
            n = abstractor.left().getArg().getNodes().toList().size();
        }
        FileWriter fw = new FileWriter(String.format(OUTPUT_TEMPLATE, String.format("%s-%s", zoneRepresentation.toString(), isPor ? "POR" : "NOPOR")), true);
        BufferedWriter bw = new BufferedWriter(fw);
        bw.write(String.format("%d,%d%n", processCount + 1, n));
        bw.close();
        fw.close();
    }
}
