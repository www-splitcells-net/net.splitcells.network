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
package net.splitcells.website;

import lombok.val;
import net.splitcells.dem.resource.ContentType;
import net.splitcells.website.server.project.renderer.extension.commonmark.CommonMarkIntegration;

import java.util.Optional;

import static net.splitcells.dem.utils.ExecutionException.execException;
import static net.splitcells.dem.utils.NotImplementedYet.notImplementedYet;

/**
 * TODO REMOVE This is a duplicate of {@link ContentType}.
 */
public enum Format {
    BINARY("application/octet-stream", true),
    HTML("text/html", false),
    CSS("text/css", false),
    CSV("text/csv", false),
    TEXT_PLAIN("text/plain", false),
    COMMON_MARK("text/markdown", false),
    JSON("application/json", false),
    XML("application/xml", false),
    ZIP("application/zip", true);

    public static Format parse(String arg) {
        val parsed = parseOptionally(arg);
        if (parsed.isEmpty()) {
            throw execException("Unknown format: " + arg);
        }
        return parsed.get();
    }

    public static Optional<Format> parseOptionally(String arg) {
        return switch (arg) {
            case "text/html" -> Optional.of(HTML);
            case "text/css" -> Optional.of(CSS);
            case "text/csv" -> Optional.of(CSV);
            case "text/plain" -> Optional.of(TEXT_PLAIN);
            case "text/markdown" -> Optional.of(COMMON_MARK);
            case "application/json" -> Optional.of(JSON);
            case "application/xml" -> Optional.of(XML);
            default -> Optional.empty();
        };
    }


    private final String mimeTypes;
    private final boolean isBinary;

    Format(String mimeTypes, boolean argIsBinary) {
        this.mimeTypes = mimeTypes;
        isBinary = argIsBinary;
    }

    public String mimeTypes() {
        return mimeTypes;
    }

    public boolean isBinary() {
        return isBinary;
    }
}
