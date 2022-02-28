package net.splitcells.website.server.projects;

import net.splitcells.dem.data.set.Set;
import net.splitcells.dem.data.set.Sets;
import net.splitcells.website.server.Config;
import net.splitcells.website.server.project.ProjectsRenderer;
import net.splitcells.website.server.project.RenderingResult;

import java.nio.file.Path;
import java.util.Optional;

import static net.splitcells.dem.data.set.Sets.setOfUniques;

public class GlobalChangelogExtension implements ProjectsRendererExtension {
    public static GlobalChangelogExtension globalChangelogExtension() {
        return new GlobalChangelogExtension();
    }

    private GlobalChangelogExtension() {

    }

    @Override
    public Optional<RenderingResult> renderFile(String path, ProjectsRenderer projectsRenderer, Config config) {
        return Optional.empty();
    }

    @Override
    public Set<Path> projectPaths(ProjectsRenderer projectsRenderer) {
        return setOfUniques(Path.of(projectsRenderer.config().rootPath() + "/CHANGELOG.global.html"));
    }
}
