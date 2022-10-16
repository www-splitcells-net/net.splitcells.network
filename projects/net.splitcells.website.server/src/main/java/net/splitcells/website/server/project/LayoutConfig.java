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
package net.splitcells.website.server.project;

import net.splitcells.dem.lang.annotations.ReturnsThis;

import java.util.Optional;

/**
 * This config contains the metadata of a path/document,
 * which is used in order to improve its rendering.
 */
public class LayoutConfig {
    public static LayoutConfig layoutConfig(String path) {
        return new LayoutConfig(path);
    }

    private final String path;
    private Optional<String> title = Optional.empty();

    private LayoutConfig(String path) {
        this.path = path;
    }

    public String path() {
        return path;
    }

    public Optional<String> title() {
        return title;
    }

    public LayoutConfig withTitle(String title) {
        this.title = Optional.of(title);
        return this;
    }
}
