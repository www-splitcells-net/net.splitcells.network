package net.splitcells.gel;

import net.splitcells.dem.resource.Paths;
import net.splitcells.dem.resource.host.ProcessHostPath;
import net.splitcells.dem.resource.host.interaction.IsEchoToFile;
import net.splitcells.dem.resource.host.interaction.MessageFilter;
import net.splitcells.dem.testing.Test;

import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.testing.Test.testIntegration;
import static net.splitcells.dem.utils.ConstructorIllegal.constructorIllegal;
import static net.splitcells.gel.GelEnv.standardConfigurator;

public final class GelTestIntegration {
    private GelTestIntegration() {
        throw constructorIllegal();
    }

    public static void main(String... arg) {
        GelEnv.process(() -> {
                    if (testIntegration()) {
                        System.exit(1);
                    }
                }
                , standardConfigurator().andThen(env -> {
                    env.config().withConfigValue(MessageFilter.class, a -> false);
                }));
    }
}
