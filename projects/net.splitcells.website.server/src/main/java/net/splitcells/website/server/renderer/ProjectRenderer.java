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

import net.splitcells.dem.data.set.Set;
import net.splitcells.dem.data.set.list.Lists;
import net.splitcells.dem.lang.namespace.NameSpaces;
import net.splitcells.dem.lang.perspective.Perspective;
import net.splitcells.website.Validator;

import java.nio.file.Path;
import java.util.Optional;

import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.data.set.list.Lists.toList;
import static net.splitcells.dem.data.set.map.Maps.map;
import static net.splitcells.dem.lang.namespace.NameSpaces.STRING;
import static net.splitcells.dem.lang.perspective.PerspectiveI.perspective;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public interface ProjectRenderer {

    static ProjectRenderer projectRenderer(String renderer, Path projectFolder, Path xslLibs, Path resources
            , String resourceRootPath
            , boolean typedFolder
            , boolean flatRepository
            , Validator validator) {
        return new ProjectRendererI(renderer, projectFolder.resolve("src/main"), xslLibs, resources, resourceRootPath
                , true
                , false
                , validator
                , projectFolder);
    }

    static ProjectRenderer projectRenderer(String renderer, Path projectFolder, Path xslLibs, Path resources
            , String resourceRootPath
            , Validator validator) {
        return new ProjectRendererI(renderer, projectFolder.resolve("src/main"), xslLibs, resources, resourceRootPath
                , true
                , false
                , validator
                , projectFolder);
    }

    /**
     * @param current
     * @param relativeProjectPath Path relative to the project folders src/xml folder. This path also represent absolute path in projects namespace.
     */
    static Perspective extendPerspectiveWithPath(Perspective current, Path relativeProjectPath) {
        for (final var element : list(relativeProjectPath.toString().split("/"))
                .stream()
                .filter(e -> !"".contentEquals(e))
                .collect(toList())) {
            final var children = current.children().stream()
                    .filter(child -> child.nameIs(NameSpaces.VAL, NameSpaces.NATURAL))
                    .filter(child -> child.propertyInstances(NameSpaces.NAME, NameSpaces.NATURAL).stream()
                            .anyMatch(property -> property.value().get().name().equals(element)))
                    .collect(Lists.toList());
            final Perspective child;
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

    @Deprecated
    Path projectFolder();

    Optional<RenderingResult> render(String path);

    @Deprecated
    Optional<byte[]> renderString(String arg);

    Optional<byte[]> renderHtmlBodyContent(String bodyContent, Optional<String> title, Optional<String> path);

    String resourceRootPath();

    @Deprecated
    Perspective projectLayout();

    Set<Path> projectPaths();
}
