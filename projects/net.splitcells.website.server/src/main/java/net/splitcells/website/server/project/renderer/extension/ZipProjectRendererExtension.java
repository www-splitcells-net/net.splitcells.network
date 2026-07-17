/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.website.server.project.renderer.extension;

import net.splitcells.dem.data.set.Set;
import net.splitcells.dem.data.set.Sets;
import net.splitcells.dem.resource.ContentType;
import net.splitcells.website.server.Config;
import net.splitcells.website.server.processor.BinaryMessage;
import net.splitcells.website.server.project.ProjectRenderer;

import java.nio.file.Path;
import java.util.Optional;

import static net.splitcells.dem.resource.ContentType.HTML_TEXT;
import static net.splitcells.dem.resource.ContentType.ZIP;
import static net.splitcells.website.server.processor.BinaryMessage.binaryMessage;

/**
 * {@link ResourceProjectRendererExtension} is better suited for that, as it avoids code duplication and simplifies the folder structure.
 */
@Deprecated
public class ZipProjectRendererExtension implements ProjectRendererExtension {
    public static ZipProjectRendererExtension zipRenderer() {
        return new ZipProjectRendererExtension();
    }

    private ZipProjectRendererExtension() {

    }

    @Override
    public Optional<BinaryMessage> renderFile(String path, ProjectRenderer projectRenderer, Config config) {
        final var requestedFile = Path.of("src/main/zip/").resolve(path);
        if (projectRenderer.projectFileSystem().isFile(requestedFile)) {
            try {
                return Optional.of(binaryMessage(projectRenderer.projectFileSystem().readFileAsBytes(requestedFile)
                        , ZIP.codeName()));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return Optional.empty();
    }

    @Override
    public Set<Path> projectPaths(ProjectRenderer projectRenderer) {
        final var projectPaths = Sets.<Path>setOfUniques();
        final var sourceFolder = Path.of("src/main/zip/");
        if (projectRenderer.projectFileSystem().isDirectory(sourceFolder)) {
            try {
                projectRenderer.projectFileSystem().walkRecursively(sourceFolder)
                        .filter(projectRenderer.projectFileSystem()::isFile)
                        .map(file -> sourceFolder.relativize(file.getParent().resolve(file.getFileName().toString())))
                        .forEach(projectPaths::addAll);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return projectPaths;
    }
}
