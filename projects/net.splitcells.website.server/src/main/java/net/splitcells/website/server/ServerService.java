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
package net.splitcells.website.server;

import net.splitcells.dem.Dem;
import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.environment.resource.ResourceOptionI;
import net.splitcells.dem.environment.resource.Service;
import net.splitcells.website.content.defaults.WebsiteContentDefaultsFileSystem;
import net.splitcells.website.server.project.ProjectRenderer;
import net.splitcells.website.server.project.renderer.ObjectsMediaRenderer;
import net.splitcells.website.server.project.renderer.ObjectsRenderer;
import net.splitcells.website.server.project.validator.SourceValidator;
import net.splitcells.website.server.projects.ProjectsRenderer;
import net.splitcells.website.server.projects.ProjectsRendererI;

import java.nio.file.Path;
import java.util.Optional;
import java.util.function.Function;

import static net.splitcells.dem.Dem.configValue;
import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.data.set.list.Lists.toList;
import static net.splitcells.dem.resource.FileSystems.fileSystemOnLocalHost;
import static net.splitcells.dem.resource.Files.readFileAsString;
import static net.splitcells.dem.resource.Paths.userHome;
import static net.splitcells.dem.utils.NotImplementedYet.notImplementedYet;
import static net.splitcells.website.Projects.projectsRenderer;
import static net.splitcells.website.server.project.ProjectRenderer.projectRenderer;
import static net.splitcells.website.server.project.validator.SourceValidator.VOID_VALIDATOR;
import static net.splitcells.website.server.project.validator.SourceValidatorViaSchema.validatorViaSchema;

public class ServerService extends ResourceOptionI<Service> {
    public ServerService() {
        super(() -> {
            return new Service() {
                Service httpServer;

                @Override
                public void start() {
                    httpServer = projectsRenderer(Dem.configValue(ServerConfig.class)).httpServer();
                    httpServer.start();
                }

                @Override
                public void close() {
                    httpServer.close();
                }

                @Override
                public void flush() {
                    httpServer.flush();
                }
            };
        });
    }

    public static ProjectsRendererI projectsRenderer(Config config) {
        final var profile = "public";
        final var validator = VOID_VALIDATOR;
        return projectsRenderer(profile
                , projectsRenderer -> fallbackProjectRenderer(projectsRenderer, profile, validator, config)
                , projectsRenderer -> config.additionalProjects().stream()
                        .map(project ->
                                projectRenderer(profile
                                        , project.projectFiles()
                                        , configValue(WebsiteContentDefaultsFileSystem.class)
                                                .subFileSystemView("src/main/xsl/net/splitcells/website/den/translation/to/html/")
                                        , configValue(WebsiteContentDefaultsFileSystem.class)
                                                .subFileSystemView("src/main/resources/html")
                                        , project.rootPath()
                                        , validator
                                        , config
                                        , projectsRenderer))
                        .collect(toList())
                        .withAppended(Dem.configValue(ObjectsRenderer.class)
                                , Dem.configValue(ObjectsMediaRenderer.class))
                , config);
    }

    public static ProjectsRendererI projectsRenderer(String profile
            , Function<ProjectsRenderer, ProjectRenderer> fallbackProjectRenderer
            , Function<ProjectsRenderer, List<ProjectRenderer>> additionalProjects
            , Config config) {
        return ProjectsRendererI.projectsRenderer(profile, fallbackProjectRenderer, additionalProjects, config);
    }

    public static ProjectRenderer fallbackProjectRenderer(ProjectsRenderer projectsRenderer, String profile
            , SourceValidator sourceValidator
            , Config config) {
        return projectRenderer(profile
                , configValue(WebsiteContentDefaultsFileSystem.class)
                , configValue(WebsiteContentDefaultsFileSystem.class)
                        .subFileSystemView("src/main/xsl/net/splitcells/website/den/translation/to/html/")
                , configValue(WebsiteContentDefaultsFileSystem.class)
                        .subFileSystemView("src/main/resources/html")
                , "/"
                , sourceValidator
                , config
                , projectsRenderer);
    }
}
