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
package net.splitcells.website.server.renderer.extension;

import net.splitcells.dem.data.set.Set;
import net.splitcells.dem.lang.perspective.Perspective;
import net.splitcells.website.server.renderer.ProjectRenderer;
import net.splitcells.website.server.renderer.RenderingResult;

import java.nio.file.Path;
import java.util.Optional;

public interface ProjectRendererExtension {
    Optional<RenderingResult> renderFile(String path, ProjectRenderer projectRenderer);

    /**
     * TODO A list of paths should be returned instead. Also the layout would not be needed anymore in this case.
     *
     * @param layout
     * @param projectRenderer
     * @return
     */
    Perspective extendProjectLayout(Perspective layout, ProjectRenderer projectRenderer);

    /**
     * @param projectRoot Path of the project.
     * @return Paths relative to project's root path.
     */
    Set<Path> projectPaths(Path projectRoot);

}
