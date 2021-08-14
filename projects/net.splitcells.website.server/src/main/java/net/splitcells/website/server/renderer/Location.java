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
package net.splitcells.website.server.renderer;

import java.nio.file.Path;

public class Location {
    public static Location location(Path root, Path relativeChild) {
        return new Location(root, relativeChild);
    }

    private final Path root;
    /**
     * RENAME
     */
    private final Path relativeChild;

    private Location(Path root, Path relativeChild) {
        this.root = root;
        this.relativeChild = relativeChild;
    }

    public Path relativeChild() {
        return relativeChild;
    }

    public Path child() {
        return root.resolve(relativeChild);
    }

    public Path root() {
        return root;
    }
}
