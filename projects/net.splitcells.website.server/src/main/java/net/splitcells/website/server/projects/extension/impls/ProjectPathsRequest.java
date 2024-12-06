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

import net.splitcells.website.server.projects.ProjectsRenderer;
import net.splitcells.website.server.security.authentication.UserSession;

import static net.splitcells.website.server.security.authentication.UserSession.ANONYMOUS_USER_SESSION;

public class ProjectPathsRequest {
    public static ProjectPathsRequest projectPathsRequest(ProjectsRenderer projectsRenderer) {
        return new ProjectPathsRequest(projectsRenderer);
    }

    private final ProjectsRenderer projectsRenderer;
    private UserSession requestor = ANONYMOUS_USER_SESSION;

    private ProjectPathsRequest(ProjectsRenderer argProjectsRenderer) {
        projectsRenderer = argProjectsRenderer;
    }

    public ProjectsRenderer projectsRenderer() {
        return projectsRenderer;
    }

    /**
     * @return Every requested path has to be explicitly authorized for the returned {@link UserSession}.
     */
    public UserSession user() {
        return requestor;
    }

    public ProjectPathsRequest withUser(UserSession argRequestor) {
        requestor = argRequestor;
        return this;
    }
}
