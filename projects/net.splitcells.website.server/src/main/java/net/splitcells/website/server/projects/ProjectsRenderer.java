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
package net.splitcells.website.server.projects;

import net.splitcells.dem.data.set.Set;
import net.splitcells.dem.data.set.list.List;
import net.splitcells.website.server.Config;
import net.splitcells.website.server.project.LayoutConfig;
import net.splitcells.website.server.project.ProjectRenderer;
import net.splitcells.website.server.project.RenderingResult;

import java.nio.file.Path;
import java.util.Optional;

import static net.splitcells.dem.data.set.Sets.setOfUniques;
import static net.splitcells.dem.data.set.list.Lists.listWithValuesOf;
import static net.splitcells.dem.lang.namespace.NameSpaces.DEN;
import static net.splitcells.dem.lang.namespace.NameSpaces.NAME;
import static net.splitcells.dem.utils.NotImplementedYet.notImplementedYet;

public interface ProjectsRenderer {
    /**
     * <p>
     *     Creates all meta info maybe required for rendering.
     *     This method is called by all rendering methods, that render all files at once, by default,
     *     except, if otherwise explicitly stated in the arguments.</p>
     * <p>
     *     So normally, this method does not have to be called explicitly.
     *     This is not the case for rendering methods, that only render single paths
     *     like {@link #render(String)}.
     * </p>
     */
    void build();

    /**
     * TODO Create flag in order to trigger incremental,
     * where only changed files are build.
     * Find out changed files via modification time or git history.
     * <p>
     * TODO This belongs to a dedicated class.
     *
     * @param target Folder where the rendered files are written to.
     */
    @Deprecated
    void serveTo(Path target);

    /**
     * TODO This belongs to a dedicated class.
     */
    @Deprecated
    void serveToHttpAt();

    /**
     * TODO This belongs to a dedicated class.
     */
    @Deprecated
    void serveAsAuthenticatedHttpsAt();

    /**
     * Renders the file of the given {@param path}.
     * This file has to be present in {@link #projectsPaths}.
     *
     * @param path This is the path of the file being rendered.
     * @return The rendered file.
     */
    Optional<RenderingResult> render(String path);

    /**
     * @return All Paths In Relative Form
     */
    Set<Path> projectsPaths();

    /**
     * @return All Relevant Paths In Relative Form
     */
    Set<Path> relevantProjectsPaths();

    Config config();

    @Deprecated
    List<ProjectRenderer> projectRenderers();

    @Deprecated
    Optional<byte[]> renderHtmlBodyContent(String bodyContent
            , Optional<String> title
            , Optional<String> path
            , Config config);

    /**
     * Renders a given String to the main output format.
     *
     * @param content     This is the String to be rendered.
     * @param metaContent Additional information about {@param content}.
     * @return This is the rendered String.
     */
    Optional<RenderingResult> renderContent(String content, LayoutConfig metaContent);

    default Set<String> relevantParentPages(String path) {
        final Set<String> relevantParentPages = setOfUniques();
        final var pathElements = listWithValuesOf(path.split("/"));
        while (pathElements.hasElements()) {
            pathElements.removeAt(pathElements.size() - 1);
            pathElements.withAppended("index.html");
            final var potentialPage = config().layoutPerspective().orElseThrow().pathOfDenValueTree
                    (pathElements.stream().reduce("", (a, b) -> a + "/" + b).substring(1));
            if (potentialPage.isPresent()) {
                final var parentPage = potentialPage.orElseThrow().stream()
                        .map(e -> e.propertyInstance(NAME, DEN).orElseThrow().valueName())
                        .reduce("", (a, b) -> a + "/" + b);
                relevantParentPages.with(parentPage);
            }
            pathElements.removeAt(pathElements.size() - 1);
        }
        return relevantParentPages;
    }
}
