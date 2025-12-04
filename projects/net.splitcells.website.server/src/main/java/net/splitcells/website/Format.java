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

import net.splitcells.dem.resource.ContentType;
import net.splitcells.website.server.project.renderer.extension.commonmark.CommonMarkIntegration;

import static net.splitcells.dem.utils.ExecutionException.execException;
import static net.splitcells.dem.utils.NotImplementedYet.notImplementedYet;

/**
 * TODO REMOVE This is a duplicate of {@link ContentType}.
 */
public enum Format {
    BINARY("application/octet-stream"),
    HTML("text/html"),
    CSS("text/css"),
    CSV("text/csv"),
    TEXT_PLAIN("text/plain"),
    COMMON_MARK("text/markdown"),
    JSON("application/json"),
    XML("application/xml");

    public static Format parse(String arg) {
        switch (arg) {
            case "text/html":
                return HTML;
            case "text/css":
                return CSS;
            case "text/csv":
                return CSV;
            case "text/plain":
                return TEXT_PLAIN;
            case "text/markdown":
                return COMMON_MARK;
            case "application/json":
                return JSON;
            case "application/xml":
                return XML;
            default:
                throw execException("Unknown format: " + arg);
        }
    }

    private final String mimeTypes;

    Format(String mimeTypes) {
        this.mimeTypes = mimeTypes;
    }

    public String mimeTypes() {
        return mimeTypes;
    }
}
