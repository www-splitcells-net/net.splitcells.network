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
import net.splitcells.dem.resource.Trail;
import net.splitcells.dem.testing.Test;
import net.splitcells.website.server.Config;
import net.splitcells.website.server.project.LayoutConfig;
import net.splitcells.website.server.processor.BinaryMessage;
import net.splitcells.website.server.projects.ProjectsRenderer;
import net.splitcells.website.server.projects.ProjectsRendererI;
import net.splitcells.website.server.projects.RenderRequest;
import net.splitcells.website.server.projects.RenderResponse;

import java.nio.file.Path;
import java.util.Optional;

import static java.util.Optional.empty;
import static net.splitcells.dem.data.set.Sets.setOfUniques;
import static net.splitcells.dem.resource.Trail.trail;
import static net.splitcells.dem.resource.communication.log.Logs.logs;
import static net.splitcells.website.server.project.LayoutConfig.layoutConfig;
import static net.splitcells.website.server.projects.RenderResponse.renderResponse;
import static net.splitcells.website.server.security.authorization.AdminRole.ADMIN_ROLE;
import static net.splitcells.website.server.security.authorization.Authorization.hasRole;
import static net.splitcells.website.server.security.authorization.Authorization.missesRole;

public class TestExtension implements ProjectsRendererExtension {
    public static TestExtension testExtension() {
        return new TestExtension();
    }

    private static final Trail PATH = trail("net/splitcells/website/test.html");

    private TestExtension() {

    }

    @Override
    public RenderResponse render(RenderRequest request, ProjectsRenderer projectsRenderer) {
        if (!request.trail().equalContents(PATH)) {
            return renderResponse(empty());
        }
        if (missesRole(request.user(), ADMIN_ROLE)) {
            return projectsRenderer.renderMissingAccessRights(request);
        }
        if ("true".equals(System.getProperty("net.splitcells.mode.build"))) {
            return renderResponse(projectsRenderer.renderContent
                    ("<p xmlns=\"http://www.w3.org/1999/xhtml\">Tests are disabled in test mode,"
                                    + " because tests are currently already running."
                                    + "Otherwise, there would an endless test recursion.</p>"
                            , layoutConfig(PATH.unixPathString())));
        }
        if (projectsRenderer.config().isRenderingStaticWebsite()) {
            return renderResponse(projectsRenderer.renderContent("<p xmlns=\"http://www.w3.org/1999/xhtml\">Tests are disabled on this static website version.</p>"
                    , layoutConfig(PATH.unixPathString())));
        }
        try {
            if (Test.testUnits()) {
                return renderResponse(projectsRenderer.renderContent("<p xmlns=\"http://www.w3.org/1999/xhtml\">Tests executed successfully. System is functional.</p>"
                        , layoutConfig(PATH.unixPathString())));
            }
            return renderResponse(errorReport(projectsRenderer));
        } catch (Throwable th) {
            logs().appendError(th);
            return renderResponse(errorReport(projectsRenderer));
        }
    }

    private Optional<BinaryMessage> errorReport(ProjectsRenderer projectsRenderer) {
        return projectsRenderer.renderContent("<p xmlns=\"http://www.w3.org/1999/xhtml\">Tests executed erroneously. System is dysfunctional.</p>"
                , layoutConfig(PATH.unixPathString()));
    }


    @Override
    public Set<Path> projectPaths(ProjectsRendererI projectsRendererI) {
        if (projectsRendererI.config().layout().isPresent()) {
            return setOfUniques(Path.of(PATH.unixPathString()));
        }
        return setOfUniques();
    }

    @Override
    public boolean requiresAuthentication(RenderRequest request) {
        return request.trail().equalContents(PATH);
    }

    @Override
    public Set<Path> projectPaths(ProjectPathsRequest request) {
        if (hasRole(request.user(), ADMIN_ROLE)) {
            return setOfUniques(Path.of(PATH.unixPathString()));
        }
        return setOfUniques();
    }
}
