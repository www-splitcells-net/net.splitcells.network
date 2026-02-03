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
package net.splitcells.dem.resource;

import net.splitcells.dem.environment.config.ProgramName;
import net.splitcells.dem.lang.annotations.JavaLegacy;
import net.splitcells.dem.utils.ExecutionException;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.util.Optional;
import java.util.stream.Stream;

import static java.nio.file.Files.createDirectories;
import static net.splitcells.dem.Dem.configValue;
import static net.splitcells.dem.lang.tree.TreeI.tree;
import static net.splitcells.dem.utils.ExecutionException.execException;
import static net.splitcells.dem.utils.NotImplementedYet.notImplementedYet;

/**
 * <p>TODO Move all file access instances specific to a computer to this class.</p>
 * <p>TODO IDEA Disallow any direct access to the hosts local file system in core source code.
 * Even access over this interface should not be allowed.
 * Instead, access to the local file system should only be injected and only limited to things,
 * that are really needed.
 * Otherwise, it will be hard to avoid random access to the local file system,
 * which makes it hard to ensure the programs portability.
 * In other words, provide {@link FileSystems} instances to the local file system of the operation system
 * via configuration and to via direct manual instance creation.
 * For this something like a registry is required,
 * where one can list such instances
 * Maybe {@link net.splitcells.dem.environment.config.framework.Configuration} can be used for this.
 * This does not apply to code, that only provides integration to the operation system.</p>
 */
@JavaLegacy
public class FileSystems implements FileSystem {
    public static FileSystem fileSystemOnLocalHost(Path rootPath) {
        if (!java.nio.file.Files.isDirectory(rootPath)) {
            throw ExecutionException.execException(tree("Could not create file system API for given folder, because the folder does not exist:")
                    .withProperty("rootPath", rootPath.toString()));
        }
        return new FileSystems(rootPath);
    }

    public static FileSystemResource temporaryFileSystem() {
        try {
            final var baseFolder = java.nio.file.Files.createTempDirectory(configValue(ProgramName.class));
            Runtime.getRuntime().addShutdownHook(new Thread(() -> Files.deleteDirectory(baseFolder)));
            final var baseFileSystem = fileSystemOnLocalHost(baseFolder);
            return new FileSystemResource() {

                @Override
                public void close() {
                    Files.deleteDirectory(baseFolder);
                }

                @Override
                public FileSystem writeToFile(Path path, byte[] content) {
                    return baseFileSystem.writeToFile(path, content);
                }

                @Override
                public FileSystem appendToFile(Path path, byte[] content) {
                    return baseFileSystem.appendToFile(path, content);
                }

                @Override
                public FileSystem subFileSystem(Path path) {
                    return baseFileSystem.subFileSystem(path);
                }

                @Override
                public InputStream inputStream(Path path) {
                    return baseFileSystem.inputStream(path);
                }

                @Override
                public String readString(Path path) {
                    return baseFileSystem.readString(path);
                }

                @Override
                public boolean exists() {
                    return baseFileSystem.exists();
                }

                @Override
                public boolean isFile(Path path) {
                    return baseFileSystem.isFile(path);
                }

                @Override
                public boolean isDirectory(Path path) {
                    return baseFileSystem.isDirectory(path);
                }

                @Override
                public Stream<Path> walkRecursively() {
                    return baseFileSystem.walkRecursively();
                }

                @Override
                public Stream<Path> walkRecursively(Path path) {
                    return baseFileSystem.walkRecursively(path);
                }

                @Override
                public byte[] readFileAsBytes(Path path) {
                    return baseFileSystem.readFileAsBytes(path);
                }

                @Override
                public FileSystemView subFileSystemView(String path) {
                    return baseFileSystem.subFileSystemView(path);
                }

                @Override
                public FileSystem createDirectoryPath(String path) {
                    baseFileSystem.createDirectoryPath(path);
                    return this;
                }

                @Override
                public FileSystem delete(String path) {
                    baseFileSystem.delete(path);
                    return this;
                }
            };
        } catch (IOException e) {
            throw execException(e);
        }
    }

