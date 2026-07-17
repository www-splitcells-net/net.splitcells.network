/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.website.server.project.renderer.extension;

import net.splitcells.dem.data.set.Set;
import net.splitcells.dem.data.set.Sets;
import net.splitcells.website.server.Config;
import net.splitcells.website.server.project.ProjectRenderer;
import net.splitcells.website.server.processor.BinaryMessage;

import java.nio.file.Path;
import java.util.Optional;

import static net.splitcells.website.server.processor.BinaryMessage.binaryMessage;

public class SvgProjectRendererExtension implements ProjectRendererExtension {
    public static SvgProjectRendererExtension svgRenderer() {
        return new SvgProjectRendererExtension();
    }

    private SvgProjectRendererExtension() {

    }

    @Override
    public Optional<BinaryMessage> renderFile(String path, ProjectRenderer projectRenderer, Config config) {
        if (path.endsWith(".svg")) {
            final var requestedFile = Path.of("src/main/svg/").resolve(path);
            if (projectRenderer.projectFileSystem().isFile(requestedFile)) {
                return Optional.of(binaryMessage(projectRenderer.projectFileSystem().readFileAsBytes(requestedFile)
                        , "image/svg+xml"));
            }
        }
        return Optional.empty();
    }

    @Override
    public Set<Path> projectPaths(ProjectRenderer projectRenderer) {
        final var projectPaths = Sets.<Path>setOfUniques();
        final var htmlPath = Path.of("src/main/svg/");
        if (projectRenderer.projectFileSystem().isDirectory(htmlPath)) {
            projectRenderer.projectFileSystem().walkRecursively(htmlPath)
                    .filter(p -> projectRenderer.projectFileSystem().isFile(p))
                    .map(htmlPath::relativize)
                    .forEach(projectPaths::addAll);
        }
        return projectPaths;
    }
}
