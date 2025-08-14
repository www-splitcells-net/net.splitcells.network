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

import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.object.DeepCloneable;
import net.splitcells.website.Format;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.data.set.list.Lists.listWithValuesOf;
import static net.splitcells.dem.environment.config.StaticFlags.ENFORCING_UNIT_CONSISTENCY;
import static net.splitcells.dem.lang.tree.TreeI.tree;
import static net.splitcells.dem.testing.Assertions.requireEquals;
import static net.splitcells.dem.utils.ExecutionException.execException;
import static net.splitcells.dem.utils.NotImplementedYet.notImplementedYet;
import static net.splitcells.website.Format.HTML;

public class Notification implements DeepCloneable {
    private static final DateTimeFormatter NOTIFICATION_DATE = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public static Notification notification(ZonedDateTime time, Format format, String content) {
        return new Notification(time, format, content);
    }

    private ZonedDateTime time;
    private final Format format;
    private final String content;
    private Optional<String> title = Optional.empty();
    private Optional<String> link = Optional.empty();
    private final List<String> tags = list();

    private Notification(ZonedDateTime argTime, Format argFormat, String argContent) {
        time = argTime;
        format = argFormat;
        content = argContent;
    }

    public ZonedDateTime time() {
        return time;
    }

    public Notification withTime(ZonedDateTime argTime) {
        time = argTime;
        return this;
    }

    public Format format() {
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

    public Notification withTitle(String arg) {
        title = Optional.of(arg);
        return this;
    }

    public Optional<String> link() {
        return link;
    }

    public Notification withLink(Optional<String> arg) {
        link = arg;
        return this;
    }

    /**
     * As the date is secondary information it is not marked and styled strongly or with a big weight.
     *
     * @return
     */
    public String toHtml() {
        if (ENFORCING_UNIT_CONSISTENCY) {
            requireEquals(format, HTML);
        }
        var html = "<span>" + NOTIFICATION_DATE.format(time) + "</span>";
        if (title.isEmpty() && link.isPresent()) {
            html += " <a href=\"" + link.get() + "\">(link)</a>";
        }
        if (title.isPresent()) {
            if (link.isPresent()) {
                html += " <a href=\"" + link.get() + "\">" + title.get() + "</a>";
            } else {
                html += " <strong>" + title.get() + "</strong>";
            }
        }
        if (!content.isBlank()) {
            html += ": " + content;
        }
        return html;
    }

    public List<String> tags() {
        return tags;
    }

    public Notification withTags(String... arg) {
        tags.withAppended(arg);
        return this;
    }

    public Notification withTags(List<String> arg) {
        tags.withAppended(arg);
        return this;
    }

    @Override
    public <R> R deepClone(Class<? extends R> arg) {
        if (arg.equals(Notification.class)) {
            return (R) notification(time, format, content)
                    .withTags(listWithValuesOf(tags))
                    .withLink(link)
                    .withTitle(title);
        }
        throw execException(tree("Cannot clone instance of " + getClass().getName() + " to an instance of " + arg.getName()));
    }
}
