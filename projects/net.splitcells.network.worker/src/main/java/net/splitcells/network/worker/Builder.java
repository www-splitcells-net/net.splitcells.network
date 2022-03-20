package net.splitcells.network.worker;

import net.splitcells.dem.data.set.list.Lists;
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
        testFunctionality(list(logger(userHome("Documents/projects/net.splitcells.martins.avots.support.system/public/net.splitcells.network.log"))));
    }

    private Builder() {
        throw constructorIllegal();
    }
}