    public static FileSystem usersStateFiles() {
        return fileSystemOnLocalHost(Files.usersStateFiles());
    }


    public static FileSystem userHome() {
        return fileSystemOnLocalHost(Paths.userHome());
    }

    private final Path rootPath;

    private FileSystems(Path rootPath) {
        this.rootPath = rootPath;
    }

    @Override
    public FileSystem copyFileFromTo(Trail from, Trail to) {
        try {
            java.nio.file.Files.copy(from.javaLegacyPath(), to.javaLegacyPath());
        } catch (IOException e) {
            throw execException(e);
        }
        return this;
    }

    @Override
    public FileSystem createDirectoryPath(String path) {
        try {
            createDirectories(rootPath.resolve(path));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        return this;
    }

    @Override
    public FileSystem delete(String path) {
        try {
            java.nio.file.Files.delete(rootPath.resolve(path));
        } catch (IOException e) {
            throw execException(e);
        }
        return this;
    }

    @Override
    public InputStream inputStream(Path path) {
        try {
            return java.nio.file.Files.newInputStream(rootPath.resolve(path));
        } catch (IOException e) {
            throw execException(e);
        }
    }

    @Override
    public String readString(Path path) {
        try {
            return java.nio.file.Files.readString(rootPath.resolve(path));
        } catch (IOException e) {
            throw ExecutionException.execException("Could not read file: " + path, e);
        }
    }

    @Override
    public FileSystem writeToFile(Path path, byte[] content) {
        final var targetPath = rootPath.resolve(path);
        if (path.getParent() != null) {
            Files.createDirectory(targetPath.getParent());
        }
        try (final var writer = new FileOutputStream(targetPath.toFile())) {
            writer.write(content);
        } catch (Exception e) {
            throw execException(e);
        }
        return this;
    }

    @Override
    public FileSystem appendToFile(Path path, byte[] content) {
        final var targetPath = rootPath.resolve(path);
        if (path.getParent() != null) {
            Files.createDirectory(targetPath.getParent());
        }
        try (final var writer = new FileOutputStream(targetPath.toFile(), true)) {
            writer.write(content);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return this;
    }

    @Override
    public boolean isFile(Path path) {
        return java.nio.file.Files.isRegularFile(rootPath.resolve(path));
    }

    @Override
    public boolean isDirectory(Path path) {
        return java.nio.file.Files.isDirectory(rootPath.resolve(path));
    }

    @Override
    public Stream<Path> walkRecursively() {
        try {
            return java.nio.file.Files.walk(rootPath).map(rootPath::relativize);
        } catch (IOException e) {
            throw execException(e);
        }
    }

    @Override
    public Stream<Path> walkRecursively(Path path) {
        try {
            return java.nio.file.Files.walk(rootPath.resolve(path)).map(rootPath::relativize);
        } catch (IOException e) {
            throw execException(e);
        }
    }

    @Override
    public byte[] readFileAsBytes(Path path) {
        try {
            return java.nio.file.Files.readAllBytes(rootPath.resolve(path));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public FileSystemView subFileSystemView(String path) {
        return subFileSystem(Path.of(path));
    }

    /**
     * This method does not check, if the sub file system actually {@link #exists()}.
     * The reason for that is, that expecting a {@link FileSystem} to {@link #exists()} by default,
     * has no advantages, as any file access later on requires checking,
     * if the corresponding folder or file exists.
     * This already implicitly checks, if the sub file system {@link #exists()}.
     *
     * @param path
     * @return
     */
    @Override
    public FileSystem subFileSystem(Path path) {
        return new FileSystems(rootPath.resolve(path));
    }

    @Override
    public boolean exists() {
        return java.nio.file.Files.isDirectory(rootPath);
    }

    @Override
    public Optional<Path> javaLegacyPath(Path path) {
        if (path.isAbsolute()) {
            throw ExecutionException.execException(tree("The given path is not allowed to be absolute.")
                    .withProperty("path", path.toString()));
        }
        return Optional.of(rootPath.resolve(path));
    }

    @Override
    public Optional<Path> javaLegacyPath() {
        return Optional.of(rootPath);
    }
}