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
package net.splitcells.website.server.project;

import net.splitcells.dem.data.set.list.Lists;
import net.splitcells.dem.lang.namespace.NameSpaces;
import net.splitcells.dem.lang.perspective.Tree;

import java.nio.file.Path;

import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.data.set.list.Lists.toList;
import static net.splitcells.dem.lang.namespace.NameSpaces.STRING;
import static net.splitcells.dem.lang.perspective.TreeI.perspective;
import static net.splitcells.dem.utils.ConstructorIllegal.constructorIllegal;

public class LayoutUtils {
    private LayoutUtils() {
        throw constructorIllegal();
    }

    /**
     * @param current
     * @param relativeProjectPath Path relative to the project folders src/xml folder. This path also represent absolute path in projects namespace.
     */
    public static Tree extendPerspectiveWithPath(Tree current, Path relativeProjectPath) {
        for (final var element : list(relativeProjectPath.toString().split("/"))
                .stream()
                .filter(e -> !"".contentEquals(e))
                .collect(toList())) {
            final var children = current.children().stream()
                    .filter(child -> child.nameIs(NameSpaces.VAL, NameSpaces.NATURAL))
                    .filter(child -> child.propertyInstances(NameSpaces.NAME, NameSpaces.NATURAL).stream()
                            .anyMatch(property -> property.value().get().name().equals(element)))
                    .collect(Lists.toList());
            final Tree child;
            if (children.isEmpty()) {
                child = perspective(NameSpaces.VAL, NameSpaces.NATURAL)
                        .withProperty(NameSpaces.NAME, NameSpaces.NATURAL, element);
                current.withChild(child);
            } else {
                child = children.get(0);
            }
            current = child;
        }
        current.withChild(
                perspective(NameSpaces.LINK, NameSpaces.DEN)
                        .withChild(perspective(NameSpaces.URL, NameSpaces.DEN)
                                .withChild(perspective("/" + relativeProjectPath.toString(), STRING))));
        return current;
    }

    public static Tree extendPerspectiveWithSimplePath(Tree current, Path relativeProjectPath) {
        for (final var element : list(relativeProjectPath.toString().replace('\\', '/').split("/"))
                .stream()
                .filter(e -> !"".contentEquals(e))
                .collect(toList())) {
            final var children = current.children().stream()
                    .filter(child -> child.nameIs(NameSpaces.VAL, NameSpaces.DEN))
                    .filter(child -> child.propertyInstances(NameSpaces.NAME, NameSpaces.DEN).stream()
                            .anyMatch(property -> property.value().get().name().equals(element)))
                    .collect(Lists.toList());
            final Tree child;
            if (children.isEmpty()) {
                child = perspective(NameSpaces.VAL, NameSpaces.DEN)
                        .withProperty(NameSpaces.NAME, NameSpaces.DEN, element);
                current.withChild(child);
            } else {
                child = children.get(0);
            }
            current = child;
        }
        current.withChild(
                perspective(NameSpaces.LINK, NameSpaces.DEN)
                        .withChild(perspective(NameSpaces.URL, NameSpaces.DEN)
                                .withChild(perspective("/" + relativeProjectPath.toString(), STRING))));
        return current;
    }
}
