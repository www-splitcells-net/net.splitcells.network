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

import net.splitcells.dem.Dem;
import net.splitcells.dem.data.set.Set;
import net.splitcells.dem.environment.config.framework.Configuration;
import net.splitcells.dem.resource.Trail;
import net.splitcells.website.server.Config;
import net.splitcells.website.server.processor.BinaryMessage;
import net.splitcells.website.server.project.LayoutConfig;
import net.splitcells.website.server.projects.ProjectsRenderer;
import net.splitcells.website.server.projects.ProjectsRendererI;
import net.splitcells.website.server.projects.RenderRequest;
import net.splitcells.website.server.projects.RenderResponse;

import java.nio.file.Path;
import java.util.Optional;

import static java.util.Optional.empty;
import static net.splitcells.dem.Dem.config;
import static net.splitcells.dem.data.set.Sets.setOfUniques;
import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.lang.tree.TreeI.tree;
import static net.splitcells.dem.resource.Trail.trail;
import static net.splitcells.website.server.project.LayoutConfig.layoutConfig;
import static net.splitcells.website.server.projects.RenderResponse.renderResponse;
import static net.splitcells.website.server.security.authorization.AdminRole.ADMIN_ROLE;
import static net.splitcells.website.server.security.authorization.Authorization.hasRole;
import static net.splitcells.website.server.security.authorization.Authorization.missesRole;

/**
 * Renders the {@link Configuration} of the current {@link Dem#config()}.
 */

public class DemConfigExtension implements ProjectsRendererExtension {
    public static ProjectsRendererExtension demConfigExtension() {
        return new DemConfigExtension();
    }

    private static final Trail PATH = trail("net/splitcells/dem/config.html");

    private DemConfigExtension() {

    }

    @Override
    public RenderResponse render(RenderRequest request, ProjectsRenderer projectsRenderer) {
        if (!request.trail().equalContents(PATH)) {
            return renderResponse(empty());
        }
        if (missesRole(request.user(), ADMIN_ROLE)) {
            return projectsRenderer.renderMissingAccessRights(request);
        }
        if (projectsRenderer.config().layout().isPresent()) {
            return renderResponse(projectsRenderer.renderContent(htmlTableOfConfig()
                    , layoutConfig(PATH.unixPathString())));
        }
        return renderResponse(empty());
    }

    private String htmlTableOfConfig() {
        final var tableContent = new StringBuilder();
        final var config = config();
        config.keys().forEach(f -> {
            tableContent.append("<tr><td>" + f.getName() + "</td>");
            tableContent.append("<td>" + config.configValueUntyped(f) + "</td>");
            tableContent.append("<td>" + config.configValueUntyped(f).getClass().getName()
                    + "</td></tr>");
        });
        return "<table xmlns=\"http://www.w3.org/1999/xhtml\"><tr><th>Key</th><th>Value</th><th>Value's Class</th></tr>"
                + tableContent
                + "</table>";
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
