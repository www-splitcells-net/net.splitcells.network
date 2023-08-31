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
import net.splitcells.gel.ext.FileSystemExt;
import net.splitcells.website.server.Config;
import net.splitcells.website.server.project.ProjectRenderer;
import net.splitcells.website.server.project.validator.SourceValidator;
import net.splitcells.website.server.projects.ProjectsRendererI;

import java.nio.file.Path;
import java.util.Optional;

import static net.splitcells.dem.Dem.configValue;
import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.data.set.list.Lists.toList;
import static net.splitcells.dem.utils.ConstructorIllegal.constructorIllegal;
import static net.splitcells.website.server.ProjectConfig.projectConfig;
import static net.splitcells.website.server.project.ProjectRenderer.projectRenderer;
import static net.splitcells.website.server.project.validator.SourceValidator.VOID_VALIDATOR;

public class WebsiteViaJar {
    private WebsiteViaJar() {
        throw constructorIllegal();
    }

    public static Config config() {
        return Config.create()
                .withAdditionalProject(projectConfig("/net/splitcells/cin/"
                        , configValue(net.splitcells.cin.FileSystem.class)))
                .withAdditionalProject(projectConfig("/net/splitcells/dem/"
                        , configValue(net.splitcells.dem.FileSystem.class)))
                .withAdditionalProject(projectConfig("/net/splitcells/dem/"
                        , configValue(net.splitcells.dem.ApiFileSystem.class)))
                .withAdditionalProject(projectConfig("/net/splitcells/gel/"
                        , configValue(net.splitcells.gel.doc.FileSystem.class)))
                .withAdditionalProject(projectConfig("/net/splitcells/gel/"
                        , configValue(net.splitcells.gel.FileSystem.class)))
                .withAdditionalProject(projectConfig("/net/splitcells/gel/"
                        , configValue(FileSystemExt.class)))
                .withAdditionalProject(projectConfig("/net/splitcells/network/"
                        , configValue(net.splitcells.network.FileSystem.class)))
                .withAdditionalProject(projectConfig("/net/splitcells/network/worker/"
                        , configValue(net.splitcells.network.worker.FileSystem.class)))
                .withAdditionalProject(projectConfig("/net/splitcells/os/state/interface/"
                        , configValue(net.splitcells.os.state.interfaces.FileSystem.class)))
                .withAdditionalProject(projectConfig("/net/splitcells/os/state/interface/lib/"
                        , configValue(net.splitcells.os.state.interfaces.lib.FileSystem.class)))
                .withAdditionalProject(projectConfig("/net/splitcells/system/"
                        , configValue(net.splitcells.system.FileSystem.class)))
                .withAdditionalProject(projectConfig("/"
                        , configValue(net.splitcells.website.FileSystem.class)))
                .withAdditionalProject(projectConfig("/"
                        , configValue(net.splitcells.website.content.defaults.FileSystem.class)))
                .withDetailedXslMenu(Optional.of(configValue(net.splitcells.website.content.defaults.FileSystem.class)
                        .readString("src/main/xsl/net/splitcells/website/detailed-menu.xsl")))
                .withAdditionalJsBackgroundFiles("net/splitcells/website/js/basic.js")
                .withAdditionalJsBackgroundFiles("net/splitcells/website/js/basic.default.js")
                .withAdditionalCssFile("net/splitcells/website/css/theme.white.variables.css")
                .withAdditionalCssFile("net/splitcells/website/css/basic.themed.css")
                .withAdditionalCssFile("net/splitcells/website/css/basic.css")
                .withAdditionalCssFile("net/splitcells/website/css/den.css")
                .withAdditionalCssFile("net/splitcells/website/css/layout.default.css")
                .withAdditionalCssFile("net/splitcells/website/css/theme.css");
    }

    public static ProjectsRendererI projectsRenderer(Config config) {
        final var profile = "public";
        final var projectsRepository = config.mainProjectRepositoryPath().orElse(Path.of("../"));
        final var validator = VOID_VALIDATOR;
        // TODO config.xmlSchema().map(s -> (SourceValidator) validatorViaSchema(s)).orElse(VOID_VALIDATOR);
        final var additionalProjectRenderers = config.additionalProjects().stream()
                .map(project ->
                        projectRenderer(profile
                                , project.projectFiles()
                                , configValue(net.splitcells.website.content.defaults.FileSystem.class)
                                        .subFileSystemView("src/main/xsl/net/splitcells/website/den/translation/to/html/")
                                , configValue(net.splitcells.website.content.defaults.FileSystem.class)
                                        .subFileSystemView("src/main/resources/html")
                                , project.rootPath()
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
        return ProjectsRendererI.projectsRenderer(profile, fallbackProjectRenderer, additionalProjects, config);
    }

    public static ProjectRenderer fallbackProjectRenderer(String profile, Path projectRepositories
            , SourceValidator sourceValidator
            , Config config) {
        return projectRenderer(profile
                , configValue(net.splitcells.website.content.defaults.FileSystem.class)
                , configValue(net.splitcells.website.content.defaults.FileSystem.class)
                        .subFileSystemView("src/main/xsl/net/splitcells/website/den/translation/to/html/")
                , configValue(net.splitcells.website.content.defaults.FileSystem.class)
                        .subFileSystemView("src/main/resources/content")
                , "/"
                , sourceValidator
                , config);
    }
}
