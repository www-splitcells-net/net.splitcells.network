/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.dem.resource;

import lombok.val;
import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.resource.communication.log.Logs;
import net.splitcells.dem.utils.ExecutionException;
import net.splitcells.dem.utils.StreamUtils;

import java.io.InputStream;
import java.nio.file.Path;
import java.util.stream.Stream;

import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.data.set.list.Lists.toList;
import static net.splitcells.dem.lang.tree.TreeI.tree;
import static net.splitcells.dem.resource.communication.log.Logs.logs;
import static net.splitcells.dem.utils.ExecutionException.execException;
import static net.splitcells.dem.utils.NotImplementedYet.notImplementedYet;

public class FileSystemUnionView implements FileSystemView {

    private static final String UNAMBIGUOUS_PATH = "Could find unambiguous file system match.";
    private static final String MATCHES = "matches";
    private static final String PATH = "matches";

    public static FileSystemUnionView fileSystemUnionView(boolean argStrictMode, List<FileSystemView> fileSystems) {
        return new FileSystemUnionView(argStrictMode, fileSystems);
    }

    public static FileSystemUnionView fileSystemUnionView(boolean argStrictMode, FileSystemView... fileSystems) {
        return new FileSystemUnionView(argStrictMode, list(fileSystems));
    }

    private final List<FileSystemView> fileSystems;
    private final boolean strictMode;
    private final Path basePath;

    private FileSystemUnionView(boolean argStrictMode, List<FileSystemView> fileSystemsArg) {
        this(argStrictMode, fileSystemsArg, java.nio.file.Paths.get("./"));
    }

    private FileSystemUnionView(boolean argStrictMode, List<FileSystemView> fileSystemsArg, Path argBasePath) {
        fileSystems = fileSystemsArg;
        strictMode = argStrictMode;
        basePath = argBasePath;
    }

    private FileSystemView getFileSystemWithExistingFile(Path path) {
        val adjustedPath = basePath.resolve(path);
        final var matches = fileSystems.stream()
                .filter(f -> f.isFile(adjustedPath))
                .collect(toList());
        if (matches.size() != 1) {
            val exception = execException(tree(UNAMBIGUOUS_PATH)
                    .withProperty(PATH, adjustedPath.toString())
                    .withProperty(MATCHES, matches.toString()));
            logs().warn(exception);
            if (strictMode) {
                throw exception;
            }
        }
        return matches.get(0);
    }

    @Override
    public InputStream inputStream(Path path) {
        return getFileSystemWithExistingFile(path).inputStream(basePath.resolve(path));
    }

    @Override
    public String readString(Path path) {
        return getFileSystemWithExistingFile(path).readString(basePath.resolve(path));
    }

    @Override
    public boolean exists() {
        final var matches = fileSystems.stream()
                .filter(FileSystemView::exists)
                .collect(toList());
        return matches.hasElements();
    }

    @Override
    public boolean isFile(Path path) {
        val adjustedPath = basePath.resolve(path);
        final var matches = fileSystems.stream()
                .map(f -> f.isFile(adjustedPath))
                .filter(f -> f)
                .collect(toList());
        if (matches.size() > 1) {
            val exception = execException(tree(UNAMBIGUOUS_PATH)
                    .withProperty(PATH, path.toString())
                    .withProperty(MATCHES, matches.toString()));
            logs().warn(exception);
            if (strictMode) {
                throw exception;
            }
        }
        return matches.hasElements();
    }

    @Override
    public boolean isDirectory(Path path) {
        val adjustedPath = basePath.resolve(path);
        final var matches = fileSystems.stream()
                .map(f -> f.isDirectory(adjustedPath))
                .filter(f -> f)
                .collect(toList());
        if (matches.size() > 1) {
            val exception = execException(tree(UNAMBIGUOUS_PATH)
                    .withProperty(PATH, path.toString())
                    .withProperty(MATCHES, matches.toString()));
            logs().warn(exception);
            if (strictMode) {
                throw exception;
            }
        }
        return matches.hasElements();
    }

    @Override
    public Stream<Path> walkRecursively() {
        final var walks = fileSystems.stream()
                .map(f -> f.walkRecursively(basePath))
                .collect(toList());
        return StreamUtils.concat(walks);
    }

    @Override
    public Stream<Path> walkRecursively(Path path) {
        val adjustedPath = basePath.resolve(path);
        final var walks = fileSystems.stream()
                .filter(f -> f.isDirectory(adjustedPath))
                .map(f -> f.walkRecursively(adjustedPath))
                .collect(toList());
        return StreamUtils.concat(walks);
    }

    @Override
    public byte[] readFileAsBytes(Path path) {
        val adjustedPath = basePath.resolve(path);
        return getFileSystemWithExistingFile(adjustedPath).readFileAsBytes(adjustedPath);
    }

    @Override
    public FileSystemView subFileSystemView(String path) {
        return new FileSystemUnionView(strictMode, fileSystems, basePath.resolve(path));
    }
}
