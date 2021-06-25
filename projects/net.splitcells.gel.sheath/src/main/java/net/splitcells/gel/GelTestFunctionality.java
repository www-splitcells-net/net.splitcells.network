package net.splitcells.gel;

import net.splitcells.dem.data.atom.Bools;
import net.splitcells.dem.environment.config.IsDeterministic;
import net.splitcells.dem.resource.host.interaction.MessageFilter;

import java.util.Optional;

import static net.splitcells.dem.testing.Test.testFunctionality;
import static net.splitcells.gel.GelEnv.standardDeveloperConfigurator;

public class GelTestFunctionality {
    public static void main(String... args) {
        if (GelEnv.process(() -> {
                    if (!testFunctionality()) {
                        throw new RuntimeException();
                    }
                }
                , standardDeveloperConfigurator().andThen(env -> {
                    env.config()
                            .withConfigValue(MessageFilter.class, a -> false)
                            .withConfigValue(IsDeterministic.class, Optional.of(Bools.truthful()));
                })).hasError()) {
            System.exit(1);
        }
    }
}
