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

import net.splitcells.dem.environment.config.StaticFlags;
import net.splitcells.dem.testing.Assertions;
import net.splitcells.website.Formats;

import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import static net.splitcells.dem.environment.config.StaticFlags.ENFORCING_UNIT_CONSISTENCY;
import static net.splitcells.dem.testing.Assertions.requireEquals;
import static net.splitcells.website.Formats.HTML;

public class Notification {
    private static final DateTimeFormatter NOTIFICATION_DATE = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public static Notification notification(ZonedDateTime time, Formats format, String content) {
        return new Notification(time, format, content);
    }

    private final ZonedDateTime time;
    private final Formats format;
    private final String content;
    private Optional<String> title = Optional.empty();
    private Optional<String> link = Optional.empty();

    private Notification(ZonedDateTime argTime, Formats argFormat, String argContent) {
        time = argTime;
        format = argFormat;
        content = argContent;
    }

    public ZonedDateTime time() {
        return time;
    }

    public Formats format() {
        return format;
    }

    public String content() {
        return content;
    }

    @Override
    public String toString() {
        return "time: " + time + ", format: " + format + ", content: " + content;
    }

    public Optional<String> title() {
        return title;
    }

    public Notification withTitle(Optional<String> arg) {
        title = arg;
        return this;
    }

    public Optional<String> link() {
        return link;
    }

    public Notification withLink(Optional<String> arg) {
        link = arg;
        return this;
    }

    public String toHtml() {
        if (ENFORCING_UNIT_CONSISTENCY) {
            requireEquals(format, HTML);
        }
        var html = "<strong>" + NOTIFICATION_DATE.format(time);
        if (title.isEmpty() && link.isPresent()) {
            html += " <a href=\"" + link.get() + "\">(link)</a>";
        }
        html += "</strong>: ";
        if (title.isPresent()) {
            if (link.isPresent()) {
                html += "<a href=\"" + link.get() + "\">" + title.get() + "</a>: ";
            } else {
                html += "<strong>" + title.get() + "</strong>: ";
            }
        }
        html += content;
        return html;
    }
}
