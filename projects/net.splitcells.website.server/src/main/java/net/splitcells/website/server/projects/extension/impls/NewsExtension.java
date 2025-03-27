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
import net.splitcells.dem.resource.Trail;
import net.splitcells.website.server.project.renderer.extension.commonmark.CommonMarkChangelogEventProjectRendererExtension;
import net.splitcells.website.server.projects.ProjectsRenderer;
import net.splitcells.website.server.projects.ProjectsRendererI;
import net.splitcells.website.server.projects.RenderRequest;
import net.splitcells.website.server.projects.RenderResponse;
import net.splitcells.website.server.projects.extension.ProjectsRendererExtension;

import java.nio.file.Path;
import java.util.Optional;

import static net.splitcells.dem.data.set.Sets.setOfUniques;
import static net.splitcells.dem.resource.Trail.trail;
import static net.splitcells.website.server.notify.NotificationQueueParser.*;
import static net.splitcells.website.server.project.LayoutConfig.layoutConfig;
import static net.splitcells.website.server.project.renderer.extension.commonmark.CommonMarkChangelogEventProjectRendererExtension.commonMarkChangelogEventRenderer;
import static net.splitcells.website.server.projects.RenderResponse.renderResponse;

public class NewsExtension implements ProjectsRendererExtension {
    public static NewsExtension newsExtension() {
        return new NewsExtension();
    }

    private static final Trail PATH = trail("net/splitcells/website/news.html");

    private NewsExtension() {

    }

    private final CommonMarkChangelogEventProjectRendererExtension eventUtils = commonMarkChangelogEventRenderer();

    @Override
    public RenderResponse render(RenderRequest request, ProjectsRenderer projectsRenderer) {
        if (PATH.equals(request.trail())) {
            return renderResponse(projectsRenderer.renderContent(
                    parseNotificationQueue(request, projectsRenderer)
                            .withSelectedTags(BLOG_ARTICLE, PROJECT)
                            .toHtml()
                    , layoutConfig(PATH.unixPathString()).withTitle("News")));
        }
        return renderResponse(Optional.empty());
    }

    @Override
    public boolean requiresAuthentication(RenderRequest request) {
        return false;
    }

    @Override
    public Set<Path> projectPaths(ProjectPathsRequest request) {
        return setOfUniques(Path.of(PATH.unixPathString()));
    }

    public Set<Path> projectPaths(ProjectsRendererI projectsRendererI) {
        return setOfUniques(Path.of(PATH.unixPathString()));
    }
}
