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

import net.splitcells.dem.data.set.Set;
import net.splitcells.dem.resource.Trail;
import net.splitcells.gel.data.table.TableModificationCounter;
import net.splitcells.website.server.projects.ProjectsRenderer;
import net.splitcells.website.server.projects.ProjectsRendererI;
import net.splitcells.website.server.projects.RenderRequest;
import net.splitcells.website.server.projects.RenderResponse;
import net.splitcells.website.server.projects.extension.ProjectsRendererExtension;
import net.splitcells.website.server.projects.extension.impls.ProjectPathsRequest;

import java.nio.file.Path;
import java.util.Optional;

import static net.splitcells.dem.Dem.configValue;
import static net.splitcells.dem.data.set.Sets.setOfUniques;
import static net.splitcells.dem.resource.Trail.trail;
import static net.splitcells.website.server.project.LayoutConfig.layoutConfig;
import static net.splitcells.website.server.projects.RenderResponse.renderResponse;
import static net.splitcells.website.server.security.authorization.AdminRole.ADMIN_ROLE;
import static net.splitcells.website.server.security.authorization.Authorization.hasRole;
import static net.splitcells.website.server.security.authorization.Authorization.missesRole;

/**
 * Provides a page, that provides statistics of different counters,
 * in order to indicate the runtime and memory performance of the problem.
 * It is intended to be used for experiments,
 * where the program is started with just the experiment code being active.
 */
public class PerformanceReport implements ProjectsRendererExtension {
    public static ProjectsRendererExtension performanceReport() {
        return new PerformanceReport();
    }

    private static final Trail PATH = trail("net/splitcells/network/system/performance-report.html");

    private PerformanceReport() {

    }

    @Override
    public RenderResponse render(RenderRequest request, ProjectsRenderer projectsRenderer) {
        if (PATH.equals(request.trail())) {
            if (missesRole(request.user(), ADMIN_ROLE)) {
                return projectsRenderer.renderMissingAccessRights(request);
            }
            return renderResponse(projectsRenderer.renderContent(htmlReport()
                    , layoutConfig(PATH.unixPathString())
                            .withTitle("Performance Report")
            ));
        }
        return renderResponse(Optional.empty());
    }

    private String htmlReport() {
        return "<div xmlns=\"http://www.w3.org/1999/xhtml\"><ul>"
                + "<li>Table modification counter sum: "
                + configValue(TableModificationCounter.class).sumCounter().currentCount()
                + "</li>"
                + "</ul></div>";
    }

    @Override
    public boolean requiresAuthentication(RenderRequest request) {
        return request.trail().equalContents(PATH);
    }

    @Override
    public Set<Path> projectPaths(ProjectsRendererI projectsRendererI) {
        return setOfUniques(Path.of(PATH.unixPathString()));
    }

    @Override
    public Set<Path> projectPaths(ProjectPathsRequest request) {
        if (hasRole(request.user(), ADMIN_ROLE)) {
            return setOfUniques(Path.of(PATH.unixPathString()));
        }
        return setOfUniques();
    }
}
