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
package net.splitcells.website.server.project.renderer.extension;

import net.splitcells.dem.data.set.Set;
import net.splitcells.dem.data.set.Sets;
import net.splitcells.dem.resource.Files;
import net.splitcells.website.server.Config;
import net.splitcells.website.server.project.ProjectRenderer;
import net.splitcells.website.server.project.RenderingResult;

import java.nio.file.Path;
import java.util.Optional;

import static net.splitcells.dem.resource.ContentType.HTML_TEXT;
import static net.splitcells.dem.resource.Files.isDirectory;
import static net.splitcells.dem.resource.Files.is_file;
import static net.splitcells.dem.resource.Files.readFileAsBytes;
import static net.splitcells.website.server.project.RenderingResult.renderingResult;

public class CsvProjectRendererExtension implements ProjectRendererExtension {
    public static CsvProjectRendererExtension csvRenderer() {
        return new CsvProjectRendererExtension();
    }

    private CsvProjectRendererExtension() {

    }

    @Override
    public Optional<RenderingResult> renderFile(String path, ProjectRenderer projectRenderer, Config config) {
        final var requestedFile = projectRenderer
                .projectFolder()
                .resolve("src/main/csv/")
                .resolve(path);
        if (is_file(requestedFile)) {
            try {
                return Optional.of(renderingResult(readFileAsBytes(requestedFile), HTML_TEXT.codeName()));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return Optional.empty();
    }

    @Override
    public Set<Path> projectPaths(ProjectRenderer projectRenderer) {
        final var projectPaths = Sets.<Path>setOfUniques();
        final var sourceFolder = projectRenderer
                .projectFolder()
                .resolve("src/main/csv/");
        if (isDirectory(sourceFolder)) {
            try {
                Files.walk_recursively(sourceFolder)
                        .filter(Files::fileExists)
                        .map(file -> sourceFolder.relativize(file.getParent().resolve(file.getFileName().toString())))
                        .forEach(projectPaths::addAll);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return projectPaths;
    }
}
