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
package net.splitcells.website.server.projects.extension;

import net.splitcells.dem.data.set.Set;
import net.splitcells.dem.testing.Test;
import net.splitcells.website.server.Config;
import net.splitcells.website.server.project.LayoutConfig;
import net.splitcells.website.server.processor.BinaryMessage;
import net.splitcells.website.server.projects.ProjectsRendererI;
import net.splitcells.website.server.projects.RenderRequest;

import java.nio.file.Path;
import java.util.Optional;

import static net.splitcells.dem.data.set.Sets.setOfUniques;
import static net.splitcells.dem.resource.communication.log.Logs.logs;

public class TestExtension implements ProjectsRendererExtension {
    public static TestExtension testExtension() {
        return new TestExtension();
    }

    private static final String PATH = "/net/splitcells/website/test.html";

    private TestExtension() {

    }

    @Override
    public Optional<BinaryMessage> renderFile(String path, ProjectsRendererI projectsRenderer, Config config) {
        if (PATH.equals(path)) {
            if ("true".equals(System.getProperty("net.splitcells.mode.build"))) {
                return projectsRenderer.renderContent
                        ("<p xmlns=\"http://www.w3.org/1999/xhtml\">Tests are disabled in test mode,"
                                        + " because tests are currently already running."
                                        + "Otherwise, there would an endless test recursion.</p>"
                                , LayoutConfig.layoutConfig(PATH));
            }
            if (config.isRenderingStaticWebsite()) {
                return projectsRenderer.renderContent
                        ("<p xmlns=\"http://www.w3.org/1999/xhtml\">Tests are disabled on this static website version.</p>"
                                , LayoutConfig.layoutConfig(PATH));
            }
            try {
                if (Test.testUnits()) {
                    return projectsRenderer.renderContent
                            ("<p xmlns=\"http://www.w3.org/1999/xhtml\">Tests executed successfully. System is functional.</p>"
                                    , LayoutConfig.layoutConfig(PATH));
                }
                return errorReport(projectsRenderer);
            } catch (Throwable th) {
                logs().appendError(th);
                return errorReport(projectsRenderer);
            }
        }
        return Optional.empty();
    }

    @Override
    public boolean requiresAuthentication(RenderRequest request) {
        return false;
    }

    private Optional<BinaryMessage> errorReport(ProjectsRendererI projectsRenderer) {
        return projectsRenderer.renderContent
                ("<p xmlns=\"http://www.w3.org/1999/xhtml\">Tests executed erroneously. System is dysfunctional.</p>"
                        , LayoutConfig.layoutConfig(PATH));
    }


    @Override
    public Set<Path> projectPaths(ProjectsRendererI projectsRendererI) {
        if (projectsRendererI.config().layout().isPresent()) {
            return setOfUniques(Path.of(PATH.substring(1)));
        }
        return setOfUniques();
    }
}
