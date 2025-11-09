package hu.bme.mit.theta.xta.analysis;

import hu.bme.mit.theta.analysis.algorithm.ArgChecker;
import hu.bme.mit.theta.analysis.expr.ExprMeetStrategy;
import hu.bme.mit.theta.analysis.utils.ArgVisualizer;
import hu.bme.mit.theta.common.visualization.writer.GraphvizWriter;
import hu.bme.mit.theta.solver.z3.Z3SolverFactory;
import hu.bme.mit.theta.xta.XtaSystem;
import hu.bme.mit.theta.xta.analysis.lazy.ClockStrategy2;
import hu.bme.mit.theta.xta.analysis.lazy.DataStrategy2;
import hu.bme.mit.theta.xta.dsl.XtaDslManager;
import hu.bme.mit.theta.xta.local_analysis.LazyXtaAndAnIntAbstractorConfig;
import hu.bme.mit.theta.xta.local_analysis.LazyXtaAndAnIntAbstractorConfigFactory;
import hu.bme.mit.theta.xta.local_analysis.SystemTypeFactory;
import hu.bme.mit.theta.xta.local_analysis.global_local.GLSystemTypeFactory;
import hu.bme.mit.theta.xta.local_analysis.server_client.SCSystemTypeFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static hu.bme.mit.theta.analysis.algorithm.SearchStrategy.BFS;
import static org.junit.Assert.assertTrue;

@RunWith(Parameterized.class)
public final class LazyPORXtaAbstractorTest {
    private static final String MODEL_MINIMAL = "/model/gl.xta";

    private static final Collection<String> MODELS = List.of(MODEL_MINIMAL);

    @Parameter(0)
    public String filepath;

    @Parameter(1)
    public DataStrategy2 dataStrategy;

    @Parameter(2)
    public ClockStrategy2 clockStrategy;

    @Parameter(3)
    public SystemTypeFactory systemTypeFactory;

    private LazyXtaAndAnIntAbstractorConfig<?, ?, ?> abstractor;

    @Parameters(name = "model: {0}, discrete: {1}, clock: {2}, type: {3}")
    public static Collection<Object[]> data() {
        final Collection<Object[]> result = new ArrayList<>();
        for (final String model : MODELS) {
            DataStrategy2 dataStrategy=DataStrategy2.getValidStrategies().iterator().next();

          //  for (final DataStrategy2 dataStrategy : DataStrategy2.getValidStrategies()) {
                for (final ClockStrategy2 clockStrategy : ClockStrategy2.getValidStrategies()) {
                    //for (final SystemTypeFactory factory : List.of(GLSystemTypeFactory.create(), SCSystemTypeFactory.create())) {
                        if (clockStrategy.getZoneRepresentation() != ClockStrategy2.ZoneRepresentation.LocalSyncSub) {
                            continue;
                        }
                        result.add(new Object[]{model, dataStrategy, clockStrategy, SCSystemTypeFactory.create()});
                   // }
                }
            //}
        }
        return result;
    }

    @Before
    public void initialize() throws IOException {
        final InputStream inputStream = getClass().getResourceAsStream(filepath);
        final XtaSystem system = XtaDslManager.createSystem(inputStream);
        abstractor = LazyXtaAndAnIntAbstractorConfigFactory.create(system, dataStrategy, clockStrategy, BFS, ExprMeetStrategy.SYNTACTIC, systemTypeFactory);
    }

    @Test
    public void test() throws IOException, InterruptedException {
        test(abstractor);
    }

    private void test(LazyXtaAndAnIntAbstractorConfig<?, ?, ?> abstractor) throws IOException, InterruptedException {
        // Act
        abstractor.check();
        var modelName = filepath.split("/")[filepath.split("/").length - 1];
        new File("./output-por").mkdirs();
        GraphvizWriter.getInstance().writeFile(ArgVisualizer.getDefault().visualize(abstractor.getArg()), "./output-por/" + modelName + "-" + dataStrategy + "-" + clockStrategy + ".svg", GraphvizWriter.Format.SVG);
        System.out.println(abstractor.getArg().getNodes().count());

        // Assert
        final ArgChecker argChecker = ArgChecker.create(Z3SolverFactory.getInstance().createSolver());
        final boolean argCheckResult = argChecker.isWellLabeled(abstractor.getArg());
        assertTrue(argCheckResult);
    }
}
