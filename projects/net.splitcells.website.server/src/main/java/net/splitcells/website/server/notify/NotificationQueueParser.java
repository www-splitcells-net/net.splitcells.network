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
import net.splitcells.dem.lang.Xml;
import net.splitcells.website.server.project.renderer.PageMetaData;
import net.splitcells.website.server.projects.ProjectsRenderer;
import net.splitcells.website.server.projects.RenderRequest;

import java.nio.file.Path;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.regex.Pattern;

import static net.splitcells.dem.environment.config.StaticFlags.ENFORCING_UNIT_CONSISTENCY;
import static net.splitcells.dem.testing.Assertions.requireEquals;
import static net.splitcells.dem.utils.StringUtils.parseString;
import static net.splitcells.dem.utils.TimeUtils.parseZonedDate;
import static net.splitcells.website.Format.HTML;
import static net.splitcells.website.server.notify.Notification.notification;
import static net.splitcells.website.server.notify.NotificationQueue.notificationQueue;
import static net.splitcells.website.server.project.renderer.extension.commonmark.NotificationParser.parseNotificationsFromChangelog;
import static net.splitcells.website.server.projects.extension.impls.ProjectPathsRequest.projectPathsRequest;

/**
 * TODO IDEA Render Codeberg issues, that are relevant to this project.
 */
public class NotificationQueueParser {
    public static final String BLOG_ARTICLE = "blog article";
    public static final String PROJECT_PROPOSAL = "project";
    public static final String PROJECT = "project";
    public static final String CHANGELOG = "changelog";
    private static final DateTimeFormatter NOTIFICATION_DATE = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final Pattern ARTICLE_FILE_NAME_DATE_PREFIX = Pattern.compile("\\d{4}-\\d{2}-\\d{2}-.*");
    private static final Pattern START_DATE = Pattern.compile("(\\* Start Date: )(\\d{4}-\\d{2}-\\d{2})");
    private static final Pattern END_DATE = Pattern.compile("(\\* End Date: )(\\d{4}-\\d{2}-\\d{2})");

    private NotificationQueueParser() {

    }

    /**
     * @param fileName The file name of a blog article in the Network Community.
     * @return The date time in the given {@code fileName}.
     */
    private static ZonedDateTime parseArticleDateFromFileName(String fileName) {
        if (ARTICLE_FILE_NAME_DATE_PREFIX.matcher(fileName).matches()) {
            return parseZonedDate(fileName.substring(0, 10));
        }
        return ZonedDateTime.now();
    }

    private static Optional<ZonedDateTime> parseArticleStartDate(String content) {
        val matcher = START_DATE.matcher(content);
        if (matcher.find()) {
            return Optional.of(parseZonedDate(matcher.group(2)));
        }
        return Optional.empty();
    }

    private static Optional<ZonedDateTime> parseArticleEndDate(String content) {
        val matcher = END_DATE.matcher(content);
        if (matcher.find()) {
            return Optional.of(parseZonedDate(matcher.group(2)));
        }
        return Optional.empty();
    }

    private static void parseNotification(NotificationQueue notificationQueue, ProjectsRenderer projectsRenderer, Path article, String... tags) {
        final var metaData = projectsRenderer.metaData(article.toString());
        final var title = metaData.flatMap(PageMetaData::title);
        if (title.isPresent()) {
            val titleVar = title.get();
            val notification = notification(parseArticleDateFromFileName(article.getFileName().toString()), HTML, "")
                    .withTitle(Optional.of(Xml.escape(titleVar)))
                    .withLink(Optional.of("/" + article))
                    .withTags(tags);
            parseArticleStartDate(parseString(projectsRenderer.sourceCode(article.toString()).orElseThrow().getContent()))
                    .ifPresent(startDate -> notificationQueue.withAdditionalNotification(
                            notification.deepClone(Notification.class)
                                    .withTime(startDate)
                                    .withTitle(toStartTitle(titleVar))));
            parseArticleEndDate(parseString(projectsRenderer.sourceCode(article.toString()).orElseThrow().getContent()))
                    .ifPresent(startDate -> notificationQueue.withAdditionalNotification(
                            notification.deepClone(Notification.class)
                                    .withTime(startDate)
                                    .withTitle(toEndTitle(titleVar))));
            notificationQueue.withAdditionalNotification(notification);
        }
    }

    private static String toStartTitle(String arg) {
        if (arg.endsWith(".")) {
            return "Project Start: " + arg;
        }
        return "Start of the Project " + arg;
    }

    private static String toEndTitle(String arg) {
        if (arg.endsWith(".")) {
            return "Project End: " + arg;
        }
        return "End of the Project " + arg;
    }

    private static String toProposalTitle(String arg) {
        if (arg.endsWith(".")) {
            return "Project Proposal: " + arg;
        }
        return "Proposal of the Project " + arg;
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
