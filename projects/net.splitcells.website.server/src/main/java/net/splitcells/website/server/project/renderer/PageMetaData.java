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
package net.splitcells.website.server.project.renderer;

import java.util.Optional;

public class PageMetaData {
    public static PageMetaData pageMetaData(String path) {
        return new PageMetaData(path);
    }

    private final String path;
    private Optional<String> title = Optional.empty();

    public PageMetaData(String path) {
        this.path = path;
    }

    public String path() {
        return path;
    }

    public PageMetaData withTitle(Optional<String> arg) {
        title = arg;
        return this;
    }

    public Optional<String> title() {
        return title;
    }

    public Optional<String> parentFolder() {
        final var pathSplit = path.split("/");
        if (pathSplit.length == 0 || pathSplit.length == 1) {
            return Optional.empty();
        }
        return Optional.of(pathSplit[pathSplit.length - 2]);
    }

    @Override
    public String toString() {
        return path + title.map(t -> ", " + t).orElse("");
    }
}
