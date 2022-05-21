package net.splitcells.website.server.projects.extension;

import net.splitcells.dem.data.set.Set;
import net.splitcells.website.server.Config;
import net.splitcells.website.server.project.LayoutConfig;
import net.splitcells.website.server.project.RenderingResult;
import net.splitcells.website.server.projects.ProjectsRendererI;

import java.nio.file.Path;
import java.util.Optional;

import static net.splitcells.dem.data.set.Sets.setOfUniques;
import static net.splitcells.dem.utils.NotImplementedYet.notImplementedYet;

public class LayoutExtension implements ProjectsRendererExtension {
    public static LayoutExtension layoutExtension() {
        return new LayoutExtension();
    }

    private static final String PATH = "/net/splitcells/website/layout.html";

    private LayoutExtension() {

    }

    @Override
    public Optional<RenderingResult> renderFile(String path, ProjectsRendererI projectsRendererI, Config config) {
        if (PATH.equals(path) && config.layout().isPresent()) {
            return projectsRendererI.renderContent(config.layout().get(), LayoutConfig.layoutConfig(PATH));
        }
        return Optional.empty();
    }

    @Override
    public Set<Path> projectPaths(ProjectsRendererI projectsRendererI) {
        if (projectsRendererI.config().layout().isPresent()) {
            return setOfUniques(Path.of(PATH.substring(1)));
        }
        return setOfUniques();
    }
}
