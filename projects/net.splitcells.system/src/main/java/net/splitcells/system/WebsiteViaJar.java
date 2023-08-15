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
package net.splitcells.system;

import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.data.set.list.Lists;
import net.splitcells.website.content.defaults.FileSystem;
import net.splitcells.website.server.Config;
import net.splitcells.website.server.project.ProjectRenderer;
import net.splitcells.website.server.project.validator.SourceValidator;
import net.splitcells.website.server.projects.ProjectsRendererI;

import java.nio.file.Path;

import static net.splitcells.dem.Dem.configValue;
import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.data.set.list.Lists.toList;
import static net.splitcells.dem.utils.ConstructorIllegal.constructorIllegal;
import static net.splitcells.website.server.project.ProjectRenderer.projectRenderer;
import static net.splitcells.website.server.project.validator.SourceValidator.VOID_VALIDATOR;

public class WebsiteViaJar {
    private WebsiteViaJar() {
        throw constructorIllegal();
    }

    public static void main(String... args) {
        projectsRenderer(Config.create()
                .withAdditionalProject(configValue(net.splitcells.dem.FileSystem.class))
                .withAdditionalProject(configValue(net.splitcells.network.FileSystem.class))
                .withAdditionalProject(configValue(net.splitcells.gel.doc.FileSystem.class))
        );
    }

    public static ProjectsRendererI projectsRenderer(Config config) {
        final var profile = "public";
        final var projectsRepository = config.mainProjectRepositoryPath().orElse(Path.of("../"));
        final var validator = VOID_VALIDATOR;
        // TODO config.xmlSchema().map(s -> (SourceValidator) validatorViaSchema(s)).orElse(VOID_VALIDATOR);
        final var additionalProjectRenderers = config.additionalProjects().stream()
                .map(project ->
                        projectRenderer(profile
                                , project
                                , configValue(net.splitcells.website.content.defaults.FileSystem.class)
                                        .subFileSystemView("net.splitcells.website.content.default/src/main/xsl/net/splitcells/website/den/translation/to/html/")
                                , configValue(net.splitcells.website.content.defaults.FileSystem.class)
                                        .subFileSystemView("net.splitcells.website.content.default/src/main/resources/html")
                                , "/"
                                , validator
                                , config))
                .collect(toList());
        return projectsRenderer(projectsRepository
                , profile
                , fallbackProjectRenderer(profile, projectsRepository, validator, config)
                , additionalProjectRenderers
                , validator
                , config);
    }

    public static ProjectsRendererI projectsRenderer(Path projectRepository, String profile
            , ProjectRenderer fallbackProjectRenderer
            , List<ProjectRenderer> additionalProjects
            , SourceValidator sourceValidator
            , Config config) {
        final var xslLib = projectRepository
                .resolve("net.splitcells.website.content.default/src/main/xsl/net/splitcells/website/den/translation/to/html/");
        return ProjectsRendererI.projectsRenderer(profile, fallbackProjectRenderer, additionalProjects
                , config);
    }

    public static ProjectRenderer fallbackProjectRenderer(String profile, Path projectRepositories
            , SourceValidator sourceValidator
            , Config config) {
        return projectRenderer(profile
                , configValue(net.splitcells.website.content.defaults.FileSystem.class)
                , configValue(net.splitcells.website.content.defaults.FileSystem.class)
                        .subFileSystemView("net.splitcells.website.content.default/src/main/xsl/net/splitcells/website/den/translation/to/html/")
                , configValue(net.splitcells.website.content.defaults.FileSystem.class)
                        .subFileSystemView("net.splitcells.website.content.default/src/main/resources/content")
                , "/"
                , sourceValidator
                , config);
    }
}
