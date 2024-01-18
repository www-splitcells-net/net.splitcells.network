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
package net.splitcells.website.server.project.renderer.extension;

import net.splitcells.dem.data.set.Set;
import net.splitcells.dem.lang.perspective.Perspective;
import net.splitcells.website.server.Config;
import net.splitcells.website.server.project.ProjectRenderer;
import net.splitcells.website.server.processor.BinaryMessage;
import net.splitcells.website.server.project.renderer.PageMetaData;
import net.splitcells.website.server.projects.ProjectsRenderer;
import net.splitcells.website.server.projects.extension.ProjectsRendererExtension;

import java.nio.file.Path;
import java.util.Optional;

import static net.splitcells.dem.data.set.Sets.setOfUniques;
import static net.splitcells.dem.utils.NotImplementedYet.notImplementedYet;

/**
 * TODO Do not only test {@link #projectPaths(ProjectRenderer)},
 * but also {@link #renderFile(String, ProjectRenderer, Config)}.
 */
public interface ProjectRendererExtension {
    /**
     * Renders a file for a given {@code path}.
     * This method is deprecated an {@link #renderFile(String, ProjectsRenderer, ProjectRenderer)} should be used
     * instead.
     * This was done, in order to allow {@link ProjectsRendererExtension} capabilities to be used
     * in {@link ProjectRendererExtension}.
     * The primary difference between the 2, is that, the former does not require any files/resources.
     * The latter always requires a folder containing projects files,
     * that are to be rendered on the website.
     *
     * @param path            Absolute Path To Be Rendered
     * @param projectRenderer Basic Rendering Utilities
     * @param config          Web Server Configuration
     * @return
     */
    @Deprecated
    default Optional<BinaryMessage> renderFile(String path, ProjectRenderer projectRenderer, Config config) {
        throw notImplementedYet();
    }

    default Optional<PageMetaData> metaData(String path, ProjectsRenderer projectsRenderer, ProjectRenderer projectRenderer) {
        return Optional.empty();
    }

    default Optional<BinaryMessage> sourceCode(String path) {
        return Optional.empty();
    }

    /**
     * Renders a file for a given {@code path}.
     *
     * @param path             Absolute Path To Be Rendered
     * @param projectsRenderer This is the complete renderer infrastructure.
     *                         For instance, here the {@link Config} of that infrastructure can be retrieved.
     * @param projectRenderer  Basic Rendering Utilities
     * @return
     */
    default Optional<BinaryMessage> renderFile(String path, ProjectsRenderer projectsRenderer, ProjectRenderer projectRenderer) {
        return renderFile(path, projectRenderer, projectsRenderer.config());
    }

    /**
     * TODO A list of paths should be returned instead. Also the layout would not be needed anymore in this case.
     *
     * @param layout
     * @param projectRenderer Project Rendered To Be Extended
     * @return
     */
    default Perspective extendProjectLayout(Perspective layout, ProjectRenderer projectRenderer) {
        return layout;
    }

    /**
     * TODO In future, it would be best to just use {@link #extendProjectLayout}.
     * <p>
     * This is only provided, because it was easier to implement this without errors,
     * compared to {@link #extendProjectLayout}.
     * This will probably change, when {@link Perspective} code is better tested and reviewed,
     * also a code to transform such a set to a {@link Perspective} layout, might always be useful.
     *
     * @param projectRenderer Project Rendered To Be Extended
     * @return Paths relative to project's root path. Absolute paths are not supported.
     */
    Set<Path> projectPaths(ProjectRenderer projectRenderer);

    /**
     * Returns the paths,
     * that can be passed to {@link #renderFile(String, ProjectRenderer, Config)}
     * and are relevant for users to view.
     *
     * @param projectRenderer The project rendered Tt be extended.
     * @return Paths relative to project's root path.
     * Absolute paths are not supported.
     */
    default Set<Path> relevantProjectPaths(ProjectRenderer projectRenderer) {
        return setOfUniques();
    }
}
