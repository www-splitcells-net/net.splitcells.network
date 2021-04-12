package net.splitcells.gel;

import net.splitcells.dem.data.atom.Bools;
import net.splitcells.dem.environment.config.IsDeterministic;
import net.splitcells.dem.resource.Paths;
import net.splitcells.dem.resource.host.ProcessHostPath;
import net.splitcells.dem.resource.host.interaction.IsEchoToFile;
import net.splitcells.dem.resource.host.interaction.MessageFilter;
import net.splitcells.dem.testing.Test;

import java.util.Optional;

import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.testing.Test.testIntegration;
import static net.splitcells.dem.utils.ConstructorIllegal.constructorIllegal;
import static net.splitcells.gel.GelEnv.standardConfigurator;

public final class GelTestIntegration {
    private GelTestIntegration() {
        throw constructorIllegal();
    }

    public static void main(String... arg) {
        testIntegration();
        /*if (GelEnv.process(() -> {
                    if (testIntegration()) {
                        throw new RuntimeException();
                    }
                }
                , standardConfigurator().andThen(env -> {
                    env.config()
                            .withConfigValue(MessageFilter.class, a -> false)
                            .withConfigValue(IsDeterministic.class, Optional.of(Bools.truthful()));
                })).hasError()) {
            System.exit(1);
        }*/
    }
}
