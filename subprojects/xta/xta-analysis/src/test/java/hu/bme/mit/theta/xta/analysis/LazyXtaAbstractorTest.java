package hu.bme.mit.theta.xta.analysis;

import com.google.common.collect.ImmutableSet;
import hu.bme.mit.theta.analysis.expr.ExprMeetStrategy;
import hu.bme.mit.theta.xta.XtaSystem;
import hu.bme.mit.theta.xta.analysis.lazy.ClockStrategy2;
import hu.bme.mit.theta.xta.analysis.lazy.DataStrategy2;
import hu.bme.mit.theta.xta.analysis.lazy.LazyXtaAbstractorConfig;
import hu.bme.mit.theta.xta.analysis.lazy.LazyXtaAbstractorConfigFactory;
import hu.bme.mit.theta.xta.dsl.XtaDslManager;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static hu.bme.mit.theta.analysis.algorithm.SearchStrategy.BFS;

@RunWith(Parameterized.class)
public final class LazyXtaAbstractorTest {
    private static final String MODEL_CSMA = "/model/csma-2.xta";
    private static final String MODEL_FDDI = "/model/fddi-2.xta";
    private static final String MODEL_FISCHER = "/model/fischer-2-32-64.xta";
    private static final String MODEL_LYNCH = "/model/lynch-2-16.xta";
    private static final String MODEL_ENGINE = "/model/engine-classic.xta";
    private static final String MODEL_BROADCAST = "/model/broadcast.xta";

    private static final Collection<String> MODELS = List.of(MODEL_CSMA, MODEL_FDDI, MODEL_FISCHER,
            MODEL_LYNCH/*, MODEL_ENGINE, MODEL_BROADCAST*/);

    private static final Collection<String> MODELS_WITH_UNKNOWN_SOLVER_STATUS = ImmutableSet.of(MODEL_CSMA, MODEL_FDDI,
            MODEL_ENGINE, MODEL_BROADCAST);

    private static final Collection<String> MODELS_WITH_GLOBAL_CLOCK = ImmutableSet.of(MODEL_ENGINE);

    @Parameter(0)
    public String filepath;

    @Parameter(1)
    public DataStrategy2 dataStrategy;

    @Parameter(2)
    public ClockStrategy2 clockStrategy;

    private LazyXtaAbstractorConfig<?, ?, ?> abstractor;

    @Parameters(name = "model: {0}, discrete: {1}, clock: {2}")
    public static Collection<Object[]> data() {
        final Collection<Object[]> result = new ArrayList<>();
        //for (final String model : MODELS) {
        String model = "/model/gl/gl-2.xta";
        DataStrategy2 dataStrategy = DataStrategy2.getValidStrategies().iterator().next();
        ClockStrategy2 clockStrategy = new ClockStrategy2(ClockStrategy2.ClockStrategy.BWITP, ClockStrategy2.ZoneRepresentation.LocalSyncSub);
        // for (final DataStrategy2 dataStrategy : DataStrategy2.getValidStrategies()) {
//        for (final ClockStrategy2 clockStrategy : ClockStrategy2.getValidStrategies()) {
//            //if (!MODELS_WITH_UNKNOWN_SOLVER_STATUS.contains(model) || (clockStrategy.getClockStrategy() != LU)) {
//            if (clockStrategy.getClockStrategy() == LU)
//                continue;
            result.add(new Object[]{model, dataStrategy, clockStrategy});
            // }
//        }
        //}
        //}
        return result;
    }

    @Before
    public void initialize() throws IOException {
        final InputStream inputStream = getClass().getResourceAsStream(filepath);
        final XtaSystem system = XtaDslManager.createSystem(inputStream);
        abstractor = LazyXtaAbstractorConfigFactory.create(system, dataStrategy, clockStrategy, BFS, ExprMeetStrategy.SYNTACTIC);
    }

    @Test
    public void test() throws IOException, InterruptedException {
        test(abstractor);
    }

    private void test(LazyXtaAbstractorConfig<?, ?, ?> abstractor) throws IOException, InterruptedException {
        // Act
        abstractor.check();
      /*  var modelName = filepath.split("/")[filepath.split("/").length - 1];
        new File("./output").mkdirs();
        GraphvizWriter.getInstance().writeFile(ArgVisualizer.getDefault().visualize(abstractor.getArg()), "./output/" + modelName + "-" + dataStrategy + "-" + clockStrategy + ".svg", GraphvizWriter.Format.SVG);*/
        System.out.println(abstractor.getArg().getNodes().count());
        // Assert
        /*final ArgChecker argChecker = ArgChecker.create(Z3SolverFactory.getInstance().createSolver());
        final boolean argCheckResult = argChecker.isWellLabeled(abstractor.getArg());
        assertTrue(argCheckResult);*/
    }
}
