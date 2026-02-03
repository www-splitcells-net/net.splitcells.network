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
package net.splitcells.website.server.project.renderer.extension.commonmark;

import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.lang.annotations.JavaLegacy;
import net.splitcells.website.Format;
import net.splitcells.website.server.notify.Notification;
import net.splitcells.website.server.project.ProjectRenderer;
import org.commonmark.node.AbstractVisitor;
import org.commonmark.node.ListItem;
import org.commonmark.node.StrongEmphasis;
import org.commonmark.node.Text;
import org.commonmark.parser.Parser;

import java.nio.file.Path;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.regex.Pattern;

import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.website.server.notify.Notification.notification;
import static net.splitcells.website.server.project.renderer.extension.commonmark.CommonMarkIntegration.commonMarkIntegration;

/**
 * Extracts {@link Event} of changelogs written in CommonMark, by searching for list entries starting with {@link #DATE_TIME_PATTERN}.
 * This can be seen as a not standardized extension of <a href="https://keepachangelog.com/en/1.0.0/">Keep a Changelog</a>.
 */
@JavaLegacy
public class NotificationParser extends AbstractVisitor {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    public static final Pattern DATE_TIME_PATTERN = Pattern.compile("([0-9][0-9][0-9][0-9]-[0-9][0-9]-[0-9][0-9])( .*)?");
    /**
     *
     */
    public static final Pattern DATE_PREFIX = Pattern.compile("<strong>\\d{4}-\\d{2}-\\d{2}.*");
    private static final Path CHANGELOG = Path.of("CHANGELOG.md");

    private static final Parser commonMarkParser = Parser.builder().build();
    private final CommonMarkIntegration renderer = commonMarkIntegration();
    private List<Notification> parsedNotifications = list();
    private final String changelogPath;
    private final String[] tags;

    private NotificationParser(String argChangelogPath, String... argTags) {
        changelogPath = argChangelogPath;
        tags = argTags;
    }

    public static List<Notification> parseNotificationsFromChangelog(ProjectRenderer projectRenderer, String... tags) {
        if (projectRenderer.projectFileSystem().isFile(CHANGELOG)) {
            return parseNotificationsFromChangelog(projectRenderer.projectFileSystem().readString(CHANGELOG), projectRenderer.resourceRootPath2().toString() + "/CHANGELOG.html", tags);
        }
        return list();
    }

    private static List<Notification> parseNotificationsFromChangelog(String eventsAsCommonMark, String changelogPath, String... tags) {
        final String contentToRender;
        if (eventsAsCommonMark.startsWith("#")) {
            final var titleLine = eventsAsCommonMark.split("[\r\n]+")[0];
            contentToRender = eventsAsCommonMark.substring(titleLine.length());
        } else {
            contentToRender = eventsAsCommonMark;
        }
        final var parsing = commonMarkParser.parse(contentToRender);
        final var parser = new NotificationParser(changelogPath, tags);
        parsing.accept(parser);
        return parser.parsedNotifications;
    }

    /**
     * The rendering is a hack, because it is not known,
     * how to just render a list item's content while skipping the first AST element.
     *
     * @param listItem
     */
    @Override
    public void visit(ListItem listItem) {
        final var itemContent = listItem.getFirstChild();
        if (itemContent.getFirstChild() == null) {
            visitChildren(listItem);
            return;
        }
        final var possibleDate = itemContent.getFirstChild();
        if (possibleDate instanceof StrongEmphasis possibleDateEmphasis) {
            final var possibleDateText = ((Text) possibleDateEmphasis.getFirstChild()).getLiteral();
            final var dateMatcher = DATE_TIME_PATTERN.matcher(possibleDateText);
            if (dateMatcher.matches()) {
                final var dateTime = LocalDate.parse(dateMatcher.group(1), DATE_TIME_FORMATTER)
                        .atStartOfDay(ZoneId.of("UTC"));
                var content = renderer.render(listItem);
                if (content.startsWith("<li>")) {
                    content = content.substring(4);
                }
                if (content.endsWith("</li>\n")) {
                    content = content.substring(0, content.length() - 1 - 5);
                }
                // The replace makes the regex work.
                if (DATE_PREFIX.matcher(content.replace("\n", "")).matches()) {
                    content = "<strong>" + content.substring(18);
                }
                parsedNotifications.add(notification(dateTime, Format.HTML, content)
                        .withLink(Optional.of(changelogPath))
                        .withTags(tags));
            }
        }
        visitChildren(listItem);
    }
}
