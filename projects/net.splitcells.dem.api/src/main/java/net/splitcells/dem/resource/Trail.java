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

import net.splitcells.dem.data.atom.Thing;
import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.data.set.list.ListView;

import java.nio.file.Path;

import static net.splitcells.dem.data.set.list.Lists.listWithValuesOf;
import static net.splitcells.dem.utils.ExecutionException.executionException;

/**
 * <p>TODO Ensure that only trails, that adhere to guidelines are accepted.</p>
 * Similar functionality as {@link Path}, that avoids the distinction between
 * relative and absolute {@link Path} instances.
 * Thereby bugs are avoided,
 * that are created via {@link Path#resolve(Path)} by accidentally mixing relative and absolute paths.
 */
public class Trail implements Thing {
    public static Trail trail(String... content) {
        return new Trail(listWithValuesOf(content));
    }

    public static Trail trail(String path) {
        return new Trail(listWithValuesOf(path.split("/")));
    }

    private final List<String> content;

    private Trail(List<String> contentArg) {
        this.content = contentArg;
    }

    public ListView<String> content() {
        return content;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Trail) {
            final var other = (Trail) obj;
            return content.equals(other.content);
        } else {
            throw executionException("Illegal argument: " + obj);
        }
    }

    @Override
    public int hashCode() {
        return Thing.hashCode(content);
    }
}
