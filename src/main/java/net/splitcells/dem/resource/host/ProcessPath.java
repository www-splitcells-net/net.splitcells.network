package net.splitcells.dem.resource.host;

import net.splitcells.dem.environment.config.ProgramName;
import net.splitcells.dem.environment.config.framework.OptionI;

import java.nio.file.Path;
import java.nio.file.Paths;

import static net.splitcells.dem.Dem.environment;

/**
 * IDEA Only use target folder during development.
 */
public class ProcessPath extends OptionI<Path> {
    public ProcessPath() {
        super(() ->
                environment().config().configValue(ProcessHostPath.class)
                        .resolve(Paths.get("."
                                , environment().config().configValue(ProgramName.class).split("\\."))));
    }
}