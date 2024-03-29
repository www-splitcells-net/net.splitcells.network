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

import net.splitcells.dem.lang.annotations.JavaLegacyArtifact;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.stream.Stream;

import static java.nio.file.Files.createDirectories;
import static net.splitcells.dem.lang.perspective.PerspectiveI.perspective;
import static net.splitcells.dem.utils.ExecutionException.executionException;
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
@JavaLegacyArtifact
public class FileSystems implements FileSystem {
    public static FileSystem fileSystemOnLocalHost(Path rootPath) {
        if (!java.nio.file.Files.isDirectory(rootPath)) {
            throw executionException(perspective("Could not create file system API for given folder, because the folder does not exist:")
                    .withProperty("rootPath", rootPath.toString()));
        }
        return new FileSystems(rootPath);
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
    public FileSystem createDirectoryPath(String path) {
        try {
            createDirectories(Path.of(path));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        return this;
    }

    @Override
    public InputStream inputStream(Path path) {
        try {
            return java.nio.file.Files.newInputStream(rootPath.resolve(path));
        } catch (IOException e) {
            throw executionException(e);
        }
    }

    @Override
    public String readString(Path path) {
        try {
            return java.nio.file.Files.readString(rootPath.resolve(path));
        } catch (IOException e) {
            throw executionException("Could not read file: " + path, e);
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
            throw executionException(e);
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
            throw executionException(e);
        }
    }

    @Override
    public Stream<Path> walkRecursively(Path path) {
        try {
            return java.nio.file.Files.walk(rootPath.resolve(path)).map(rootPath::relativize);
        } catch (IOException e) {
            throw executionException(e);
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

    @Override
    public FileSystem subFileSystem(Path path) {
        return fileSystemOnLocalHost(this.rootPath.resolve(path));
    }

    @Override
    public boolean exists() {
        return java.nio.file.Files.isDirectory(rootPath);
    }
}