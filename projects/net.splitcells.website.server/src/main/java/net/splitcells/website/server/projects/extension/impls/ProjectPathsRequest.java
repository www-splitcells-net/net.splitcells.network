/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.website.server.projects.extension.impls;

import net.splitcells.website.server.projects.ProjectsRenderer;
import net.splitcells.website.server.security.authentication.Authentication;
import net.splitcells.website.server.security.authentication.UserSession;

import static net.splitcells.dem.Dem.configValue;
import static net.splitcells.website.server.security.authentication.Authentication.anonymous;

/**
 * An instance to request all website paths via {@link ProjectsRenderer#projectPaths(ProjectPathsRequest)}.
 */
public class ProjectPathsRequest {
    public static ProjectPathsRequest projectPathsRequest(ProjectsRenderer projectsRenderer) {
        return new ProjectPathsRequest(projectsRenderer);
    }

    private final ProjectsRenderer projectsRenderer;
    private UserSession requestor = anonymous();

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
