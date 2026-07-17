/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.website.server.projects;

import net.splitcells.dem.resource.FileSystemView;
import net.splitcells.dem.resource.Paths;
import net.splitcells.dem.utils.BinaryUtils;
import net.splitcells.dem.utils.ExecutionException;

import java.io.InputStream;
import java.nio.file.Path;
import java.util.stream.Stream;

import static net.splitcells.dem.lang.tree.TreeI.tree;
import static net.splitcells.dem.utils.ExecutionException.execException;
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
        throw ExecutionException.execException(tree("Unknown root folder.")
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
