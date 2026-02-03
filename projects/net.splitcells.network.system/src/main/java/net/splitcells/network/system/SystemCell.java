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
package net.splitcells.network.system;

import net.splitcells.cin.CinFileSystem;
import net.splitcells.dem.Dem;
import net.splitcells.dem.DemFileSystem;
import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.environment.Cell;
import net.splitcells.dem.environment.Environment;
import net.splitcells.gel.GelCoreFileSystem;
import net.splitcells.gel.doc.GelDocFileSystem;
import net.splitcells.gel.editor.GelEditorFileSystem;
import net.splitcells.gel.ext.GelExtFileSystem;
import net.splitcells.gel.ui.GelUiCell;
import net.splitcells.gel.ui.GelUiFileSystem;
import net.splitcells.gel.ui.editor.geal.EditorProcessor;
import net.splitcells.network.NetworkFileSystem;
import net.splitcells.network.worker.via.java.NetworkWorkerFileSystem;
import net.splitcells.project.ProjectFileSystem;
import net.splitcells.shell.OsiFileSystem;
import net.splitcells.shell.lib.OsiLibFileSystem;
import net.splitcells.website.WebsiteServerFileSystem;
import net.splitcells.website.content.defaults.WebsiteContentDefaultsFileSystem;
import net.splitcells.website.server.Config;
import net.splitcells.website.server.ServerConfig;
import net.splitcells.website.server.ServerService;
import net.splitcells.website.server.WebsiteServerCell;
import net.splitcells.website.server.project.ProjectRenderer;
import net.splitcells.website.server.project.validator.SourceValidator;
import net.splitcells.website.server.projects.ProjectsRenderer;
import net.splitcells.website.server.projects.ProjectsRendererI;
import net.splitcells.website.server.test.HtmlLiveTest;

import java.util.Optional;
import java.util.function.Function;

import static net.splitcells.dem.Dem.configValue;
import static net.splitcells.dem.Dem.serve;
import static net.splitcells.dem.lang.TrailLink.trailLink;
import static net.splitcells.dem.resource.Trail.trail;
import static net.splitcells.gel.editor.geal.FunctionCallDoc.functionCallDoc;
import static net.splitcells.gel.ui.editor.geal.example.ColloquiumExample.colloquiumExample;
import static net.splitcells.gel.ui.editor.geal.EditorProcessor.editorProcessor;
import static net.splitcells.gel.ui.editor.geal.EditorProcessorTest.TEST_OPTIMIZATION_GUI;
import static net.splitcells.gel.ui.editor.geal.example.SportCourseExample.sportCourseExample;
import static net.splitcells.network.system.PerformanceReport.performanceReport;
import static net.splitcells.website.server.ProgramConfig.programConfig;
import static net.splitcells.website.server.ProjectConfig.projectConfig;
import static net.splitcells.website.server.WebsiteServerCell.configureNoneCellInit;

public class SystemCell implements Cell {

    public static void main(String... args) {
        serve(SystemCell.class);
    }


    @Override
    public String groupId() {
        return "net.splitcells";
    }

    @Override
    public String artifactId() {
        return "system";
    }

    @Override
    public void accept(Environment env) {
        env.config()
                .withConfigValue(ServerConfig.class, config(env.config().configValue(ServerConfig.class)))
                .withConfigValue(HtmlLiveTest.class, TEST_OPTIMIZATION_GUI)
        ;
        env.withCell(WebsiteServerCell.class).withCell(GelUiCell.class);
    }

    @Deprecated
    public Config config2() {
        return config();
    }

    @Deprecated
    public Config config2(Config arg) {
        return config(arg);
    }

    public ProjectsRendererI projectsRenderer2(Config config) {
        return projectsRenderer(config);
    }

    @Deprecated
    public ProjectsRendererI projectsRenderer2(String profile
            , Function<ProjectsRenderer, ProjectRenderer> fallbackProjectRenderer
            , Function<ProjectsRenderer, List<ProjectRenderer>> additionalProjects
            , SourceValidator sourceValidator
            , Config config) {
        return projectsRenderer(profile, fallbackProjectRenderer, additionalProjects, sourceValidator, config);
    }

    @Deprecated
    public ProjectRenderer fallbackProjectRenderer2(ProjectsRenderer projectsRenderer, String profile
            , SourceValidator sourceValidator
            , Config config) {
        return fallbackProjectRenderer(projectsRenderer, profile, sourceValidator, config);
    }

    /**
     * @return
     * @deprecated TODO This method is used for versions, where the server is not managed by {@link Dem}.
     * Remove the callers and after that remove this method.
     */
    @Deprecated
    public static Config config() {
        return config(configValue(ServerConfig.class));
    }

