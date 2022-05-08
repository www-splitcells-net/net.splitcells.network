package net.splitcells.dem.resource.host;

import net.splitcells.dem.environment.config.framework.OptionI;

import java.nio.file.Path;

import static net.splitcells.dem.resource.host.SystemUtils.runShellScript;
import static net.splitcells.dem.utils.ExecutionException.executionException;

public class HostName extends OptionI<String> {
    public HostName() {
        super(() -> {
            final var hostname = runShellScript("hostname", Path.of("."));
            if (hostname.exitCode() != 0) {
                throw executionException("Could not determine hostname: " + hostname.output());
            }
            return hostname.output();
        });
    }
}
