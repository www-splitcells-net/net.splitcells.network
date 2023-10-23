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
import static net.splitcells.website.server.processor.BinaryMessage.binaryMessage;

public class JacocoReportProjectRendererExtension implements ProjectRendererExtension {
    public static JacocoReportProjectRendererExtension jacocoReportRenderer() {
        return new JacocoReportProjectRendererExtension();
    }

    private JacocoReportProjectRendererExtension() {

    }

    @Override
    public Optional<BinaryMessage> renderFile(String path, ProjectRenderer projectRenderer, Config config) {
        if (("/" + path).startsWith(projectRenderer.resourceRootPath2().resolve("jacoco-report").toString())) {
            final var requestedFile = Path.of("target/site/jacoco/")
                    .resolve(projectRenderer.resourceRootPath2().resolve("jacoco-report")
                            .relativize(Path.of("/" + path + "/")));
            if (projectRenderer.projectFileSystem().isFile(requestedFile)) {
                try {
                    return Optional.of(
                            binaryMessage(projectRenderer.projectFileSystem().readFileAsBytes(requestedFile)
                                    , HTML_TEXT.codeName()));
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public Set<Path> projectPaths(ProjectRenderer projectRenderer) {
        final var projectPaths = Sets.<Path>setOfUniques();
        final var sourceFolder = Path.of("target/site/jacoco/");
        if (projectRenderer.projectFileSystem().isDirectory(sourceFolder)) {
            // Last substring map converts the absolute path to relative one.
            projectRenderer.projectFileSystem().walkRecursively(sourceFolder)
                    .filter(projectRenderer.projectFileSystem()::isFile)
                    .map(file -> sourceFolder.relativize(file.getParent().resolve(file.getFileName().toString())))
                    .map(path -> projectRenderer.resourceRootPath2().resolve("jacoco-report").resolve(path))
                    .map(path -> Path.of(path.toString().substring(1)))
                    .forEach(projectPaths::addAll);
        }
        return projectPaths;
    }
}