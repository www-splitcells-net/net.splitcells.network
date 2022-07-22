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

import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.lang.Xml;
import net.splitcells.dem.lang.namespace.NameSpace;
import net.splitcells.dem.lang.namespace.NameSpaces;
import net.splitcells.dem.lang.perspective.Perspective;
import net.splitcells.website.server.Config;
import net.splitcells.dem.resource.Files;

import java.nio.file.Paths;
import java.util.stream.Stream;

import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.lang.namespace.NameSpaces.NATURAL;
import static net.splitcells.dem.lang.perspective.PerspectiveI.perspective;
import static net.splitcells.website.Projects.projectsRenderer;

@Deprecated
public class LayoutRenderer {
    public static void main(String... args) {
        // TODO privateProjectsRenderer().build();
        projectsRenderer(Config.create()).build();
        final var layout = perspective("layout", NATURAL);
        Stream.concat(
                Files.walk_recursively(Paths.get("../net.splitcells.os.state.interface/src/main/bash"))
                        .filter(Files::is_file)
                , Files.walk_recursively(Paths.get("../net.splitcells.os.state.interface/src/main/python"))
                        .filter(Files::is_file))
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
                , Xml.toPrettyString(perspective("", NameSpaces.NATURAL)
                        .withChild(layout).toDom()));
    }

    public static void extend(Perspective perspective, List<String> path) {
        extend(perspective, path, NATURAL);
    }

    public static void extend(Perspective perspective, List<String> path, NameSpace nameSpaces) {
        if (path.isEmpty()) {
            return;
        }
        final Perspective nextChild = perspective
                .childNamed(path.get(0), nameSpaces)
                .orElseGet(() -> {
                    final var nextChild2 = perspective(path.get(0), nameSpaces);
                    perspective.withChild(nextChild2);
                    return nextChild2;
                });
        path.remove(0);
        extend(nextChild, path, nameSpaces);
    }
}