/*
 * Copyright (c) 2021 Contributors To The `net.splitcells.*` Projects
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the Eclipse
 * Public License, v. 2.0 are satisfied: GNU General Public License v2.0 or later
 * which is available at https://www.gnu.org/licenses/old-licenses/gpl-2.0-standalone.html
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.website;

import net.splitcells.dem.Dem;
import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.resource.FileSystem;
import net.splitcells.dem.resource.FileSystems;
import net.splitcells.dem.resource.communication.log.LogLevel;
import net.splitcells.website.server.Config;
import net.splitcells.website.server.project.ProjectRenderer;
import net.splitcells.website.server.project.renderer.ObjectsMediaRenderer;
import net.splitcells.website.server.project.renderer.ObjectsRenderer;
import net.splitcells.website.server.projects.ProjectsRenderer;
import net.splitcells.website.server.projects.ProjectsRendererI;
import net.splitcells.website.server.project.validator.SourceValidator;

import java.nio.file.Path;
import java.util.Optional;
import java.util.function.Function;

import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.resource.FileSystems.fileSystemOnLocalHost;
import static net.splitcells.dem.resource.communication.log.Logs.logs;
import static net.splitcells.dem.utils.ConstructorIllegal.constructorIllegal;
import static net.splitcells.dem.utils.NotImplementedYet.notImplementedYet;
import static net.splitcells.website.server.project.validator.SourceValidator.VOID_VALIDATOR;
import static net.splitcells.website.server.project.validator.SourceValidatorViaSchema.validatorViaSchema;
import static net.splitcells.website.server.project.ProjectRenderer.projectRenderer;

public class Projects {
    public static ProjectsRendererI projectsRenderer(Config config) {
        final var profile = "public";
        final var projectsRepository = config.mainProjectRepositoryPath().orElse(Path.of("../"));
        final var validator = config.xmlSchema().map(s -> (SourceValidator) validatorViaSchema(s))
                .orElse(VOID_VALIDATOR);
        return projectsRenderer(projectsRepository
                , profile
                , projectsRenderer -> fallbackProjectRenderer(profile, projectsRepository, validator, config, projectsRenderer)
                , projectsRenderer -> list()
                , validator
                , config);
    }

    public static ProjectsRendererI projectsRenderer(Path projectRepository, String profile
            , Function<ProjectsRenderer, ProjectRenderer> fallbackProjectRenderer
            , Function<ProjectsRenderer, List<ProjectRenderer>> additionalProjects
            , SourceValidator sourceValidator
            , Config config) {
        final var xslLib = projectRepository
                .resolve("net.splitcells.website.content.default/src/main/xsl/net/splitcells/website/den/translation/to/html/");
        return ProjectsRendererI.projectsRenderer(profile, fallbackProjectRenderer
                , projectsRenderer -> additionalProjects.apply(projectsRenderer)
                        .withAppended(projectRenderers(profile
                                , fileSystemOnLocalHost(projectRepository)
                                , fileSystemOnLocalHost(projectRepository.resolve("../../"))
                                , sourceValidator
                                , fileSystemOnLocalHost(xslLib)
                                , config
                                , projectsRenderer))
                , config);
    }

    public static ProjectsRendererI projectsRenderer(FileSystem projectClusterRepo, String profile
            , Function<ProjectsRenderer, ProjectRenderer> fallbackProjectRenderer
            , Function<ProjectsRenderer, List<ProjectRenderer>> additionalProjects
            , SourceValidator sourceValidator
            , FileSystem xslLib
            , Config config) {
        return ProjectsRendererI.projectsRenderer(profile, fallbackProjectRenderer
                , projectsRenderer -> additionalProjects.apply(projectsRenderer).withAppended(projectRenderers(profile
                        , projectClusterRepo.subFileSystem("net.splitcells.network/projects/")
                        , projectClusterRepo
                        , sourceValidator
                        , xslLib
                        , config
                        , projectsRenderer))
                , config);
    }

    public static ProjectRenderer fallbackProjectRenderer(String profile, Path projectRepositories
            , SourceValidator sourceValidator
            , Config config
            , ProjectsRenderer projectsRenderer) {
        return projectRenderer(profile
                , fileSystemOnLocalHost(projectRepositories.resolve("net.splitcells.website.content.default/"))
                , fileSystemOnLocalHost(projectRepositories.resolve("net.splitcells.website.content.default/src/main/xsl/net/splitcells/website/den/translation/to/html/"))
                , fileSystemOnLocalHost(projectRepositories.resolve("net.splitcells.website.content.default/src/main/resources/content"))
                , "/"
                , sourceValidator
                , config
                , projectsRenderer);
    }

    public static List<ProjectRenderer> projectRenderers(String profile
            , FileSystem integratedProjectRepo
            , FileSystem projectClusterRepo
            , SourceValidator sourceValidator
            , FileSystem xslLib
            , Config config
            , ProjectsRenderer projectsRenderer) {
        final var projectRenderers = list(projectRenderer
                        (profile
                                , integratedProjectRepo.subFileSystem("net.splitcells.dem/")
                                , xslLib
                                , integratedProjectRepo.subFileSystem("net.splitcells.website.content.default/src/main/resources/html")
                                , "/net/splitcells/dem"
                                , sourceValidator
                                , config
                                , projectsRenderer)
                , projectRenderer
                        (profile
                                , integratedProjectRepo.subFileSystem("net.splitcells.network/")
                                , xslLib
                                , integratedProjectRepo.subFileSystem("net.splitcells.martins.avots.website/src/main/resources/html")
                                , "/net/splitcells/network"
                                , sourceValidator
                                , config
                                , projectsRenderer)
                , projectRenderer
                        (profile
                                , integratedProjectRepo.subFileSystem("net.splitcells.gel.doc/")
                                , xslLib
                                , integratedProjectRepo.subFileSystem("net.splitcells.martins.avots.website/src/main/resources/html")
                                , "/net/splitcells/gel"
                                , sourceValidator
                                , config
                                , projectsRenderer)
                , projectRenderer
                        (profile
                                , integratedProjectRepo.subFileSystem("net.splitcells.gel.core/")
                                , xslLib
                                , integratedProjectRepo.subFileSystem("net.splitcells.martins.avots.website/src/main/resources/html")
                                , "/net/splitcells/gel"
                                , sourceValidator
                                , config
                                , projectsRenderer)
                , projectRenderer
                        (profile
                                , integratedProjectRepo.subFileSystem("net.splitcells.gel.ext/")
                                , xslLib
                                , integratedProjectRepo.subFileSystem("net.splitcells.martins.avots.website/src/main/resources/html")
                                , "/net/splitcells/gel"
                                , sourceValidator
                                , config
                                , projectsRenderer)
                , projectRenderer
                        (profile
                                , integratedProjectRepo.subFileSystem("net.splitcells.system/")
                                , xslLib
                                , integratedProjectRepo.subFileSystem("net.splitcells.martins.avots.website/src/main/resources/html")
                                , "/net/splitcells/system"
                                , sourceValidator
                                , config
                                , projectsRenderer)
                , projectRenderer
                        (profile
                                , integratedProjectRepo.subFileSystem("net.splitcells.website.content.default/")
                                , xslLib
                                , integratedProjectRepo.subFileSystem("net.splitcells.website.content.default/src/main/resources/html")
                                , "/net/splitcells/website"
                                , sourceValidator
                                , config
                                , projectsRenderer)
                , projectRenderer
                        (profile
                                , integratedProjectRepo.subFileSystem("net.splitcells.website.server/")
                                , xslLib
                                , integratedProjectRepo.subFileSystem("net.splitcells.website.content.default/src/main/resources/html")
                                , "/"
                                , sourceValidator
                                , config
                                , projectsRenderer)
                , projectRenderer
                        (profile
                                , integratedProjectRepo.subFileSystem("net.splitcells.shell/")
                                , xslLib
                                , integratedProjectRepo.subFileSystem("net.splitcells.martins.avots.website/src/main/resources/html")
                                , "/net/splitcells/shell"
                                , sourceValidator
                                , config
                                , projectsRenderer)
                , projectRenderer
                        (profile
                                , integratedProjectRepo.subFileSystem("net.splitcells.dem.api/")
                                , xslLib
                                , integratedProjectRepo.subFileSystem("net.splitcells.martins.avots.website/src/main/resources/html")
                                , "/net/splitcells/dem"
                                , sourceValidator
                                , config
                                , projectsRenderer)
                , projectRenderer
                        (profile
                                , integratedProjectRepo.subFileSystem("net.splitcells.dem.core/")
                                , xslLib
                                , integratedProjectRepo.subFileSystem("net.splitcells.martins.avots.website/src/main/resources/html")
                                , "/net/splitcells/dem"
                                , sourceValidator
                                , config
                                , projectsRenderer)
                , projectRenderer
                        (profile
                                , integratedProjectRepo.subFileSystem("net.splitcells.network.worker.via.java/")
                                , xslLib
                                , integratedProjectRepo.subFileSystem("net.splitcells.martins.avots.website/src/main/resources/html")
                                , "/net/splitcells/network/worker/via/java"
                                , sourceValidator
                                , config
                                , projectsRenderer)
                , projectRenderer
                        (profile
                                , integratedProjectRepo.subFileSystem("net.splitcells.cin/")
                                , xslLib
                                , integratedProjectRepo.subFileSystem("net.splitcells.martins.avots.website/src/main/resources/html")
                                , "/net/splitcells/cin"
                                , sourceValidator
                                , config
                                , projectsRenderer)
                , Dem.configValue(ObjectsRenderer.class)
                , Dem.configValue(ObjectsMediaRenderer.class)
        );
        if (Dem.configValue(RenderUserStateRepo.class)) {
            projectRenderers.withAppended(projectRenderer(profile
                    , FileSystems.usersStateFiles()
                    , xslLib
                    , integratedProjectRepo.subFileSystem("net.splitcells.martins.avots.website/src/main/resources/html")
                    , "/"
                    , sourceValidator
                    , config
                    , projectsRenderer));
        }
        if (projectClusterRepo.isDirectory("net.splitcells.network.media")) {
            projectRenderers.add(projectRenderer
                    (profile
                            , projectClusterRepo.subFileSystem("net.splitcells.network.media")
                            , xslLib
                            , integratedProjectRepo.subFileSystem("net.splitcells.website.content.default/src/main/resources/html")
                            , "/net/splitcells"
                            , sourceValidator
                            , config
                            , projectsRenderer));
        }
        if (projectClusterRepo.isDirectory("net.splitcells.network.log")) {
            projectRenderers.add(projectRenderer
                    (profile
                            , projectClusterRepo.subFileSystem("net.splitcells.network.log")
                            , xslLib
                            , integratedProjectRepo.subFileSystem("net.splitcells.website.content.default/src/main/resources/html")
                            , "/"
                            , sourceValidator
                            , config
                            , projectsRenderer));
        } else {
            logs().append("Project 'net.splitcells.network.log' does not exist.", LogLevel.WARNING);
        }
        if (projectClusterRepo.isDirectory("net.splitcells.symbiosis")) {
            projectRenderers.add(projectRenderer
                    (profile
                            , projectClusterRepo.subFileSystem("net.splitcells.symbiosis")
                            , xslLib
                            , integratedProjectRepo.subFileSystem("net.splitcells.website.content.default/src/main/resources/html")
                            , "/"
                            , sourceValidator
                            , config
                            , projectsRenderer));
        } else {
            logs().append("Project 'net.splitcells.symbiosis' does not exist.", LogLevel.WARNING);
        }
        return projectRenderers;
    }

    private Projects() {
        throw constructorIllegal();
    }
}