    /**
     * <p>TODO This seems to be partially a duplicate of {@link WebsiteServerCell#accept(Environment)}.</p>
     *
     * @param arg
     * @return
     * @deprecated TODO Move this method into {@link SystemCell}.
     */
    @Deprecated
    public static Config config(Config arg) {
        configureNoneCellInit(arg);
        return arg
                .withAdditionalProject(projectConfig("/net/splitcells/cin/"
                        , configValue(CinFileSystem.class)))
                .withAdditionalProject(projectConfig("/net/splitcells/dem/"
                        , configValue(DemFileSystem.class)))
                .withAdditionalProject(projectConfig("/net/splitcells/dem/"
                        , configValue(net.splitcells.dem.DemApiFileSystem.class)))
                .withAdditionalProject(projectConfig("/net/splitcells/gel/doc/"
                        , configValue(GelDocFileSystem.class)))
                .withAdditionalProject(projectConfig("/net/splitcells/gel/"
                        , configValue(GelCoreFileSystem.class)))
                .withAdditionalProject(projectConfig("/net/splitcells/gel/ext/"
                        , configValue(GelExtFileSystem.class)))
                .withAdditionalProject(projectConfig("/net/splitcells/gel/editor/"
                        , configValue(GelEditorFileSystem.class)))
                .withAdditionalProject(projectConfig("/net/splitcells/gel/ui/"
                        , configValue(GelUiFileSystem.class)))
                .withAdditionalProject(projectConfig("/net/splitcells/network/"
                        , configValue(NetworkFileSystem.class)))
                .withAdditionalProject(projectConfig("/net/splitcells/network/worker/via/java/"
                        , configValue(NetworkWorkerFileSystem.class)))
                .withAdditionalProject(projectConfig("/net/splitcells/shell/"
                        , configValue(OsiFileSystem.class)))
                .withAdditionalProject(projectConfig("/net/splitcells/shell/lib/"
                        , configValue(OsiLibFileSystem.class)))
                .withAdditionalProject(projectConfig("/net/splitcells/network/system/"
                        , configValue(SystemsFileSystem.class)))
                .withAdditionalProject(projectConfig("/net/splitcells/project/"
                        , configValue(ProjectFileSystem.class)))
                .withDetailedXslMenu(Optional.of(configValue(WebsiteContentDefaultsFileSystem.class)
                        .readString("src/main/xsl/net/splitcells/website/detailed-menu.xsl")))
                .withXslWindowMenu(Optional.of(configValue(WebsiteContentDefaultsFileSystem.class)
                        .readString("src/main/xsl/net/splitcells/website/window-menu.xsl")))
                .withAdditionalJsBackgroundFiles("net/splitcells/website/js/dragula.min.js")
                .withAdditionalJsBackgroundFiles("net/splitcells/website/js/codemirror-editor-bundle.js")
                .withAdditionalCssFile("net/splitcells/website/css/tabulator.min.css")
                .withAdditionalCssFile("net/splitcells/website/css/dragula.min.css")
                .withAdditionalCssFile("net/splitcells/website/css/highlight.css")
                .withAdditionalProgramConfig(programConfig("Splitcells Network Documentation"
                        , "/net/splitcells/network/hub/README")
                        .withLogoPath(Optional.of("net/splitcells/website/images/thumbnail/medium/community.2016.12.11.chrom.0.dina4.jpg"))
                        .withDescription(Optional.of("We provide an open source ecosystem centered around optimization and operations research.")))
                .withAdditionalProgramConfig(programConfig("News"
                        , "/net/splitcells/website/news")
                        .withLogoPath(Optional.of("net/splitcells/website/images/average.source.code.image.generator.filling.via.horizontal.100.percent.jpg"))
                        .withDescription(Optional.of("All User and Developer Relevant News in One Place at Your Finger Tips")))
                .withAdditionalProgramConfig(programConfig("Generic Allocation Editor's Tough Love Edition"
                        , "/net/splitcells/gel/ui/editor/geal/index")
                        .withLogoPath(Optional.of("net/splitcells/website/images/thumbnail/medium/net.splitcells.gel.ui.logo.jpg"))
                        .withDescription(Optional.of("Define and solve assignment problems in text form.")))
                .withAdditionalProcessor(EditorProcessor.PATH, editorProcessor())
                .withAdditionalProjectsRendererExtension(performanceReport())
                .withAdditionalProjectsRendererExtension(functionCallDoc())
                .withAdditionalProjectsRendererExtension(colloquiumExample())
                .withAdditionalProjectsRendererExtension(sportCourseExample())
                .withLicensePage(trailLink("Licensing Info of The Core Project net.splitcells.network", "/net/splitcells/network/LICENSE.html"))
                .withLicensePage(trailLink("Copyright Notice", "/net/splitcells/network/NOTICE.html"))
                .withLicensePage(trailLink("Developer Certificate of Origin Version 1.1", "/net/splitcells/network/legal/Developer_Certificate_of_Origin.v1.1.html"))
                .withLicensePage(trailLink("Apache License - Version 2.0, January 2004", "/net/splitcells/network/legal/licenses/Apache-2.0.html"))
                .withLicensePage(trailLink("BSD 2 Clause License", "/net/splitcells/network/legal/licenses/BSD-2-Clause.html"))
                .withLicensePage(trailLink("BSD 3 Clause License", "/net/splitcells/network/legal/licenses/BSD-3-Clause.html"))
                .withLicensePage(trailLink("Attribution-ShareAlike 4.0 International License", "/net/splitcells/network/legal/licenses/CC-BY-SA-4.0.html"))
                .withLicensePage(trailLink("Eclipse Public License - v 2.0", "/net/splitcells/network/legal/licenses/EPL-2.0.html"))
                .withLicensePage(trailLink("GNU GENERAL PUBLIC LICENSE - Version 2, June 1991 with Classpath Exception", "/net/splitcells/network/legal/licenses/GPL-2.0-or-later-WITH-Classpath-exception-2.0.html"))
                .withLicensePage(trailLink("GNU GENERAL PUBLIC LICENSE - Version 2, June 1991", "/net/splitcells/network/legal/licenses/GPL-2.0-or-later.html"))
                .withLicensePage(trailLink("ISC License", "/net/splitcells/network/legal/licenses/ISC.html"))
                .withLicensePage(trailLink("GNU LESSER GENERAL PUBLIC LICENSE - Version 2.1, February 1999", "/net/splitcells/network/legal/licenses/LGPL-2.1-or-later.html"))
                .withLicensePage(trailLink("MIT License", "/net/splitcells/network/legal/licenses/MIT.html"))
                .withLicensePage(trailLink("Mozilla Public License Version 2.0", "/net/splitcells/network/legal/licenses/MPL-2.0.html"))
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
