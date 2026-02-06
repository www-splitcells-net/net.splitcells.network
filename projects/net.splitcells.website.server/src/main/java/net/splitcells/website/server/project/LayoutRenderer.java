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

import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.lang.namespace.NameSpace;
import net.splitcells.dem.lang.tree.Tree;
import net.splitcells.dem.lang.tree.TreeI;
import net.splitcells.website.server.Config;
import net.splitcells.dem.resource.Files;

import java.nio.file.Paths;
import java.util.stream.Stream;

import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.lang.namespace.NameSpaces.NATURAL;
import static net.splitcells.dem.lang.tree.TreeI.tree;
import static net.splitcells.website.Projects.projectsRenderer;

@Deprecated
public class LayoutRenderer {
    public static void main(String... args) {
        // TODO privateProjectsRenderer().build();
        projectsRenderer(Config.create()).build();
        final var layout = TreeI.tree("layout", NATURAL);
        Stream.concat(
                        Files.walkRecursively(Paths.get("../net.splitcells.shell/src/main/bash"))
                                .filter(Files::isFile)
                        , Files.walkRecursively(Paths.get("../net.splitcells.shell/src/main/python"))
                                .filter(Files::isFile))
                .sorted()
                .forEach(file -> extend(layout, list(file.toFile().getName().split("\\."))));
        System.out.println(Paths.get(System.getProperty("user.home"))
                .resolve("Documents/projects/net.splitcells.martins.avots.support.system/private")
                .resolve("net.splitcells.martins.avots.support.system/src/main/")
                .resolve("xml/net/splitcells/martins/avots/support/system/layout.xml"));
        Files.writeToFile(
                Paths.get(System.getProperty("user.home"))
                        .resolve("Documents/projects/net.splitcells.martins.avots.support.system/private")
                        .resolve("net.splitcells.martins.avots.support.system/src/main/")
                        .resolve("xml/net/splitcells/martins/avots/support/system/layout.xml")
                , layout.toXmlString());
    }

    public static void extend(Tree tree, List<String> path) {
        extend(tree, path, NATURAL);
    }

    public static void extend(Tree tree, List<String> path, NameSpace nameSpaces) {
        if (path.isEmpty()) {
            return;
        }
        final Tree nextChild = tree
                .childNamed(path.get(0), nameSpaces)
                .orElseGet(() -> {
                    final var nextChild2 = TreeI.tree(path.get(0), nameSpaces);
                    tree.withChild(nextChild2);
                    return nextChild2;
                });
        path.remove(0);
        extend(nextChild, path, nameSpaces);
    }
}