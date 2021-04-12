package net.splitcells.gel;

import net.splitcells.dem.data.atom.Bools;
import net.splitcells.dem.environment.config.IsDeterministic;
import net.splitcells.dem.resource.Paths;
import net.splitcells.dem.resource.host.ProcessHostPath;
import net.splitcells.dem.resource.host.interaction.IsEchoToFile;
import net.splitcells.dem.resource.host.interaction.MessageFilter;
import net.splitcells.dem.utils.random.DeterministicRootSourceSeed;
import net.splitcells.gel.test.integration.OralExamsTest;

import java.util.Optional;

import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.utils.ConstructorIllegal.constructorIllegal;
import static net.splitcells.gel.GelEnv.process;
import static net.splitcells.gel.GelEnv.standardConfigurator;

public final class GelDev {
    private GelDev() {
        throw constructorIllegal();
    }

    public static void main(String... arg) {
        new OralExamsTest().testRandomInstanceSolving();
        /*process(() -> {
            //new MinimalDistanceTest().test_multiple_line_addition_and_removal();
            //new ConstraintTest().test_incomingGroupsOfConstraintPath();
            new OralExamsTest().testCurrentDevelopment();
        }, standardConfigurator().andThen(env -> {
            env.config()
                    .withConfigValue(MessageFilter.class
                            , a -> a.path().equals(list("debugging")))
                    //.withConfigValue(MessageFilter.class
                    //        , a -> a.path().equals(list("demands", "Solution", "optimization", "Escalator"))
                    //                || a.path().equals(list("demands", "Solution")))
                    .withConfigValue(IsEchoToFile.class, true)
                    .withConfigValue(IsDeterministic.class, Optional.of(Bools.truthful()))
                    .withConfigValue(DeterministicRootSourceSeed.class, 1000L)
                    .withConfigValue(ProcessHostPath.class
                            , Paths.userHome("connections", "tmp.storage", "dem"));
        }));*/
    }
}
