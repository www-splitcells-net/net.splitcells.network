package net.splitcells.website;

import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.environment.config.framework.OptionI;
import net.splitcells.website.server.renderer.ProjectRenderer;
import net.splitcells.website.server.renderer.ProjectsRenderer;

import java.nio.file.Path;
import java.nio.file.Paths;

import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.utils.Not_implemented_yet.not_implemented_yet;
import static net.splitcells.website.server.renderer.ProjectsRenderer.projectsRenderer;

public class Projects {
    public static ProjectsRenderer projectsRenderer() {
        throw not_implemented_yet();
    }

    public static ProjectRenderer fallbackProjectRenderer(String profile, Path projectRepositories) {
        throw not_implemented_yet();
    }

    public static List<ProjectRenderer> projectRenderers(String profile, Path projectRepositories) {
        return list();
    }
}
