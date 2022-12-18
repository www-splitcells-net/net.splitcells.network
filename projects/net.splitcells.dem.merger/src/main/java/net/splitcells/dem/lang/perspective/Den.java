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
package net.splitcells.dem.lang.perspective;

import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.lang.namespace.NameSpaces;

import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.lang.perspective.PerspectiveI.perspective;
import static net.splitcells.dem.utils.ExecutionException.executionException;

public class Den {

    public static Perspective val(String name) {
        return perspective("val").withProperty("name", name);
    }

    public static Perspective project(Perspective... arg) {
        return perspective("project");
    }

    public static Perspective todo(Perspective... arg) {
        return perspective("todo");
    }

    public static Perspective todo(String text, Perspective... arg) {
        return perspective("todo");
    }

    public static Perspective priority(Perspective... arg) {
        return perspective("priority");
    }

    public static Perspective queue(Perspective... arg) {
        return perspective("queue");
    }

    public static Perspective scheduling(Perspective... arg) {
        return perspective("scheduling");
    }

    public static Perspective solution(Perspective... arg) {
        return perspective("solution");
    }

    public static Perspective subtree(Perspective perspective, List<String> path) {
        if (path.isEmpty()) {
            return perspective;
        }
        final var next = path.remove(0);
        final var fittingChild = perspective.children().stream()
                .filter(child -> child.nameIs(NameSpaces.VAL, NameSpaces.DEN))
                .filter(child -> child.propertyInstances(NameSpaces.NAME, NameSpaces.DEN).stream()
                        .anyMatch(property -> property.value().get().name().equals(next)))
                .findFirst();
        if (fittingChild.isEmpty()) {
            throw executionException("Looking for `" + next + "` but only found `" + perspective.children() + "`.");
        }
        return subtree(fittingChild.get()
                , path);
    }
}
