package net.splitcells.dem.resource.host;

import net.splitcells.dem.environment.config.framework.OptionI;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.Supplier;

public class ProcessHostPath extends OptionI<Path> {
    public ProcessHostPath() {
        super(() -> Paths.get("."));
    }
}
