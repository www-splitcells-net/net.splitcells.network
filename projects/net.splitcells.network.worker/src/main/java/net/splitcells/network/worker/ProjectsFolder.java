package net.splitcells.network.worker;

import net.splitcells.dem.environment.config.framework.OptionI;

import java.nio.file.Path;
import java.util.function.Supplier;

import static net.splitcells.dem.resource.Paths.userHome;

public class ProjectsFolder extends OptionI<Path> {
    public ProjectsFolder() {
        super(() -> userHome("Documents/projects/net.splitcells.martins.avots.support.system/public/"));
    }
}
