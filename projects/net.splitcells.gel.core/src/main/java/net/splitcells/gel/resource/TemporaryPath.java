package net.splitcells.gel.resource;

import static net.splitcells.dem.Dem.environment;

import java.nio.file.Path;

import net.splitcells.dem.environment.config.framework.OptionI;
import net.splitcells.dem.resource.host.ProcessPath;

public class TemporaryPath extends OptionI<Path> {
    public TemporaryPath() {
        super(() -> environment().config()
                .configValue(ProcessPath.class)
                .resolve("tmp"));
    }
}
