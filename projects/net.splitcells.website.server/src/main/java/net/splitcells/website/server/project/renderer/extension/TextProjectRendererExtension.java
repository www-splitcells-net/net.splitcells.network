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
import net.splitcells.dem.lang.Xml;
import net.splitcells.dem.lang.namespace.NameSpaces;
import net.splitcells.dem.resource.Files;
import net.splitcells.dem.resource.Paths;
import net.splitcells.website.server.Config;
import net.splitcells.website.server.project.ProjectRenderer;
import net.splitcells.website.server.project.RenderingResult;

import java.nio.file.Path;
import java.util.Optional;

import static net.splitcells.dem.resource.ContentType.HTML_TEXT;
import static net.splitcells.website.server.project.RenderingResult.renderingResult;

/**
 * Projects the file tree located "src/main/txt/" of the project's folder.
 * The projected path's replaces the "txt" file suffix with "html".
 * All files need to end with ".txt".
 */
public class TextProjectRendererExtension implements ProjectRendererExtension {
    public static TextProjectRendererExtension textExtension() {
        return new TextProjectRendererExtension();
    }

    private TextProjectRendererExtension() {
    }

    @Override
    public Optional<RenderingResult> renderFile(String path, ProjectRenderer projectRenderer, Config config) {
        Optional<Path> fileToRender = Optional.empty();
        if (path.endsWith(".html")) {
            // TODO This should return the raw text file.
            final var textFile = projectRenderer
                    .projectFolder()
                    .resolve("src/main/txt")
                    .resolve(path.substring(0, path.lastIndexOf(".html")) + ".txt");
            if (Files.is_file(textFile)) {
                fileToRender = Optional.of(textFile);
            }
        } else {
            final var textFile = projectRenderer
                    .projectFolder()
                    .resolve("src/main/txt")
                    .resolve(path);
            if (Files.is_file(textFile)) {
                fileToRender = Optional.of(textFile);
            }
        }
        if (fileToRender.isPresent()) {
            final var content = Xml.rElement(NameSpaces.NATURAL, "text");
            final var metaElement = Xml.rElement(NameSpaces.SEW, "meta");
            final var pathElement = Xml.rElement(NameSpaces.SEW, "path");
            pathElement.appendChild(Xml.textNode(path));
            metaElement.appendChild(pathElement);
            content.appendChild(metaElement);
            content.appendChild(Xml.textNode(Paths.readString(fileToRender.get())));
            return Optional.of(renderingResult(projectRenderer.renderRawXml(Xml.toPrettyString(content), config).get()
                    , HTML_TEXT.codeName()));
        }
        return Optional.empty();
    }

    @Override
    public Set<Path> projectPaths(ProjectRenderer projectRenderer) {
        final var projectPaths = Sets.<Path>setOfUniques();
        final var sourceFolder = projectRenderer.projectFolder().resolve("src/main").resolve("txt");
        if (Files.isDirectory(sourceFolder)) {
                Files.walk_recursively(sourceFolder)
                        .filter(Files::is_file)
                        .map(file -> sourceFolder.relativize(
                                file.getParent()
                                        .resolve(net.splitcells.dem.resource.Paths.removeFileSuffix
                                                (file.getFileName().toString()) + ".html")))
                        .forEach(projectPaths::addAll);
                Files.walk_recursively(sourceFolder)
                        .filter(Files::is_file)
                        .map(file -> sourceFolder.relativize(file.getParent().resolve(file.getFileName().toString())))
                        .forEach(projectPaths::addAll);
        }
        return projectPaths;
    }
}
