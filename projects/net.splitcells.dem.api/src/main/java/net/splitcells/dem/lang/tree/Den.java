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
package net.splitcells.dem.lang.tree;

import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.lang.namespace.NameSpaces;

import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.lang.tree.TreeI.tree;
import static net.splitcells.dem.utils.ExecutionException.executionException;

public class Den {

    public static Tree val(String name) {
        return tree("val").withProperty("name", name);
    }

    public static Tree project(Tree... arg) {
        return tree("project");
    }

    public static Tree todo(Tree... arg) {
        return tree("todo");
    }

    public static Tree todo(String text, Tree... arg) {
        return tree("todo");
    }

    public static Tree priority(Tree... arg) {
        return tree("priority");
    }

    public static Tree queue(Tree... arg) {
        return tree("queue");
    }

    public static Tree scheduling(Tree... arg) {
        return tree("scheduling");
    }

    public static Tree solution(Tree... arg) {
        return tree("solution");
    }

    public static Tree subtree(Tree tree, List<String> path) {
        if (path.isEmpty()) {
            return tree;
        }
        final var next = path.remove(0);
        final var fittingChild = tree.children().stream()
                .filter(child -> child.nameIs(NameSpaces.VAL, NameSpaces.DEN))
                .filter(child -> child.propertyInstances(NameSpaces.NAME, NameSpaces.DEN).stream()
                        .anyMatch(property -> property.value().get().name().equals(next)))
                .findFirst();
        if (fittingChild.isEmpty()) {
            throw executionException("Looking for `" + next + "` but only found `" + tree.children() + "`.");
        }
        return subtree(fittingChild.get()
                , path);
    }
}
