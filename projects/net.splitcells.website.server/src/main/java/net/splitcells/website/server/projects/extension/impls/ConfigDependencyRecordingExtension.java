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
import net.splitcells.dem.environment.config.framework.ConfigDependencyRecording;
import net.splitcells.dem.environment.config.framework.Option;
import net.splitcells.dem.resource.ContentType;
import net.splitcells.dem.resource.Trail;
import net.splitcells.website.server.projects.ProjectsRenderer;
import net.splitcells.website.server.projects.RenderRequest;
import net.splitcells.website.server.projects.RenderResponse;
import net.splitcells.website.server.projects.extension.ProjectsRendererExtension;

import java.nio.file.Path;
import java.util.Optional;

import static net.splitcells.dem.Dem.configValue;
import static net.splitcells.dem.data.set.Sets.setOfUniques;
import static net.splitcells.dem.resource.Trail.trail;
import static net.splitcells.dem.utils.StringUtils.stringBuilder;
import static net.splitcells.website.server.processor.BinaryMessage.binaryMessage;
import static net.splitcells.website.server.projects.RenderResponse.renderResponse;
import static net.splitcells.website.server.security.authentication.UserSession.isValidNoLoginStandard;
import static net.splitcells.website.server.security.authorization.AdminRole.ADMIN_ROLE;
import static net.splitcells.website.server.security.authorization.Authorization.missesRole;

public class ConfigDependencyRecordingExtension implements ProjectsRendererExtension {
    public static ProjectsRendererExtension configDependencyRecordingExtension() {
        return new ConfigDependencyRecordingExtension();
    }

    private static final Trail DEPENDENCY_RECORDING_PATH = trail("net/splitcells/website/server/projects/extension/config-dependency-recording.html");

    private ConfigDependencyRecordingExtension() {
    }

    /**
     * On the website the horizontal space is limited, whereas the vertical space is practically unlimited.
     * Drawing the Mermaid flow chart from top to bottom makes the flowchart unreadable,
     * as there are a lot of dependencies between {@link Option}, that are thereby rendered in a long row,
     * making the chart's elements small and thereby unreadable.
     * On the other hand, the maximal length of transient dependencies is assumed to be very limited in practice.
     * Therefore, the Mermaid flow chart is drawn from left to right (graph LR),
     * so that the horizontal space is used for transient dependencies and
     * the vertical space is used to list the different dependencies.
     *
     * @param request
     * @param projectsRenderer
     * @return
     */
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
            content.append("<div align=\"center\"><code class=\"mermaid\">\ngraph LR\n");
            configValue(ConfigDependencyRecording.class).dependencies().forEach((from, to) ->
                    to.forEach(target -> {
                        content.append(from.getName());
                        content.append(" --> ");
                        content.append(target.getName());
                        content.append("\n");
                    }));
            content.append("</code></div><script src=\"/net/splitcells/website/js/mermaid.min.js\"></script>");
            return renderResponse(Optional.of(binaryMessage(projectsRenderer.renderHtmlBodyContent(content.toString()
                                    , Optional.of("Config Dependency Recording")
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
