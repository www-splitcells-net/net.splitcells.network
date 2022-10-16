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
package net.splitcells.website.server.projects.extension;

import net.splitcells.dem.data.set.Set;
import net.splitcells.dem.lang.perspective.Perspective;
import net.splitcells.website.server.Config;
import net.splitcells.website.server.project.RenderingResult;
import net.splitcells.website.server.projects.ProjectsRendererI;

import java.nio.file.Path;
import java.util.Optional;

import static net.splitcells.dem.data.set.Sets.setOfUniques;

public interface ProjectsRendererExtension {
    Optional<RenderingResult> renderFile(String path, @Deprecated ProjectsRendererI projectsRendererI, Config config);

    default Perspective extendProjectLayout(Perspective layout, @Deprecated ProjectsRendererI projectsRendererI) {
        return layout;
    }

    /**
     * 
     * @param projectsRendererI
     * @return Set of paths relative to {@link Config#rootPath()}.
     */
    Set<Path> projectPaths(@Deprecated ProjectsRendererI projectsRendererI);

    /**
     *
     * @param projectsRendererI
     * @return Set of paths relative to {@link Config#rootPath()}.
     */
    default Set<Path> relevantProjectPaths(@Deprecated ProjectsRendererI projectsRendererI) {
        return projectPaths(projectsRendererI);
    }
}
