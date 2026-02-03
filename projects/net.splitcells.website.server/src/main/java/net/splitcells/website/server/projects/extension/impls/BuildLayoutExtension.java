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
package net.splitcells.website.server.projects.extension.impls;

import net.splitcells.dem.resource.Trail;
import net.splitcells.website.server.projects.ProjectsRenderer;
import net.splitcells.website.server.projects.RenderRequest;
import net.splitcells.website.server.projects.RenderResponse;
import net.splitcells.website.server.projects.extension.ProjectsRendererExtension;

import java.util.Optional;

import static net.splitcells.dem.resource.Trail.trail;
import static net.splitcells.website.server.projects.RenderResponse.renderResponse;

/**
 * Rebuilds the file layout of the webserver.
 * This is the list of files, when all pages of the webserver are rendered.
 */
public class BuildLayoutExtension implements ProjectsRendererExtension {
    private static final Trail PATH = trail("net/splitcells/website/layout/build.html");

    public static BuildLayoutExtension buildLayoutExtension() {
        return new BuildLayoutExtension();
    }

    private BuildLayoutExtension() {

    }

    @Override public RenderResponse render(RenderRequest request, ProjectsRenderer projectsRenderer) {
        if (!request.trail().equalContents(PATH)) {
            return renderResponse(Optional.empty());
        }
        projectsRenderer.build();
        return renderResponse(projectsRenderer.render("/"));
    }

    @Override public boolean requiresAuthentication(RenderRequest request) {
        return request.trail().equalContents(PATH);
    }
}
