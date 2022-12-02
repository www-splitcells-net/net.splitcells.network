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
package net.splitcells.website.server.projects;

import net.splitcells.dem.data.set.Set;
import net.splitcells.dem.data.set.list.List;
import net.splitcells.website.server.Config;
import net.splitcells.website.server.project.LayoutConfig;
import net.splitcells.website.server.project.ProjectRenderer;
import net.splitcells.website.server.project.RenderingResult;

import java.nio.file.Path;
import java.util.Optional;

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
}
