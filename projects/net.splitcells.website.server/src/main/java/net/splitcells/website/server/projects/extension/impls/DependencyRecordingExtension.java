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

import net.splitcells.dem.data.set.Set;
import net.splitcells.dem.resource.ContentType;
import net.splitcells.dem.resource.Trail;
import net.splitcells.website.server.projects.ProjectsRenderer;
import net.splitcells.website.server.projects.RenderRequest;
import net.splitcells.website.server.projects.RenderResponse;
import net.splitcells.website.server.projects.extension.ProjectsRendererExtension;

import java.nio.file.Path;
import java.util.Optional;

import static net.splitcells.dem.data.set.Sets.setOfUniques;
import static net.splitcells.dem.resource.Trail.trail;
import static net.splitcells.dem.utils.StringUtils.stringBuilder;
import static net.splitcells.website.server.processor.BinaryMessage.binaryMessage;
import static net.splitcells.website.server.projects.RenderResponse.renderResponse;
import static net.splitcells.website.server.security.authentication.UserSession.isValidNoLoginStandard;
import static net.splitcells.website.server.security.authorization.AdminRole.ADMIN_ROLE;
import static net.splitcells.website.server.security.authorization.Authorization.missesRole;

public class DependencyRecordingExtension implements ProjectsRendererExtension {
    public static ProjectsRendererExtension dependencyRecordingExtension() {
        return new DependencyRecordingExtension();
    }

    private static final Trail DEPENDENCY_RECORDING_PATH = trail("net/splitcells/website/server/projects/extension/dependency-recording.html");

    private DependencyRecordingExtension() {
    }

    @Override
    public RenderResponse render(RenderRequest request, ProjectsRenderer projectsRenderer) {
        if (!request.trail().equalContents(DEPENDENCY_RECORDING_PATH)) {
            return renderResponse(Optional.empty());
        }
        if (isValidNoLoginStandard(request.user())) {
            return projectsRenderer.renderMissingLogin(request);
        } else if (missesRole(request.user(), ADMIN_ROLE)) {
            return projectsRenderer.renderMissingAccessRights(request);
        } else {
            final var content = stringBuilder();
            content.append("<div align=\"center\"><code class=\"mermaid\">\ngraph TD\n");
            content.append("</code></div><script src=\"https://cdn.jsdelivr.net/npm/mermaid/dist/mermaid.min.js\"></script>");
            return renderResponse(Optional.of(binaryMessage(projectsRenderer.renderHtmlBodyContent(content.toString()
                                    , Optional.of("Dependency Recording")
                                    , Optional.of(DEPENDENCY_RECORDING_PATH.unixPathString())
                                    , projectsRenderer.config())
                            .orElseThrow()
                    , ContentType.HTML_TEXT.codeName())));
        }
    }

    @Override
    public boolean requiresAuthentication(RenderRequest request) {
        return request.trail().equalContents(DEPENDENCY_RECORDING_PATH);
    }

    @Override
    public Set<Path> projectPaths(ProjectPathsRequest request) {
        return setOfUniques(Path.of(DEPENDENCY_RECORDING_PATH.unixPathString()));
    }
}
