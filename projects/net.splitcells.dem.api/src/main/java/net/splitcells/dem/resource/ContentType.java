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
package net.splitcells.dem.resource;

import net.splitcells.dem.data.Flows;

import java.util.Optional;

import static net.splitcells.dem.data.Flows.flow;

public enum ContentType {
    BINARY("application/octet-stream", true),
    COMMON_MARK("text/markdown", false),
    CSV("csv", false),
    UTF_8("UTF-8", false),
    HTML_TEXT("text/html", false),
    CSS("text/css", false),
    JS("text/javascript", false),
    TEXT("text/plain", false),
    XML("application/xml", false),
    ZIP("application/zip", true),
    JSON("application/json", false),
    GIF("image/gif", true);

    private final String codeName;
    private final boolean isBinary;

    ContentType(String argCodeName, boolean argIsBinary) {
        codeName = argCodeName;
        isBinary = argIsBinary;
    }

    public String codeName() {
        return codeName;
    }

    public boolean isBinary() {
        return isBinary;
    }

    public static Optional<ContentType> parseOptionally(String arg) {
        return flow(values()).filter(v -> v.codeName().equals(arg)).findFirst();
    }
}
