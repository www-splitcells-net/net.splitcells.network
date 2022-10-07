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
package net.splitcells.website.server.project.renderer.extension.commonmark;

import net.splitcells.dem.data.set.Set;
import net.splitcells.dem.data.set.Sets;
import net.splitcells.dem.resource.Files;
import net.splitcells.website.server.Config;
import net.splitcells.website.server.project.ProjectRenderer;
import net.splitcells.website.server.project.RenderingResult;
import net.splitcells.website.server.project.renderer.extension.ProjectRendererExtension;

import java.nio.file.Path;
import java.util.Optional;

import static net.splitcells.dem.data.set.Sets.setOfUniques;
import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.resource.ContentType.HTML_TEXT;
import static net.splitcells.dem.resource.Files.is_file;
import static net.splitcells.dem.resource.Paths.readString;
import static net.splitcells.website.server.project.RenderingResult.renderingResult;

public class CommonMarkProjectRendererExtension implements ProjectRendererExtension {
    public static CommonMarkProjectRendererExtension commonMarkExtension() {
        return new CommonMarkProjectRendererExtension();
    }

    private final CommonMarkIntegration renderer = CommonMarkIntegration.commonMarkIntegration();

    private CommonMarkProjectRendererExtension() {

    }

    @Override
    public Optional<RenderingResult> renderFile(String path, ProjectRenderer projectRenderer, Config config) {
        if (path.endsWith(".html")) {
            final var commonMarkFile = projectRenderer.projectFolder().resolve("src/main").resolve("md")
                    .resolve(path.substring(0, path.lastIndexOf(".html")) + ".md");
            if (is_file(commonMarkFile)) {
                final var pathContent = readString(commonMarkFile);
                return Optional.of(renderingResult(renderer.render(pathContent, projectRenderer, path, config)
                        , HTML_TEXT.codeName()));
            }
        }
        return Optional.empty();
    }

    @Override
    public Set<Path> projectPaths(ProjectRenderer projectRenderer) {
        final var projectPaths = Sets.<Path>setOfUniques();
        final var sourceFolder = projectRenderer.projectFolder().resolve("src/main").resolve("md");
        if (Files.isDirectory(sourceFolder)) {
            Files.walk_recursively(sourceFolder)
                    .filter(Files::is_file)
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
