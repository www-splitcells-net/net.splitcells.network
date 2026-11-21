/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.dem.resource;

import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;
import net.splitcells.dem.lang.annotations.JavaLegacy;
import net.splitcells.dem.utils.ExecutionException;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Optional;
import java.util.stream.Stream;

import static net.splitcells.dem.lang.tree.TreeI.tree;
import static net.splitcells.dem.utils.ExecutionException.execException;

@JavaLegacy
public class FileSystemViaMemory implements FileSystem {

    public static FileSystem fileSystemViaMemory() {
        return new FileSystemViaMemory();
    }

    private final java.nio.file.FileSystem backend;
    /**
     * This is the normalized absolute root folder of this file system inside {@link #backend}.
     * All paths given as arguments are relative to this folder and are not allowed to leave it.
     */
    private final Path base;

    private FileSystemViaMemory() {
        this(Jimfs.newFileSystem(Configuration.unix()
                .toBuilder()
                .setWorkingDirectory("/")
                .build()));
    }

    private FileSystemViaMemory(java.nio.file.FileSystem argBackend) {
        this(argBackend, argBackend.getPath("/"));
    }

    private FileSystemViaMemory(java.nio.file.FileSystem argBackend, Path argBase) {
        backend = argBackend;
        base = argBase.toAbsolutePath().normalize();
    }

    /**
     * Resolves the given path relative to {@link #base} and
     * ensures, that the result is located inside {@link #base}.
     * Absolute paths are not accepted, as all paths are relative to {@link #base}.
     *
     * @param path This is a path relative to {@link #base}.
     * @return The normalized absolute path inside {@link #backend}, if it is located inside {@link #base}.
     */
    private Optional<Path> resolveInside(Path path) {
        if (path.isAbsolute()) {
            throw execException(tree("Path has to be a child of the base path. Absolute paths are not allowed.")
                    .withProperty("base path", base.toString())
                    .withProperty("path", path.toString()));
        }
        final var resolved = base.resolve(path.toString()).normalize();
        if (!resolved.startsWith(base)) {
            throw execException(tree("Path has to be a child of the base path. Absolute paths are not allowed.")
                    .withProperty("base path", base.toString())
                    .withProperty("path", path.toString()));
        }
        return Optional.of(resolved);
    }

    private Path requireInside(Path path) {
        return resolveInside(path).orElseThrow(() -> execException(tree("The given path is not located inside the base path.")
                .withProperty("path", path.toString())
                .withProperty("base path", base.toString())));
    }

    private Path requireInside(String path) {
        return requireInside(Path.of(path));
    }

    @Override
    public FileSystem writeToFile(Path path, byte[] content) {
        try {
            Files.write(requireInside(path), content);
        } catch (IOException e) {
            throw execException(e);
        }
        return this;
    }

    @Override
    public FileSystem appendToFile(Path path, byte[] content) {
        try {
            Files.write(requireInside(path)
                    , content
                    , StandardOpenOption.APPEND
                    , StandardOpenOption.CREATE);
        } catch (IOException e) {
            throw execException(e);
        }
        return this;
    }

    @Override
    public FileSystem subFileSystem(Path path) {
        return new FileSystemViaMemory(backend, requireInside(path));
    }

    @Override
    public InputStream inputStream(Path path) {
        try {
            return Files.newInputStream(requireInside(path));
        } catch (IOException e) {
            throw execException(e);
        }
    }

    @Override
    public String readString(Path path) {
        try {
            return java.nio.file.Files.readString(requireInside(path));
        } catch (IOException e) {
            throw ExecutionException.execException("Could not read file: " + path, e);
        }
    }

    @Override
    public boolean exists() {
        return Files.exists(base);
    }

    @Override
    public boolean isFile(Path path) {
        return resolveInside(path).map(java.nio.file.Files::isRegularFile).orElse(false);
    }

    @Override
    public boolean isDirectory(Path path) {
        return resolveInside(path).map(p -> java.nio.file.Files.isDirectory(p)).orElse(false);
    }

    @Override
    public Stream<Path> walkRecursively() {
        return walkRecursively(".");
    }

    @Override
    public Stream<Path> walkRecursively(Path path) {
        try {
            return java.nio.file.Files.walk(requireInside(path))
                    .map(p -> {
                        if (p.startsWith("/")) {
                            return Path.of(p.toString().substring(1));
                        }
                        return p;
                    }).filter(p -> !p.toString().isEmpty());
        } catch (IOException e) {
            throw execException(e);
        }
    }

    @Override
    public byte[] readFileAsBytes(Path path) {
        try {
            return java.nio.file.Files.readAllBytes(requireInside(path));
        } catch (IOException e) {
            throw execException(e);
        }
    }

    @Override
    public FileSystem createDirectoryPath(String path) {
        try {
            Files.createDirectories(requireInside(path));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return this;
    }

    @Override
    public FileSystem delete(String path) {
        try {
            Files.delete(requireInside(path));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return this;
    }
}
