package net.splitcells.network.worker;

import net.splitcells.dem.Dem;
import net.splitcells.dem.data.set.list.Lists;
import net.splitcells.dem.environment.config.ProgramName;
import net.splitcells.dem.resource.Paths;

import java.nio.file.Path;

import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.resource.Paths.userHome;
import static net.splitcells.dem.testing.Test.testFunctionality;
import static net.splitcells.dem.utils.ConstructorIllegal.constructorIllegal;
import static net.splitcells.network.worker.Logger.logger;

/**
 * maven.execute net.splitcells.network.worker.Builder
 */
public class Builder {
    public static void main(String... args) {
        if (args.length != 1) {
            throw new IllegalArgumentException("Exactly one argument is required, but " + args.length + " were given. The argument is the id of the test executor.");
        }
        Dem.process(() -> testFunctionality(list(logger(userHome("Documents/projects/net.splitcells.martins.avots.support.system/public/net.splitcells.network.log"))))
                , env -> env.config().withConfigValue(ProgramName.class, args[0]));
    }

    private Builder() {
        throw constructorIllegal();
    }
}
