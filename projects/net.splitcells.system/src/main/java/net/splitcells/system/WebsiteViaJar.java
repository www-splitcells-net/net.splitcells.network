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

import net.splitcells.cin.CinFileSystem;
import net.splitcells.dem.DemFileSystem;
import net.splitcells.dem.data.set.list.List;
import net.splitcells.gel.GelCoreFileSystem;
import net.splitcells.gel.doc.GelDocFileSystem;
import net.splitcells.gel.ext.GelExtFileSystem;
import net.splitcells.gel.ui.GelUiFileSystem;
import net.splitcells.gel.ui.SolutionCalculator;
import net.splitcells.network.NetworkFileSystem;
import net.splitcells.network.blog.NetworkBlogFileSystem;
import net.splitcells.network.worker.NetworkWorkerFileSystem;
import net.splitcells.os.state.interfaces.OsiFileSystem;
import net.splitcells.os.state.interfaces.lib.OsiLibFileSystem;
import net.splitcells.project.files.standard.ProjectStandardFileSystem;
import net.splitcells.website.WebsiteServerFileSystem;
import net.splitcells.website.content.defaults.WebsiteContentDefaultsFileSystem;
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
import static net.splitcells.gel.ui.SolutionCalculator.solutionCalculator;
import static net.splitcells.website.server.ProgramConfig.programConfig;
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
                        , configValue(CinFileSystem.class)))
                .withAdditionalProject(projectConfig("/net/splitcells/dem/"
                        , configValue(DemFileSystem.class)))
                .withAdditionalProject(projectConfig("/net/splitcells/dem/"
                        , configValue(net.splitcells.dem.DemApiFileSystem.class)))
                .withAdditionalProject(projectConfig("/net/splitcells/gel/"
                        , configValue(GelDocFileSystem.class)))
                .withAdditionalProject(projectConfig("/net/splitcells/gel/"
                        , configValue(GelCoreFileSystem.class)))
                .withAdditionalProject(projectConfig("/net/splitcells/gel/"
                        , configValue(GelExtFileSystem.class)))
                .withAdditionalProject(projectConfig("/net/splitcells/gel/ui/"
                        , configValue(GelUiFileSystem.class)))
                .withAdditionalProject(projectConfig("/net/splitcells/network/"
                        , configValue(NetworkFileSystem.class)))
                .withAdditionalProject(projectConfig("/net/splitcells/network/blog/"
                        , configValue(NetworkBlogFileSystem.class)))
                .withAdditionalProject(projectConfig("/net/splitcells/network/worker/"
                        , configValue(NetworkWorkerFileSystem.class)))
                .withAdditionalProject(projectConfig("/net/splitcells/os/state/interface/"
                        , configValue(OsiFileSystem.class)))
                .withAdditionalProject(projectConfig("/net/splitcells/os/state/interface/lib/"
                        , configValue(OsiLibFileSystem.class)))
                .withAdditionalProject(projectConfig("/net/splitcells/system/"
                        , configValue(SystemsFileSystem.class)))
                .withAdditionalProject(projectConfig("/net/splitcells/project/files/standard"
                        , configValue(ProjectStandardFileSystem.class)))
                .withAdditionalProject(projectConfig("/"
                        , configValue(WebsiteServerFileSystem.class)))
                .withAdditionalProject(projectConfig("/"
                        , configValue(WebsiteContentDefaultsFileSystem.class)))
                .withDetailedXslMenu(Optional.of(configValue(WebsiteContentDefaultsFileSystem.class)
                        .readString("src/main/xsl/net/splitcells/website/detailed-menu.xsl")))
                .withXslWindowMenu(Optional.of(configValue(WebsiteContentDefaultsFileSystem.class)
                        .readString("src/main/xsl/net/splitcells/website/window-menu.xsl")))
                .withAdditionalJsBackgroundFiles("net/splitcells/website/js/codemirror-editor-bundle.js")
                .withAdditionalJsBackgroundFiles("net/splitcells/website/js/basic.js")
                .withAdditionalJsBackgroundFiles("net/splitcells/website/js/basic.default.js")
                .withAdditionalCssFile("net/splitcells/website/css/theme.white.variables.css")
                .withAdditionalCssFile("net/splitcells/website/css/basic.themed.css")
                .withAdditionalCssFile("net/splitcells/website/css/basic.css")
                .withAdditionalCssFile("net/splitcells/website/css/den.css")
                .withAdditionalCssFile("net/splitcells/website/css/layout.default.css")
                .withAdditionalCssFile("net/splitcells/website/css/theme.css")
                .withAdditionalCssFile("net/splitcells/website/css/tabulator.min.css")
                .withAdditionalProgramConfig(programConfig("Splitcells Network Documentation"
                        , "/net/splitcells/network/README")
                        .withLogoPath(Optional.of("net/splitcells/website/images/thumbnail/medium/community.2016.12.11.chrom.0.dina4.jpg"))
                        .withDescription(Optional.of("We provide an open source ecosystem centered around optimization and operations research.")))
                .withAdditionalProgramConfig(programConfig("Generic Allocation Editor"
                        , "/net/splitcells/gel/ui/editor")
                        .withLogoPath(Optional.of("net/splitcells/website/images/thumbnail/medium/net.splitcells.gel.ui.logo.jpg"))
                        .withDescription(Optional.of("Define and solve assignment problems.")))
                .withAdditionalProcessor(SolutionCalculator.PATH, solutionCalculator());
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
                                , configValue(WebsiteContentDefaultsFileSystem.class)
                                        .subFileSystemView("src/main/xsl/net/splitcells/website/den/translation/to/html/")
                                , configValue(WebsiteContentDefaultsFileSystem.class)
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
                , configValue(WebsiteContentDefaultsFileSystem.class)
                , configValue(WebsiteContentDefaultsFileSystem.class)
                        .subFileSystemView("src/main/xsl/net/splitcells/website/den/translation/to/html/")
                , configValue(WebsiteContentDefaultsFileSystem.class)
                        .subFileSystemView("src/main/resources/html")
                , "/"
                , sourceValidator
                , config);
    }
}
