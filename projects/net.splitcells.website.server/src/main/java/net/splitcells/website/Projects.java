package net.splitcells.website;

import net.splitcells.dem.data.set.list.List;
import net.splitcells.website.server.renderer.ProjectRenderer;
import net.splitcells.website.server.renderer.ProjectsRenderer;

import java.nio.file.Path;
import java.nio.file.Paths;

import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.utils.NotImplementedYet.notImplementedYet;
import static net.splitcells.website.ValidatorViaSchema.validatorViaSchema;
import static net.splitcells.website.server.renderer.ProjectRenderer.projectRenderer;

public class Projects {
    public static ProjectsRenderer projectsRenderer() {
        final var profile = "public";
        final var projectsRepository = Paths.get("../");
        final var validator = validatorViaSchema(net.splitcells.dem.resource.Paths.path("src/main/xsd/den.xsd"));
        return projectsRenderer(projectsRepository
                , profile
                , fallbackProjectRenderer(profile, projectsRepository, validator)
                , list()
                , validator);
    }

    public static ProjectsRenderer projectsRenderer(Path projectRepository, String profile
            , ProjectRenderer fallbackProjectRenderer
            , List<ProjectRenderer> additionalProjects
            , Validator validator) {
        return ProjectsRenderer.projectsRenderer(profile, fallbackProjectRenderer
                , additionalProjects.withAppended(projectRenderers(profile, projectRepository, validator)));
    }

    public static ProjectRenderer fallbackProjectRenderer(String profile, Path projectRepositories, Validator validator) {
        return projectRenderer(profile
                , projectRepositories.resolve("net.splitcells.website.default.content/")
                , projectRepositories.resolve("net.splitcells.website.default.content/src/main/xsl/net/splitcells/website/den/translation/to/html/")
                , projectRepositories.resolve("net.splitcells.website.default.content/src/main/resources/content")
                , "/"
                , validator);
    }

    public static List<ProjectRenderer> projectRenderers(String profile, Path projectRepositories, Validator validator) {
        return list(projectRenderer
                        (profile
                                , projectRepositories.resolve("net.splitcells.dem/")
                                , projectRepositories.resolve("net.splitcells.website.default.content/src/main/xsl/net/splitcells/website/den/translation/to/html/")
                                , projectRepositories.resolve("net.splitcells.website.default.content/src/main/resources/html")
                                , "/net/splitcells/dem"
                                , validator)
                ,projectRenderer
                        (profile
                                , projectRepositories.resolve("../")
                                , projectRepositories.resolve("net.splitcells.website.default.content/src/main/xsl/net/splitcells/website/den/translation/to/html/")
                                , projectRepositories.resolve("net.splitcells.martins.avots.website/src/main/resources/html")
                                , "/net/splitcells/network"
                                , validator)
                , projectRenderer
                        (profile
                                , projectRepositories.resolve("net.splitcells.gel.doc/")
                                , projectRepositories.resolve("net.splitcells.website.default.content/src/main/xsl/net/splitcells/website/den/translation/to/html/")
                                , projectRepositories.resolve("net.splitcells.martins.avots.website/src/main/resources/html")
                                , "/net/splitcells/gel"
                                , validator)
                , projectRenderer
                        (profile
                                , projectRepositories.resolve("net.splitcells.system/")
                                , projectRepositories.resolve("net.splitcells.website.default.content/src/main/xsl/net/splitcells/website/den/translation/to/html/")
                                , projectRepositories.resolve("net.splitcells.martins.avots.website/src/main/resources/html")
                                , "/net/splitcells"
                                , validator)
                , projectRenderer
                        (profile
                                , projectRepositories.resolve("net.splitcells.website.default.content/")
                                , projectRepositories.resolve("net.splitcells.website.default.content/src/main/xsl/net/splitcells/website/den/translation/to/html/")
                                , projectRepositories.resolve("net.splitcells.website.default.content/src/main/resources/html")
                                , "/net/splitcells/website"
                                , validator)
        );
    }
}
