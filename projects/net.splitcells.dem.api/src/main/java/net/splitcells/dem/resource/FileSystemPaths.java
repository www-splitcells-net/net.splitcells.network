/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.dem.resource;

import java.nio.file.Path;

import static net.splitcells.dem.lang.tree.TreeI.tree;
import static net.splitcells.dem.utils.ExecutionException.execException;

/**
 * <p>Provides the containment checks, that every {@link FileSystemView} implementation applies to its path arguments.
 * A path given to a {@link FileSystemView} is always relative to the root of that {@link FileSystemView}.
 * It is not allowed to point outside of this root,
 * neither via an absolute path nor via {@code ..} segments.
 * Otherwise, paths provided by untrusted sources (i.e. URL paths of web requests)
 * could be used to read or write arbitrary files.</p>
 */
public final class FileSystemPaths {
    private FileSystemPaths() {
        throw execException();
    }

    /**
     * @param path This is the path relative to the root of a {@link FileSystemView}.
     * @return Whether the given path stays inside the root of the {@link FileSystemView}.
     */
    public static boolean isContainedPath(Path path) {
        if (path.isAbsolute() || path.getRoot() != null) {
            return false;
        }
        final var normalized = path.normalize();
        return normalized.getNameCount() == 0 || !"..".equals(normalized.getName(0).toString());
    }

    public static boolean isContainedPath(String path) {
        return isContainedPath(Path.of(path));
    }

    /**
     * @param path This is the path relative to the root of a {@link FileSystemView}.
     * @return Returns the given path unchanged, if it stays inside the root of the {@link FileSystemView}.
     */
    public static Path requireContainedPath(Path path) {
        if (!isContainedPath(path)) {
            throw execException(tree("The given path is not allowed to leave the root of the file system.")
                    .withProperty("path", path.toString()));
        }
        return path;
    }

    public static String requireContainedPath(String path) {
        requireContainedPath(Path.of(path));
        return path;
    }
}
