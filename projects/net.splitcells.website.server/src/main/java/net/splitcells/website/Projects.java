package net.splitcells.website;

import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.environment.config.framework.OptionI;
import net.splitcells.website.server.renderer.ProjectRenderer;
import net.splitcells.website.server.renderer.ProjectsRenderer;

import java.nio.file.Path;
import java.nio.file.Paths;

import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.utils.NotImplementedYet.not_implemented_yet;
import static net.splitcells.website.server.renderer.ProjectsRenderer.projectsRenderer;

public class Projects {
    public static ProjectsRenderer projectsRenderer() {
        final var profile = "public";
        final var projectsRepository = Paths.get("../");
        return projectsRenderer(projectsRepository
                , profile
                , fallbackProjectRenderer(profile, projectsRepository)
                , list());
    }

    public static ProjectsRenderer projectsRenderer(Path projectRepository, String profile
            , ProjectRenderer fallbackProjectRenderer
            , List<ProjectRenderer> additionalProjects) {
        return ProjectsRenderer.projectsRenderer(profile, fallbackProjectRenderer
                , additionalProjects.withAppended(projectRenderers(profile, projectRepository)));
    }

    public static ProjectRenderer fallbackProjectRenderer(String profile, Path projectRepositories) {
        return new ProjectRenderer(profile
                , projectRepositories.resolve("net.splitcells.website.default.content/src/main/")
                , projectRepositories.resolve("net.splitcells.website.default.content/src/main/xsl/net/splitcells/website/den/translation/to/html/")
                , projectRepositories.resolve("net.splitcells.website.default.content/src/main/resources/content")
                , "/");
    }

    public static List<ProjectRenderer> projectRenderers(String profile, Path projectRepositories) {
        return list(new ProjectRenderer
                        (profile
                                , projectRepositories.resolve("net.splitcells.dem/src/main/")
                                , projectRepositories.resolve("net.splitcells.website.default.content/src/main/xsl/net/splitcells/website/den/translation/to/html/")
                                , projectRepositories.resolve("net.splitcells.website.default.content/src/main/resources/html")
                                , "/net/splitcells/dem")
                , new ProjectRenderer
                        (profile
                                , projectRepositories.resolve("../src/main/")
                                , projectRepositories.resolve("net.splitcells.website.default.content/src/main/xsl/net/splitcells/website/den/translation/to/html/")
                                , projectRepositories.resolve("net.splitcells.martins.avots.website/src/main/resources/html")
                                , "/net/splitcells/network")
                , new ProjectRenderer
                        (profile
                                , projectRepositories.resolve("net.splitcells.gel.doc/src/main/")
                                , projectRepositories.resolve("net.splitcells.website.default.content/src/main/xsl/net/splitcells/website/den/translation/to/html/")
                                , projectRepositories.resolve("net.splitcells.martins.avots.website/src/main/resources/html")
                                , "/net/splitcells/gel")
                , new ProjectRenderer
                        (profile
                                , projectRepositories.resolve("net.splitcells.system/src/main/")
                                , projectRepositories.resolve("net.splitcells.website.default.content/src/main/xsl/net/splitcells/website/den/translation/to/html/")
                                , projectRepositories.resolve("net.splitcells.martins.avots.website/src/main/resources/html")
                                , "/net/splitcells")
                , new ProjectRenderer
                        (profile
                                , projectRepositories.resolve("net.splitcells.website.default.content/src/main/")
                                , projectRepositories.resolve("net.splitcells.website.default.content/src/main/xsl/net/splitcells/website/den/translation/to/html/")
                                , projectRepositories.resolve("net.splitcells.website.default.content/src/main/resources/html")
                                , "/net/splitcells/website")
        );
    }
}
