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

import net.splitcells.website.Formats;

import java.time.Instant;

public class Notification {
    public static Notification notification(Instant time, Formats format, String content) {
        return new Notification(time, format, content);
    }

    private final Instant time;
    private final Formats format;
    private final String content;

    private Notification(Instant argTime, Formats argFormat, String argContent) {
        time = argTime;
        format = argFormat;
        content = argContent;
    }

    public Instant time() {
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
}
