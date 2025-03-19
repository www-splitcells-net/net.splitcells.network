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
import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.data.set.list.Lists;
import net.splitcells.dem.environment.config.StaticFlags;
import net.splitcells.dem.lang.Xml;
import net.splitcells.dem.resource.Trail;
import net.splitcells.dem.testing.Assertions;
import net.splitcells.website.Formats;
import net.splitcells.website.server.notify.Notification;
import net.splitcells.website.server.notify.NotificationQueue;
import net.splitcells.website.server.project.renderer.extension.commonmark.CommonMarkChangelogEventProjectRendererExtension;
import net.splitcells.website.server.project.renderer.extension.commonmark.CommonMarkIntegration;
import net.splitcells.website.server.projects.ProjectsRenderer;
import net.splitcells.website.server.projects.RenderRequest;
import net.splitcells.website.server.projects.RenderResponse;
import net.splitcells.website.server.projects.extension.ProjectsRendererExtension;

import java.nio.file.Path;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Optional;

import static net.splitcells.dem.data.set.Sets.setOfUniques;
import static net.splitcells.dem.environment.config.StaticFlags.ENFORCING_UNIT_CONSISTENCY;
import static net.splitcells.dem.resource.Trail.trail;
import static net.splitcells.dem.testing.Assertions.requireEquals;
import static net.splitcells.website.Formats.HTML;
import static net.splitcells.website.server.notify.Notification.notification;
import static net.splitcells.website.server.notify.NotificationQueue.notificationQueue;
import static net.splitcells.website.server.project.LayoutConfig.layoutConfig;
import static net.splitcells.website.server.project.renderer.extension.commonmark.CommonMarkChangelogEventProjectRendererExtension.commonMarkChangelogEventRenderer;
import static net.splitcells.website.server.project.renderer.extension.commonmark.CommonMarkIntegration.commonMarkIntegration;
import static net.splitcells.website.server.projects.RenderResponse.renderResponse;
import static net.splitcells.website.server.projects.extension.impls.ProjectPathsRequest.projectPathsRequest;
import static net.splitcells.website.server.security.authorization.AdminRole.ADMIN_ROLE;
import static net.splitcells.website.server.security.authorization.Authorization.missesRole;

/**
 * TODO IDEA Render Codeberg issues, that are relevant to this project.
 */
public class NotificationExtension implements ProjectsRendererExtension {
    private static final Trail PATH = trail("net/splitcells/website/notifications.html");

    public static NotificationExtension notificationExtension() {
        return new NotificationExtension();
    }

    private final CommonMarkChangelogEventProjectRendererExtension eventUtils = commonMarkChangelogEventRenderer();
    private final CommonMarkIntegration renderer = commonMarkIntegration();

    private NotificationExtension() {

    }

    @Override
    public RenderResponse render(RenderRequest request, ProjectsRenderer projectsRenderer) {
        if (PATH.equals(request.trail())) {
            final var notificationQueue = notificationQueue();
            notificationQueue.withAdditionalNotifications(
                    projectsRenderer.projectRenderers().stream()
                            .map(pr -> eventUtils.extractEvent(pr.resourceRootPath2().resolve("CHANGELOG.events.html").toString(), pr, projectsRenderer.config()))
                            .reduce(List::withAppended)
                            .orElseGet(Lists::list)
                            .stream()
                            .map(event ->
                                    notification(event.dateTime().atZone(ZoneId.of("UTC")).toInstant()
                                            , HTML
                                            , renderer.render(event.node()))).toList());
            notificationQueue.withAdditionalNotifications(
                    projectsRenderer.projectPaths(projectPathsRequest(projectsRenderer).withUser(request.user()))
                            .stream()
                            .filter(p -> p.toString().startsWith("net/splitcells/network/community/blog/articles/"))
                            .map(article ->
                                    projectsRenderer.metaData(article.toString())
                                            .flatMap(meta -> meta.title().map(title -> notification(Instant.now(), HTML, Xml.escape(title)))))
                            .filter(Optional::isPresent)
                            .map(Optional::get)
                            .toList());
            notificationQueue.withAdditionalNotifications(
                    projectsRenderer.projectsPaths()
                            .stream()
                            .filter(p -> p.toString().startsWith("net/splitcells/network/community/blog/articles/"))
                            .map(article ->
                                    projectsRenderer.metaData(article.toString())
                                            .flatMap(meta -> meta.title().map(title -> notification(Instant.now(), HTML, Xml.escape(title)))))
                            .filter(Optional::isPresent)
                            .map(Optional::get)
                            .toList());
            if (ENFORCING_UNIT_CONSISTENCY) {
                notificationQueue.notifications().forEach(n -> requireEquals(n.format(), HTML));
            }
            final var content = "<ol xmlns=\"http://www.w3.org/1999/xhtml\">"
                    + notificationQueue.notifications().reversed().stream()
                    .map(n -> n.content())
                    .reduce((a, b) -> a + "\n" + b)
                    .orElse("")
                    + "</ol>";
            return renderResponse(projectsRenderer.renderContent(content
                    , layoutConfig(PATH.unixPathString())
                            .withTitle("Notifications")
            ));
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
}
