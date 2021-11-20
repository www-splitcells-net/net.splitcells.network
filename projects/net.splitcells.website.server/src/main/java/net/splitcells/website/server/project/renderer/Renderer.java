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
package net.splitcells.website.server.project.renderer;

import net.splitcells.dem.data.set.Set;
import net.splitcells.dem.lang.perspective.Perspective;
import net.splitcells.website.server.project.ProjectRenderer;
import net.splitcells.website.server.project.RenderingResult;

import java.nio.file.Path;
import java.util.Optional;

/**
 * TODO Do not only test {@link #projectPaths(ProjectRenderer)}, but also {@link #renderFile(String, ProjectRenderer)}.
 */
public interface Renderer {
    Optional<RenderingResult> renderFile(String path, ProjectRenderer projectRenderer);

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
     *
     * This is only provided, because it was easier to implement this without errors,
     * compared to {@link #extendProjectLayout}.
     * This will probably change, when {@link Perspective} code is better tested and reviewed,
     * also a code to transform such a set to a {@link Perspective} layout, might always be useful.
     *
     * @param projectRenderer Project Rendered To Be Extended
     * @return Paths relative to project's root path.
     */
    Set<Path> projectPaths(ProjectRenderer projectRenderer);

}
