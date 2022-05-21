package net.splitcells.website.server.projects.extension;

import net.splitcells.dem.data.set.Set;
import net.splitcells.dem.lang.perspective.Perspective;
import net.splitcells.website.server.Config;
import net.splitcells.website.server.project.RenderingResult;
import net.splitcells.website.server.projects.ProjectsRendererI;

import java.nio.file.Path;
import java.util.Optional;

import static net.splitcells.dem.data.set.Sets.setOfUniques;

public interface ProjectsRendererExtension {
    Optional<RenderingResult> renderFile(String path, @Deprecated ProjectsRendererI projectsRendererI, Config config);

    default Perspective extendProjectLayout(Perspective layout, @Deprecated ProjectsRendererI projectsRendererI) {
        return layout;
    }

    Set<Path> projectPaths(@Deprecated ProjectsRendererI projectsRendererI);

    default Set<Path> relevantProjectPaths(@Deprecated ProjectsRendererI projectsRendererI) {
        return projectPaths(projectsRendererI);
    }
}
