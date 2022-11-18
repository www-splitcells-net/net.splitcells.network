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

import net.splitcells.dem.Dem;
import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.resource.communication.interaction.LogLevel;
import net.splitcells.website.server.Config;
import net.splitcells.website.server.project.ProjectRenderer;
import net.splitcells.website.server.project.renderer.ObjectsMediaRenderer;
import net.splitcells.website.server.project.renderer.ObjectsRenderer;
import net.splitcells.website.server.projects.ProjectsRendererI;
import net.splitcells.website.server.project.validator.SourceValidator;

import java.nio.file.Path;
import java.nio.file.Paths;

import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.resource.Files.isDirectory;
import static net.splitcells.dem.resource.communication.log.Domsole.domsole;
import static net.splitcells.dem.utils.NotImplementedYet.notImplementedYet;
import static net.splitcells.website.server.project.validator.SourceValidatorViaSchema.validatorViaSchema;
import static net.splitcells.website.server.project.ProjectRenderer.projectRenderer;

public class Projects {
    @Deprecated
    public static ProjectsRendererI projectsRenderer(Config config) {
        final var profile = "public";
        final var projectsRepository = Paths.get("../");
        final var validator = validatorViaSchema(net.splitcells.dem.resource.Paths.path("src/main/xsd/den.xsd"));
        return projectsRenderer(projectsRepository
                , profile
                , fallbackProjectRenderer(profile, projectsRepository, validator, config)
                , list()
                , validator
                , config);
    }

    public static ProjectsRendererI projectsRenderer(Path projectRepository, String profile
            , ProjectRenderer fallbackProjectRenderer
            , List<ProjectRenderer> additionalProjects
            , SourceValidator sourceValidator
            , Config config) {
        final var xslLib = projectRepository
                .resolve("net.splitcells.website.default.content/src/main/xsl/net/splitcells/website/den/translation/to/html/");
        return ProjectsRendererI.projectsRenderer(profile, fallbackProjectRenderer
                , additionalProjects.withAppended(projectRenderers(profile, projectRepository, sourceValidator, xslLib, config))
                , config);
    }

    public static ProjectsRendererI projectsRenderer(Path projectRepository, String profile
            , ProjectRenderer fallbackProjectRenderer
            , List<ProjectRenderer> additionalProjects
            , SourceValidator sourceValidator
            , Path xslLib
            , Config config) {
        return ProjectsRendererI.projectsRenderer(profile, fallbackProjectRenderer
                , additionalProjects.withAppended(projectRenderers(profile, projectRepository, sourceValidator, xslLib, config))
                , config);
    }

    public static ProjectRenderer fallbackProjectRenderer(String profile, Path projectRepositories, SourceValidator sourceValidator, Config config) {
        return projectRenderer(profile
                , projectRepositories.resolve("net.splitcells.website.default.content/")
                , projectRepositories.resolve("net.splitcells.website.default.content/src/main/xsl/net/splitcells/website/den/translation/to/html/")
                , projectRepositories.resolve("net.splitcells.website.default.content/src/main/resources/content")
                , "/"
                , sourceValidator
                , config);
    }

