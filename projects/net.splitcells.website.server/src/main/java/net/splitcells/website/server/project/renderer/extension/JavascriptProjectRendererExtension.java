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

import static net.splitcells.website.server.processor.BinaryMessage.binaryMessage;

public class JavascriptProjectRendererExtension implements ProjectRendererExtension {
    public static JavascriptProjectRendererExtension javascriptRenderer() {
        return new JavascriptProjectRendererExtension();
    }

    private JavascriptProjectRendererExtension() {

    }

    @Override
    public Optional<BinaryMessage> renderFile(String path, ProjectRenderer projectRenderer, Config config) {
        final var requestedFile = Path.of("src/main/js/")
                .resolve(path);
        if (projectRenderer.projectFileSystem().isFile(requestedFile)) {
            try {
                return Optional.of(binaryMessage(projectRenderer.projectFileSystem().readFileAsBytes(requestedFile)
                        , "text/javascript"));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return Optional.empty();
    }

    @Override
    public Set<Path> projectPaths(ProjectRenderer projectRenderer) {
        final var projectPaths = Sets.<Path>setOfUniques();
        final var sourceFolder = Path.of("src/main/js/");
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
