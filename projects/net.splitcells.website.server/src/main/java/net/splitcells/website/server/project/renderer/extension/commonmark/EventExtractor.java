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
@JavaLegacy
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
        if (possibleDate instanceof StrongEmphasis possibleDateEmphasis) {
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
