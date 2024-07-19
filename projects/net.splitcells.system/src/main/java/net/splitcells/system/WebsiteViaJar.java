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
import net.splitcells.dem.Dem;
import net.splitcells.dem.DemFileSystem;
import net.splitcells.dem.data.set.list.List;
import net.splitcells.gel.GelCoreFileSystem;
import net.splitcells.gel.doc.GelDocFileSystem;
import net.splitcells.gel.ext.GelExtFileSystem;
import net.splitcells.gel.ui.GelUiFileSystem;
import net.splitcells.gel.ui.no.code.editor.NoCodeSolutionCalculator;
import net.splitcells.gel.ui.SolutionCalculator;
import net.splitcells.network.NetworkFileSystem;
import net.splitcells.network.worker.via.java.NetworkWorkerFileSystem;
import net.splitcells.os.state.interfaces.OsiFileSystem;
import net.splitcells.os.state.interfaces.lib.OsiLibFileSystem;
import net.splitcells.project.files.standard.ProjectStandardFileSystem;
import net.splitcells.website.WebsiteServerFileSystem;
import net.splitcells.website.content.defaults.WebsiteContentDefaultsFileSystem;
import net.splitcells.website.server.Config;
import net.splitcells.website.server.ServerService;
import net.splitcells.website.server.project.ProjectRenderer;
import net.splitcells.website.server.project.validator.SourceValidator;
import net.splitcells.website.server.projects.ProjectsRenderer;
import net.splitcells.website.server.projects.ProjectsRendererI;

import java.util.Optional;
import java.util.function.Function;

import static net.splitcells.dem.Dem.configValue;
import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.utils.ConstructorIllegal.constructorIllegal;
import static net.splitcells.gel.ui.no.code.editor.FunctionMeta.functionMeta;
import static net.splitcells.gel.ui.no.code.editor.Functions.functions;
import static net.splitcells.gel.ui.no.code.editor.NoCodeSolutionCalculator.noCodeSolutionCalculator;
import static net.splitcells.gel.ui.SolutionCalculator.solutionCalculator;
import static net.splitcells.gel.ui.no.code.editor.TopLevelFunctions.topLevelFunctions;
import static net.splitcells.website.server.ProgramConfig.programConfig;
import static net.splitcells.website.server.ProjectConfig.projectConfig;
import static net.splitcells.website.server.project.ProjectRenderer.projectRenderer;

public class WebsiteViaJar {
    private WebsiteViaJar() {
        throw constructorIllegal();
    }

    /**
     * @return
     * @deprecated TODO This method is used for versions, where the server is not managed by {@link Dem}.
     * Remove the callers and after that remove this method.
     */
    @Deprecated
    public static Config config() {
        return config(Config.create());
    }

    /**
     * @param arg
     * @return
     * @deprecated TODO Move this method into {@link SystemCell}.
     */
    @Deprecated
    public static Config config(Config arg) {
        return arg
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
                .withAdditionalProject(projectConfig("/net/splitcells/network/worker/via/java/"
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
                .withAdditionalJsBackgroundFiles("net/splitcells/website/js/jquery.js")
                .withAdditionalJsBackgroundFiles("net/splitcells/website/js/codemirror-editor-bundle.js")
                .withAdditionalJsBackgroundFiles("net/splitcells/website/js/basic.js")
                .withAdditionalJsBackgroundFiles("net/splitcells/website/js/basic.default.js")
                .withAdditionalJsBackgroundFiles("net/splitcells/website/js/dragula.min.js")
                .withAdditionalCssFile("net/splitcells/website/css/theme.white.variables.css")
                .withAdditionalCssFile("net/splitcells/website/css/basic.themed.css")
                .withAdditionalCssFile("net/splitcells/website/css/basic.css")
                .withAdditionalCssFile("net/splitcells/website/css/den.css")
                .withAdditionalCssFile("net/splitcells/website/css/layout.default.css")
                .withAdditionalCssFile("net/splitcells/website/css/theme.css")
                .withAdditionalCssFile("net/splitcells/gel/ui/no/code/editor/style.css")
                .withAdditionalCssFile("net/splitcells/website/css/tabulator.min.css")
                .withAdditionalCssFile("net/splitcells/website/css/dragula.min.css")
                .withAdditionalProgramConfig(programConfig("Splitcells Network Documentation"
                        , "/net/splitcells/network/hub/README")
                        .withLogoPath(Optional.of("net/splitcells/website/images/thumbnail/medium/community.2016.12.11.chrom.0.dina4.jpg"))
                        .withDescription(Optional.of("We provide an open source ecosystem centered around optimization and operations research.")))
                .withAdditionalProgramConfig(programConfig("Generic Allocation Editor"
                        , "/net/splitcells/gel/ui/editor")
                        .withLogoPath(Optional.of("net/splitcells/website/images/thumbnail/medium/net.splitcells.gel.ui.logo.jpg"))
                        .withDescription(Optional.of("Define and solve assignment problems.")))
                .withAdditionalProcessor(SolutionCalculator.PATH, solutionCalculator())
                .withAdditionalProcessor(NoCodeSolutionCalculator.PATH, noCodeSolutionCalculator())
                .withAdditionalProjectsRendererExtension(topLevelFunctions())
                .withAdditionalProjectsRendererExtension(functions())
                .withAdditionalProjectsRendererExtension(functionMeta())
                ;
    }

    /**
     * Use {@link ServerService#projectsRenderer(Config)} instead.
     *
     * @param config
     * @return
     */
    @Deprecated
    public static ProjectsRendererI projectsRenderer(Config config) {
        return ServerService.projectsRenderer(config);
    }

    /**
     * Use {@link ServerService#projectsRenderer(String, Function, Function, Config)} instead.
     *
     * @param profile
     * @param fallbackProjectRenderer
     * @param additionalProjects
     * @param sourceValidator
     * @param config
     * @return
     */
    @Deprecated
    public static ProjectsRendererI projectsRenderer(String profile
            , Function<ProjectsRenderer, ProjectRenderer> fallbackProjectRenderer
            , Function<ProjectsRenderer, List<ProjectRenderer>> additionalProjects
            , SourceValidator sourceValidator
            , Config config) {
        return ServerService.projectsRenderer(profile, fallbackProjectRenderer, additionalProjects, config);
    }

    /**
     * Use {@link ServerService#fallbackProjectRenderer(ProjectsRenderer, String, SourceValidator, Config)} instead.
     *
     * @param projectsRenderer
     * @param profile
     * @param sourceValidator
     * @param config
     * @return
     */
    @Deprecated
    public static ProjectRenderer fallbackProjectRenderer(ProjectsRenderer projectsRenderer, String profile
            , SourceValidator sourceValidator
            , Config config) {
        return ServerService.fallbackProjectRenderer(projectsRenderer, profile, sourceValidator, config);
    }
}
