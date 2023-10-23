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
package net.splitcells.website.server.project.renderer.extension;

import net.splitcells.dem.data.set.Set;
import net.splitcells.dem.data.set.Sets;
import net.splitcells.website.server.Config;
import net.splitcells.website.server.project.ProjectRenderer;
import net.splitcells.website.server.processor.BinaryMessage;

import java.nio.file.Path;
import java.util.Optional;

import static net.splitcells.dem.resource.ContentType.HTML_TEXT;
import static net.splitcells.website.Formats.CSS;
import static net.splitcells.website.server.processor.BinaryMessage.binaryMessage;

/**
 * Renders the Javadoc of projects at `{@link ProjectRenderer#projectFileSystem()}/javadoc/*`,
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
    public Optional<BinaryMessage> renderFile(String path, ProjectRenderer projectRenderer, Config config) {
        if (("/" + path).startsWith(projectRenderer.resourceRootPath2().resolve(RENDERED_JAVADOC_FOLDER).toString())) {
            final var requestedFile = Path.of(SOURCE_JAVADOC_FOLDER)
                    .resolve(projectRenderer
                            .resourceRootPath2()
                            .resolve(RENDERED_JAVADOC_FOLDER)
                            .relativize(Path.of("/" + path + "/")));
            if (projectRenderer.projectFileSystem().isFile(requestedFile)) {
                final String format;
                if (path.endsWith(".css")) {
                    format = CSS.toString();
                } else {
                    format = HTML_TEXT.codeName();
                }
                return Optional.of(binaryMessage(projectRenderer.projectFileSystem().readFileAsBytes(requestedFile)
                        , format));
            }
        }
        return Optional.empty();
    }

    @Override
    public Set<Path> projectPaths(ProjectRenderer projectRenderer) {
        final var projectPaths = Sets.<Path>setOfUniques();
        final var sourceFolder = Path.of(SOURCE_JAVADOC_FOLDER);
        // Last substring map converts the absolute path to relative one.
        if (projectRenderer.projectFileSystem().isDirectory(sourceFolder)) {
            projectRenderer.projectFileSystem().walkRecursively(sourceFolder)
                    .filter(projectRenderer.projectFileSystem()::isFile)
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
        final var sourceFolder = Path.of(SOURCE_JAVADOC_FOLDER);
        if (projectRenderer.projectFileSystem().isDirectory(sourceFolder)) {
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