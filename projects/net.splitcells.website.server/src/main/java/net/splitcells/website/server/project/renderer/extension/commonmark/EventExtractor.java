/*
 * Copyright (c) 2021 Mārtiņš Avots (Martins Avots) and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0, or the MIT License,
 * which is available at https://spdx.org/licenses/MIT.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR MIT
 */
package net.splitcells.website.server.project.renderer.extension.commonmark;

import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.lang.annotations.JavaLegacyArtifact;
import org.commonmark.node.AbstractVisitor;
import org.commonmark.node.ListItem;
import org.commonmark.node.StrongEmphasis;
import org.commonmark.node.Text;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.regex.Pattern;

import static net.splitcells.dem.data.set.list.Lists.list;

/**
 * Extracts {@link Event} of changelogs written in CommonMark, by searching for list entries starting with {@link #DATE_TIME_PATTERN}.
 * This can be seen as a not standardized extension of <a href="https://keepachangelog.com/en/1.0.0/">Keep a Changelog</a>.
 */
@JavaLegacyArtifact
public class EventExtractor extends AbstractVisitor {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    public static final Pattern DATE_TIME_PATTERN = Pattern.compile("([0-9][0-9][0-9][0-9]-[0-9][0-9]-[0-9][0-9])( .*)?");

    public static EventExtractor eventExtractor() {
        return new EventExtractor();
    }

    private List<Event> extractedEvents = list();

    private EventExtractor() {

    }

    @Override
    public void visit(ListItem listItem) {
        final var itemContent = listItem.getFirstChild();
        if (itemContent.getFirstChild() == null) {
            visitChildren(listItem);
            return;
        }
        final var possibleDate = itemContent.getFirstChild();
        if (possibleDate instanceof StrongEmphasis) {
            final var possibleDateEmphasis = (StrongEmphasis) possibleDate;
            final var possibleDateText = ((Text) possibleDateEmphasis.getFirstChild()).getLiteral();
            final var dateMatcher = DATE_TIME_PATTERN.matcher(possibleDateText);
            if (dateMatcher.matches()) {
                final var dateTime = LocalDate.parse(dateMatcher.group(1), DATE_TIME_FORMATTER);
                extractedEvents.add(Event.event(LocalDateTime.of(dateTime, LocalTime.of(0, 0)), listItem));
            }
        }
        visitChildren(listItem);
    }

    public List<Event> extractedEvents() {
        return extractedEvents;
    }
}