    public static List<ProjectRenderer> projectRenderers(String profile, Path integratedProjectRepositories, SourceValidator sourceValidator
            , Path xslLib
            , Config config) {
        final var projectClusterRepository = integratedProjectRepositories.resolve("../../");
        final var projectRenderers = list(projectRenderer
                        (profile
                                , integratedProjectRepositories.resolve("net.splitcells.dem/")
                                , xslLib
                                , integratedProjectRepositories.resolve("net.splitcells.website.default.content/src/main/resources/html")
                                , "/net/splitcells/dem"
                                , sourceValidator
                                , config)
                , projectRenderer
                        (profile
                                , integratedProjectRepositories.resolve("../")
                                , xslLib
                                , integratedProjectRepositories.resolve("net.splitcells.martins.avots.website/src/main/resources/html")
                                , "/net/splitcells/network"
                                , sourceValidator
                                , config)
                , projectRenderer
                        (profile
                                , integratedProjectRepositories.resolve("net.splitcells.gel.doc/")
                                , xslLib
                                , integratedProjectRepositories.resolve("net.splitcells.martins.avots.website/src/main/resources/html")
                                , "/net/splitcells/gel"
                                , sourceValidator
                                , config)
                , projectRenderer
                        (profile
                                , integratedProjectRepositories.resolve("net.splitcells.gel.core/")
                                , xslLib
                                , integratedProjectRepositories.resolve("net.splitcells.martins.avots.website/src/main/resources/html")
                                , "/net/splitcells/gel"
                                , sourceValidator
                                , config)
                , projectRenderer
                        (profile
                                , integratedProjectRepositories.resolve("net.splitcells.gel.sheath/")
                                , xslLib
                                , integratedProjectRepositories.resolve("net.splitcells.martins.avots.website/src/main/resources/html")
                                , "/net/splitcells/gel"
                                , sourceValidator
                                , config)
                , projectRenderer
                        (profile
                                , integratedProjectRepositories.resolve("net.splitcells.system/")
                                , xslLib
                                , integratedProjectRepositories.resolve("net.splitcells.martins.avots.website/src/main/resources/html")
                                , "/net/splitcells/system"
                                , sourceValidator
                                , config)
                , projectRenderer
                        (profile
                                , integratedProjectRepositories.resolve("net.splitcells.website.default.content/")
                                , xslLib
                                , integratedProjectRepositories.resolve("net.splitcells.website.default.content/src/main/resources/html")
                                , "/net/splitcells/website"
                                , sourceValidator
                                , config)
                , projectRenderer
                        (profile
                                , integratedProjectRepositories.resolve("net.splitcells.website.server/")
                                , xslLib
                                , integratedProjectRepositories.resolve("net.splitcells.website.default.content/src/main/resources/html")
                                , "/"
                                , sourceValidator
                                , config)
                , projectRenderer
                        (profile
                                , integratedProjectRepositories.resolve("net.splitcells.os.state.interface/")
                                , xslLib
                                , integratedProjectRepositories.resolve("net.splitcells.martins.avots.website/src/main/resources/html")
                                , "/net/splitcells/os/state/interface"
                                , sourceValidator
                                , config)
                , projectRenderer
                        (profile
                                , integratedProjectRepositories.resolve("net.splitcells.dem.merger/")
                                , xslLib
                                , integratedProjectRepositories.resolve("net.splitcells.martins.avots.website/src/main/resources/html")
                                , "/net/splitcells/dem"
                                , sourceValidator
                                , config)
                , projectRenderer
                        (profile
                                , integratedProjectRepositories.resolve("net.splitcells.dem.core/")
                                , xslLib
                                , integratedProjectRepositories.resolve("net.splitcells.martins.avots.website/src/main/resources/html")
                                , "/net/splitcells/dem"
                                , sourceValidator
                                , config)
                , projectRenderer
                        (profile
                                , integratedProjectRepositories.resolve("net.splitcells.network.worker/")
                                , xslLib
                                , integratedProjectRepositories.resolve("net.splitcells.martins.avots.website/src/main/resources/html")
                                , "/net/splitcells/network/worker"
                                , sourceValidator
                                , config)
                , projectRenderer
                        (profile
                                , integratedProjectRepositories.resolve("net.splitcells.cin/")
                                , xslLib
                                , integratedProjectRepositories.resolve("net.splitcells.martins.avots.website/src/main/resources/html")
                                , "/net/splitcells/cin"
                                , sourceValidator
                                , config)
                , Dem.configValue(ObjectsRenderer.class)
                , Dem.configValue(ObjectsMediaRenderer.class)
        );
        if (Dem.configValue(RenderUserStateRepo.class)) {
            projectRenderers.withAppended(projectRenderer(profile
                    , net.splitcells.dem.resource.Paths.usersStateFiles()
                    , xslLib
                    , integratedProjectRepositories.resolve("net.splitcells.martins.avots.website/src/main/resources/html")
                    , "/"
                    , sourceValidator
                    , config));
        }
        if (isDirectory(integratedProjectRepositories)) {
            projectRenderers.add(projectRenderer
                    (profile
                            , projectClusterRepository.resolve("net.splitcells.network.media")
                            , xslLib
                            , integratedProjectRepositories.resolve("net.splitcells.website.default.content/src/main/resources/html")
                            , "/net/splitcells"
                            , sourceValidator
                            , config));
        }
        if (isDirectory(integratedProjectRepositories.resolve("../../net.splitcells.network.log/"))) {
            projectRenderers.add(projectRenderer
                    (profile
                            , integratedProjectRepositories.resolve("../../net.splitcells.network.log/")
                            , xslLib
                            , integratedProjectRepositories.resolve("net.splitcells.website.default.content/src/main/resources/html")
                            , "/"
                            , sourceValidator
                            , config));
        } else {
            domsole().append("Project 'net.splitcells.network.log' does not exist.", LogLevel.WARNING);
        }
        return projectRenderers;
    }
}
