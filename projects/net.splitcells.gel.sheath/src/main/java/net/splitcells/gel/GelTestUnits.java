package net.splitcells.gel;

import net.splitcells.dem.resource.host.interaction.MessageFilter;

import static net.splitcells.dem.resource.host.Files.writeToFile;
import static net.splitcells.dem.testing.Test.testUnits;
import static net.splitcells.dem.utils.ConstructorIllegal.constructorIllegal;
import static net.splitcells.gel.GelEnv.standardDeveloperConfigurator;

public final class GelTestUnits {
    private GelTestUnits() {
        throw constructorIllegal();
    }

    public static void main(String... arg) {
        GelEnv.process(() -> {
                    if (!testUnits()) {
                        System.exit(1);
                    }
                }
                , standardDeveloperConfigurator().andThen(env -> {
                    env.config().withConfigValue(MessageFilter.class, a -> false);
                }));
    }
}
