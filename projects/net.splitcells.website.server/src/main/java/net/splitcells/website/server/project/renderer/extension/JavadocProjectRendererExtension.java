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
import static net.splitcells.website.Formats.CSS;
import static net.splitcells.website.server.project.RenderingResult.renderingResult;

/**
 * Renders the Javadoc of projects at `{@link ProjectRenderer#projectFolder()}/javadoc/*`,
 * if a Javadoc build is located at `{@link ProjectRenderer#resourceRootPath2()}/target/site/apidocs`.
 */
public class JavadocProjectRendererExtension implements ProjectRendererExtension {
    private static final String RENDERED_JAVADOC_FOLDER = "javadoc";
    private static final String SOURCE_JAVADOC_FOLDER = "target/site/apidocs";

    public static JavadocProjectRendererExtension javadocReportRenderer() {
        return new JavadocProjectRendererExtension();
    }

    private JavadocProjectRendererExtension() {

    }

    @Override
    public Optional<RenderingResult> renderFile(String path, ProjectRenderer projectRenderer, Config config) {
        if (("/" + path).startsWith(projectRenderer.resourceRootPath2().resolve(RENDERED_JAVADOC_FOLDER).toString())) {
            final var requestedFile = projectRenderer
                    .projectFolder()
                    .resolve(SOURCE_JAVADOC_FOLDER)
                    .resolve(projectRenderer.resourceRootPath2().resolve(RENDERED_JAVADOC_FOLDER).relativize(Path.of("/" + path + "/")));
            if (is_file(requestedFile)) {
                final String format;
                if (path.endsWith(".css")) {
                    format = CSS.toString();
                } else {
                    format = HTML_TEXT.codeName();
                }
                return Optional.of(renderingResult(readFileAsBytes(requestedFile), format));
            }
        }
        return Optional.empty();
    }

    @Override
    public Set<Path> projectPaths(ProjectRenderer projectRenderer) {
        final var projectPaths = Sets.<Path>setOfUniques();
        final var sourceFolder = projectRenderer
                .projectFolder()
                .resolve(SOURCE_JAVADOC_FOLDER);
        // Last substring map converts the absolute path to relative one.
        if (isDirectory(sourceFolder)) {
            Files.walk_recursively(sourceFolder)
                    .filter(Files::is_file)
                    .map(file -> sourceFolder.relativize(file.getParent().resolve(file.getFileName().toString())))
                    .map(path -> projectRenderer.resourceRootPath2().resolve(RENDERED_JAVADOC_FOLDER).resolve(path))
                    .map(path -> Path.of(path.toString().substring(1)))
                    .forEach(projectPaths::addAll);
        }
        return projectPaths;
    }

    @Override
    public Set<Path> relevantProjectPaths(ProjectRenderer projectRenderer) {
        final var projectPaths = Sets.<Path>setOfUniques();
        final var sourceFolder = projectRenderer
                .projectFolder()
                .resolve(SOURCE_JAVADOC_FOLDER);
        if (isDirectory(sourceFolder)) {
            projectPaths.add(
                    Path.of(projectRenderer
                            .resourceRootPath2()
                            .resolve(RENDERED_JAVADOC_FOLDER)
                            .resolve("index.html")
                            .toString()
                            .substring(1)));
        }
        return projectPaths;
    }
}