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
package net.splitcells.website.server.project.renderer.extension.commonmark;

import net.splitcells.dem.data.set.Set;
import net.splitcells.dem.data.set.Sets;
import net.splitcells.website.server.project.ProjectRenderer;
import net.splitcells.website.server.project.RenderingResult;
import net.splitcells.website.server.project.renderer.extension.ProjectRendererExtension;
import net.splitcells.website.server.projects.ProjectsRenderer;

import java.nio.file.Path;
import java.util.Optional;

import static net.splitcells.dem.data.set.Sets.setOfUniques;
import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.resource.ContentType.HTML_TEXT;
import static net.splitcells.website.server.project.RenderingResult.renderingResult;

public class CommonMarkProjectRendererExtension implements ProjectRendererExtension {
    public static CommonMarkProjectRendererExtension commonMarkExtension() {
        return new CommonMarkProjectRendererExtension();
    }

    private final CommonMarkIntegration renderer = CommonMarkIntegration.commonMarkIntegration();

    private CommonMarkProjectRendererExtension() {

    }

    @Override
    public Optional<RenderingResult> renderFile(String path, ProjectsRenderer projectsRenderer, ProjectRenderer projectRenderer) {
        if (path.endsWith(".html")) {
            final var commonMarkFile = Path.of("src/main/md")
                    .resolve(path.substring(0, path.lastIndexOf(".html")) + ".md");
            if (projectRenderer.projectFileSystem().isFile(commonMarkFile)) {
                final var pathContent = projectRenderer.projectFileSystem().readString(commonMarkFile);
                return Optional.of(renderingResult(renderer.render(pathContent, projectRenderer, path, projectsRenderer.config(), projectsRenderer)
                        , HTML_TEXT.codeName()));
            }
        }
        return Optional.empty();
    }

    @Override
    public Set<Path> projectPaths(ProjectRenderer projectRenderer) {
        final var projectPaths = Sets.<Path>setOfUniques();
        final var sourceFolder = Path.of("src/main/md");
        if (projectRenderer.projectFileSystem().isDirectory(sourceFolder)) {
            projectRenderer.projectFileSystem().walkRecursively(sourceFolder)
                    .filter(projectRenderer.projectFileSystem()::isFile)
                    .map(file -> sourceFolder.relativize(
                            file.getParent()
                                    .resolve(net.splitcells.dem.resource.Paths.removeFileSuffix
                                            (file.getFileName().toString()) + ".html")))
                    .forEach(projectPaths::addAll);
        }
        return projectPaths;
    }

    @Override
    public Set<Path> relevantProjectPaths(ProjectRenderer projectRenderer) {
        return projectPaths(projectRenderer);
    }
}
