package net.splitcells.gel;

import net.splitcells.dem.resource.Paths;
import net.splitcells.dem.resource.host.ProcessHostPath;
import net.splitcells.dem.resource.host.interaction.IsEchoToFile;
import net.splitcells.gel.test.integration.OralExamsTest;

import static net.splitcells.dem.utils.ConstructorIllegal.constructorIllegal;
import static net.splitcells.gel.GelEnv.process;
import static net.splitcells.gel.GelEnv.standardConfigurator;

public final class GelDev {
    private GelDev() {
        throw constructorIllegal();
    }

    public static void main(String... arg) {
        process(() -> {
            new OralExamsTest().testCurrent();
        }, standardConfigurator().andThen(env -> {
            env.config()
                    .withConfigValue(IsEchoToFile.class, true)
                    .withConfigValue(ProcessHostPath.class, Paths.userHome("connections", "tmp.storage", "dem"));
        }));
    }
}
