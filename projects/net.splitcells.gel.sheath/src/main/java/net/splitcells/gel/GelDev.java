package net.splitcells.gel;

import net.splitcells.dem.DemTest;
import net.splitcells.dem.data.set.list.Lists;
import net.splitcells.dem.resource.Paths;
import net.splitcells.dem.resource.host.ProcessHostPath;
import net.splitcells.dem.resource.host.interaction.IsEchoToFile;
import net.splitcells.dem.resource.host.interaction.MessageFilter;
import net.splitcells.dem.testing.Test;
import net.splitcells.gel.constraint.ConstraintTest;
import net.splitcells.gel.rating.rater.MinimalDistanceTest;
import net.splitcells.gel.test.integration.OralExamsTest;

import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.utils.ConstructorIllegal.constructorIllegal;
import static net.splitcells.gel.GelEnv.process;
import static net.splitcells.gel.GelEnv.standardConfigurator;

public final class GelDev {
    private GelDev() {
        throw constructorIllegal();
    }

    public static void main(String... arg) {
        process(() -> {
            //new MinimalDistanceTest().test_multiple_line_addition_and_removal();
            //new ConstraintTest().test_incomingGroupsOfConstraintPath();
            new OralExamsTest().testCurrent();
        }, standardConfigurator().andThen(env -> {
            env.config()
                    .withConfigValue(MessageFilter.class
                            , a -> a.path().equals(list("debugging")))
                    /*.withConfigValue(MessageFilter.class
                            , a -> a.path().equals(list("demands", "Solution", "optimization", "Escalator"))
                                    || a.path().equals(list("demands", "Solution")))*/
                    .withConfigValue(IsEchoToFile.class, true)
                    .withConfigValue(ProcessHostPath.class
                            , Paths.userHome("connections", "tmp.storage", "dem"));
        }));
    }
}
