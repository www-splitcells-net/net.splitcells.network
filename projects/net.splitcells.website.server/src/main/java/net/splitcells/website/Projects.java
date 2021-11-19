/*
 * Copyright (c) 2021 Mārtiņš Avots (Martins Avots) and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0, or the MIT License,
 * which is available at https://spdx.org/licenses/MIT.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR MIT
 */
package net.splitcells.website;

import net.splitcells.dem.data.set.list.List;
import net.splitcells.website.server.project.ProjectRenderer;
import net.splitcells.website.server.project.ProjectsRenderer;

import java.nio.file.Path;
import java.nio.file.Paths;

import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.utils.NotImplementedYet.notImplementedYet;
import static net.splitcells.website.ValidatorViaSchema.validatorViaSchema;
import static net.splitcells.website.server.project.ProjectRenderer.projectRenderer;

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
                                , projectRepositories.resolve("net.splitcells.gel.sheath/")
                                , projectRepositories.resolve("net.splitcells.website.default.content/src/main/xsl/net/splitcells/website/den/translation/to/html/")
                                , projectRepositories.resolve("net.splitcells.martins.avots.website/src/main/resources/html")
                                , "/net/splitcells/gel"
                                , validator)
                , projectRenderer
                        (profile
                                , projectRepositories.resolve("net.splitcells.system/")
                                , projectRepositories.resolve("net.splitcells.website.default.content/src/main/xsl/net/splitcells/website/den/translation/to/html/")
                                , projectRepositories.resolve("net.splitcells.martins.avots.website/src/main/resources/html")
                                , "/net/splitcells/system"
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
