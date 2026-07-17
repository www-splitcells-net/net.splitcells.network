/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.website.server.projects.extension.impls;

import net.splitcells.dem.data.set.Set;
import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.data.set.list.Lists;
import net.splitcells.dem.resource.Trail;
import net.splitcells.website.server.Config;
import net.splitcells.website.server.notify.NotificationQueueParser;
import net.splitcells.website.server.processor.BinaryMessage;
import net.splitcells.website.server.project.renderer.extension.commonmark.CommonMarkChangelogEventProjectRendererExtension;
import net.splitcells.website.server.projects.ProjectsRenderer;
import net.splitcells.website.server.projects.ProjectsRendererI;
import net.splitcells.website.server.projects.RenderRequest;
import net.splitcells.website.server.projects.RenderResponse;
import net.splitcells.website.server.projects.extension.ProjectsRendererExtension;

import java.nio.file.Path;
import java.util.Optional;

import static net.splitcells.dem.data.set.Sets.setOfUniques;
import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.resource.Trail.trail;
import static net.splitcells.website.server.notify.NotificationQueueParser.CHANGELOG;
import static net.splitcells.website.server.notify.NotificationQueueParser.parseNotificationQueue;
import static net.splitcells.website.server.processor.BinaryMessage.binaryMessage;
import static net.splitcells.website.server.project.LayoutConfig.layoutConfig;
import static net.splitcells.website.server.project.renderer.extension.commonmark.CommonMarkChangelogEventProjectRendererExtension.commonMarkChangelogEventRenderer;
import static net.splitcells.website.server.projects.RenderResponse.renderResponse;

public class GlobalChangelogExtension implements ProjectsRendererExtension {
    public static GlobalChangelogExtension globalChangelogExtension() {
        return new GlobalChangelogExtension();
    }

    private static final Trail PATH = trail("net/splitcells/CHANGELOG.global.html");

    private GlobalChangelogExtension() {

    }

    private final CommonMarkChangelogEventProjectRendererExtension eventUtils = commonMarkChangelogEventRenderer();

    @Override public RenderResponse render(RenderRequest request, ProjectsRenderer projectsRenderer) {
        if (PATH.equals(request.trail())) {
            return renderResponse(projectsRenderer.renderContent(
                    parseNotificationQueue(request, projectsRenderer).toHtml()
                    , layoutConfig(PATH.unixPathString()).withTitle("Global Changelog")));
        }
        return renderResponse(Optional.empty());
    }

    @Override public boolean requiresAuthentication(RenderRequest request) {
        return false;
    }

    @Override public Set<Path> projectPaths(ProjectPathsRequest request) {
        return setOfUniques(Path.of(PATH.unixPathString()));
    }

    @Override public Set<Path> projectPaths(ProjectsRendererI projectsRendererI) {
        return setOfUniques(Path.of(PATH.unixPathString()));
    }
}
