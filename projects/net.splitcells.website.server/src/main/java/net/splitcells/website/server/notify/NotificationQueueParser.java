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
package net.splitcells.website.server.notify;

import lombok.val;
import net.splitcells.dem.data.atom.Integers;
import net.splitcells.dem.lang.Xml;
import net.splitcells.website.server.project.renderer.PageMetaData;
import net.splitcells.website.server.projects.ProjectsRenderer;
import net.splitcells.website.server.projects.RenderRequest;

import java.nio.file.Path;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.regex.Pattern;

import static net.splitcells.dem.environment.config.StaticFlags.ENFORCING_UNIT_CONSISTENCY;
import static net.splitcells.dem.testing.Assertions.requireEquals;
import static net.splitcells.website.Formats.HTML;
import static net.splitcells.website.server.notify.Notification.notification;
import static net.splitcells.website.server.notify.NotificationQueue.notificationQueue;
import static net.splitcells.website.server.project.renderer.extension.commonmark.NotificationParser.parseNotificationsFromChangelog;
import static net.splitcells.website.server.projects.extension.impls.ProjectPathsRequest.projectPathsRequest;

/**
 * TODO IDEA Render Codeberg issues, that are relevant to this project.
 */
public class NotificationQueueParser {
    public static final String BLOG_ARTICLE = "blog article";
    public static final String PROJECT = "project";
    public static final String CHANGELOG = "changelog";
    private static final DateTimeFormatter NOTIFICATION_DATE = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final Pattern ARTICLE_FILE_NAME_DATE_PREFIX = Pattern.compile("\\d{4}-\\d{2}-\\d{2}-.*");

    private NotificationQueueParser() {

    }

    /**
     * @param fileName The file name of a blog article in the Network Community.
     * @return The date time in the given {@code fileName}.
     */
    private static ZonedDateTime parseArticleDate(String fileName) {
        if (ARTICLE_FILE_NAME_DATE_PREFIX.matcher(fileName).matches()) {
            return ZonedDateTime.of(Integers.parse(fileName.substring(0, 4))
                    , Integers.parse(fileName.substring(5, 7))
                    , Integers.parse(fileName.substring(8, 10))
                    , 0
                    , 0
                    , 0
                    , 0
                    , ZoneId.of("UTC"));
        }
        return ZonedDateTime.now();
    }

    private static void parseNotification(NotificationQueue notificationQueue, ProjectsRenderer projectsRenderer, Path article, String... tags) {
        final var metaData = projectsRenderer.metaData(article.toString());
        final var title = metaData.flatMap(PageMetaData::title);
        if (title.isPresent()) {
            val notification = notification(parseArticleDate(article.getFileName().toString()), HTML, "")
                    .withTitle(Optional.of(Xml.escape(title.get())))
                    .withLink(Optional.of("/" + article))
                    .withTags(tags);
            notificationQueue.withAdditionalNotification(notification);
        }
    }

    public static NotificationQueue parseNotificationQueue(RenderRequest request, ProjectsRenderer projectsRenderer) {
        final var notificationQueue = notificationQueue();
        projectsRenderer.projectRenderers().stream()
                .map(pr -> parseNotificationsFromChangelog(pr, CHANGELOG))
                .forEach(ns -> ns.forEach(notificationQueue::withAdditionalNotification));
        projectsRenderer
                .projectPaths(projectPathsRequest(projectsRenderer).withUser(request.user()))
                .stream()
                .concat(projectsRenderer.projectsPaths().stream())
                .forEach(p -> {
                    if (!ARTICLE_FILE_NAME_DATE_PREFIX.matcher(p.getFileName().toString()).matches()) {
                        return;
                    }
                    if (p.toString().startsWith("net/splitcells/network/community/blog/")) {
                        parseNotification(notificationQueue, projectsRenderer, p, BLOG_ARTICLE);
                    } else if (p.toString().startsWith("net/splitcells/network/community/")) {
                        parseNotification(notificationQueue, projectsRenderer, p, PROJECT);
                    }
                });
        if (ENFORCING_UNIT_CONSISTENCY) {
            notificationQueue.notifications().forEach(n -> requireEquals(n.format(), HTML));
        }
        return notificationQueue;
    }
}
