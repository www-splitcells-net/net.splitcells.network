package net.splitcells.dem;

import net.splitcells.dem.lang.Xml;
import net.splitcells.dem.lang.namespace.NameSpaces;
import net.splitcells.dem.resource.host.interaction.Domsole;
import net.splitcells.dem.resource.host.interaction.IsEchoToFile;
import net.splitcells.dem.resource.host.interaction.LogLevel;
import net.splitcells.dem.resource.host.interaction.MessageFilter;

import java.util.Optional;

/**
 * Used for experiments.
 *
 * maven.execute net.splitcells.dem.DemTest
 */
public class DemTest {
    public static void main(String... args) {
        Dem.process(() -> {
        }, (env) -> {
            env.config()
                    .withConfigValue(MessageFilter.class, (message) -> true);
        });
    }
}
