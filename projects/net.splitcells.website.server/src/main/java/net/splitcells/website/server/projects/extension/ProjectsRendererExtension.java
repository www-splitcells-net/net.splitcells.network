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
package net.splitcells.website.server.projects.extension;

import net.splitcells.dem.data.set.Set;
import net.splitcells.dem.lang.perspective.Perspective;
import net.splitcells.website.server.Config;
import net.splitcells.website.server.processor.BinaryMessage;
import net.splitcells.website.server.projects.ProjectsRendererI;

import java.nio.file.Path;
import java.util.Optional;

import static net.splitcells.dem.data.set.Sets.setOfUniques;

public interface ProjectsRendererExtension {
    Optional<BinaryMessage> renderFile(String path, @Deprecated ProjectsRendererI projectsRendererI, Config config);

    default Perspective extendProjectLayout(Perspective layout, @Deprecated ProjectsRendererI projectsRendererI) {
        return layout;
    }

    /**
     * The paths are relative to the website's {@link Config#rootPath()}.
     * 
     * @param projectsRendererI
     * @return Set of paths relative to {@link Config#rootPath()}.
     */
    Set<Path> projectPaths(@Deprecated ProjectsRendererI projectsRendererI);

    /**
     * The paths are relative to the website's {@link Config#rootPath()}.
     *
     * @param projectsRendererI
     * @return Set of paths relative to {@link Config#rootPath()}.
     */
    default Set<Path> relevantProjectPaths(@Deprecated ProjectsRendererI projectsRendererI) {
        return projectPaths(projectsRendererI);
    }
}
