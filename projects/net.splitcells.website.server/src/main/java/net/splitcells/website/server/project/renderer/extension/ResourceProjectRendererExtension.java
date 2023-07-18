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
import net.splitcells.dem.resource.ContentType;
import net.splitcells.website.server.Config;
import net.splitcells.website.server.project.ProjectRenderer;
import net.splitcells.website.server.project.RenderingResult;

import java.nio.file.Path;
import java.util.Optional;

import static net.splitcells.dem.resource.ContentType.HTML_TEXT;
import static net.splitcells.website.server.project.RenderingResult.renderingResult;

/**
 * Projects the file tree located "src/main/resources/html/" of the project's folder.
 * <p>
 * TODO Split resources into actual HTML documents and other binary data.
 */
public class ResourceProjectRendererExtension implements ProjectRendererExtension {
    public static ResourceProjectRendererExtension resourceRenderer() {
        return new ResourceProjectRendererExtension();
    }

    private ResourceProjectRendererExtension() {

    }

    @Override
    public Optional<RenderingResult> renderFile(String path, ProjectRenderer projectRenderer, Config config) {
        final var requestedFile = Path.of("src/main/resources/html/").resolve(path);
        if (projectRenderer.projectFileSystem().isFile(requestedFile)) {
            final String format;
            if (path.endsWith(".svg")) {
                format = "image/svg+xml";
            } else if (path.endsWith(".jpg")) {
                format = "image/jpeg";
            } else if (path.endsWith(".css")) {
                format = ContentType.CSS.codeName();
            } else if (path.endsWith(".js")) {
                format = ContentType.JS.codeName();
            } else {
                format = HTML_TEXT.codeName();
            }
            return Optional.of(renderingResult(projectRenderer.projectFileSystem().readFileAsBytes(requestedFile)
                    , format));
        }
        return Optional.empty();
    }

    @Override
    public Set<Path> projectPaths(ProjectRenderer projectRenderer) {
        final var projectPaths = Sets.<Path>setOfUniques();
        final var htmlPath = Path.of("src/main/resources/html/");
        if (projectRenderer.projectFileSystem().isDirectory(htmlPath)) {
            projectRenderer.projectFileSystem().walkRecursively(htmlPath)
                    .filter(p -> projectRenderer.projectFileSystem().isFile(p))
                    .map(htmlPath::relativize)
                    .forEach(projectPaths::addAll);
        }
        return projectPaths;
    }
}
