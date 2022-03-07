package net.splitcells.website.server.projects;

import net.splitcells.dem.data.set.Set;
import net.splitcells.dem.lang.perspective.Perspective;
import net.splitcells.website.server.Config;
import net.splitcells.website.server.project.RenderingResult;

import java.nio.file.Path;
import java.util.Optional;

import static net.splitcells.dem.data.set.Sets.setOfUniques;

public interface ProjectsRendererExtension {
    Optional<RenderingResult> renderFile(String path, ProjectsRenderer projectsRenderer, Config config);

    default Perspective extendProjectLayout(Perspective layout, ProjectsRenderer projectsRenderer) {
        return layout;
    }

    Set<Path> projectPaths(ProjectsRenderer projectsRenderer);

    default Set<Path> relevantProjectPaths(ProjectsRenderer projectsRenderer) {
        return projectPaths(projectsRenderer);
    }
}
