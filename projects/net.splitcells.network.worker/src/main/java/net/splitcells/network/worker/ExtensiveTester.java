package net.splitcells.network.worker;

import net.splitcells.dem.Dem;
import net.splitcells.dem.environment.config.ProgramName;

import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.testing.Test.testExtensively;
import static net.splitcells.dem.testing.Test.testUnits;
import static net.splitcells.dem.utils.ConstructorIllegal.constructorIllegal;
import static net.splitcells.network.worker.Logger.logger;

public class ExtensiveTester {
    public static void main(String... args) {
        if (args.length != 1) {
            throw new IllegalArgumentException("Exactly one argument is required, but " + args.length + " were given. The argument is the id of the test executor.");
        }
        Dem.process(() -> testExtensively(list(logger()))
                , env -> env.config().withConfigValue(ProgramName.class, args[0]));
    }

    private ExtensiveTester() {
        throw constructorIllegal();
    }
}
