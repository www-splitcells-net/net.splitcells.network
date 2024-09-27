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
package net.splitcells.website.server.projects;

import net.splitcells.dem.resource.FileSystemView;
import net.splitcells.dem.resource.Paths;
import net.splitcells.dem.utils.BinaryUtils;

import java.io.InputStream;
import java.nio.file.Path;
import java.util.stream.Stream;

import static net.splitcells.dem.lang.tree.TreeI.perspective;
import static net.splitcells.dem.utils.ExecutionException.executionException;
import static net.splitcells.dem.utils.NotImplementedYet.notImplementedYet;

public class ProjectsRendererSourceCodeFileSystem implements FileSystemView {
    private static final String SOURCE_CODE_FOLDER = "source-code/";

    public static ProjectsRendererSourceCodeFileSystem projectsRendererSourceCodeFileSystem(ProjectsRenderer projectsRenderer) {
        return new ProjectsRendererSourceCodeFileSystem(projectsRenderer);
    }

    private final ProjectsRenderer projectsRenderer;

    private ProjectsRendererSourceCodeFileSystem(ProjectsRenderer projectsRendererArg) {
        projectsRenderer = projectsRendererArg;
    }

    @Override
    public InputStream inputStream(Path path) {
        final var pathStr = Paths.toString(path);
        if (pathStr.startsWith(SOURCE_CODE_FOLDER)) {
            return BinaryUtils.binaryInputStream(projectsRenderer.sourceCode(pathStr.substring(SOURCE_CODE_FOLDER.length()))
                    .orElseThrow()
                    .getContent());
        }
        throw executionException(perspective("Unknown root folder.")
                .withProperty("path", pathStr)
                .withProperty("projects renderer", projectsRenderer.toString()));
    }

    @Override
    public String readString(Path path) {
        throw notImplementedYet();
    }

    @Override
    public boolean exists() {
        throw notImplementedYet();
    }

    @Override
    public boolean isFile(Path path) {
        throw notImplementedYet();
    }

    @Override
    public boolean isDirectory(Path path) {
        throw notImplementedYet();
    }

    @Override
    public Stream<Path> walkRecursively() {
        throw notImplementedYet();
    }

    @Override
    public Stream<Path> walkRecursively(Path path) {
        throw notImplementedYet();
    }

    @Override
    public byte[] readFileAsBytes(Path path) {
        throw notImplementedYet();
    }

    @Override
    public FileSystemView subFileSystemView(String path) {
        throw notImplementedYet();
    }
}
