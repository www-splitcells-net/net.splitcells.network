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

import com.google.common.jimfs.Jimfs;
import net.splitcells.dem.lang.annotations.JavaLegacyArtifact;
import net.splitcells.dem.utils.ExecutionException;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.stream.Stream;

import static net.splitcells.dem.utils.ExecutionException.execException;
import static net.splitcells.dem.utils.NotImplementedYet.notImplementedYet;

@JavaLegacyArtifact
public class FileSystemViaMemory implements FileSystem {

    public static FileSystem fileSystemViaMemory() {
        return new FileSystemViaMemory();
    }

    private final java.nio.file.FileSystem backend;

    private FileSystemViaMemory() {
        backend = Jimfs.newFileSystem();
    }

    @Override
    public FileSystem writeToFile(Path path, byte[] content) {
        try {
            Files.write(backend.getPath(path.toString()), content);
        } catch (IOException e) {
            throw execException(e);
        }
        return this;
    }

    @Override
    public FileSystem appendToFile(Path path, byte[] content) {
        try {
            Files.write(backend.getPath(path.toString()), content, StandardOpenOption.APPEND);
        } catch (IOException e) {
            throw execException(e);
        }
        return this;
    }

    @Override
    public FileSystem subFileSystem(Path path) {
        throw notImplementedYet();
    }

    @Override
    public InputStream inputStream(Path path) {
        try {
            return Files.newInputStream(backend.getPath(path.toString()));
        } catch (IOException e) {
            throw execException(e);
        }
    }

    @Override
    public String readString(Path path) {
        try {
            return java.nio.file.Files.readString(backend.getPath(path.toString()));
        } catch (IOException e) {
            throw ExecutionException.execException("Could not read file: " + path, e);
        }
    }

    @Override
    public boolean exists() {
        return true;
    }

    @Override
    public boolean isFile(Path path) {
        return java.nio.file.Files.isRegularFile(backend.getPath(path.toString()));
    }

    @Override
    public boolean isDirectory(Path path) {
        return java.nio.file.Files.isDirectory(backend.getPath(path.toString()));
    }

    @Override
    public Stream<Path> walkRecursively() {
        try {
            return java.nio.file.Files.walk(backend.getPath("./"));
        } catch (IOException e) {
            throw execException(e);
        }
    }

    @Override
    public Stream<Path> walkRecursively(Path path) {
        try {
            return java.nio.file.Files.walk(backend.getPath(path.toString()));
        } catch (IOException e) {
            throw execException(e);
        }
    }

    @Override
    public byte[] readFileAsBytes(Path path) {
        try {
            return java.nio.file.Files.readAllBytes(backend.getPath(path.toString()));
        } catch (IOException e) {
            throw execException(e);
        }
    }

    @Override
    public FileSystemView subFileSystemView(String path) {
        throw notImplementedYet();
    }

    @Override
    public FileSystem createDirectoryPath(String path) {
        try {
            Files.createDirectories(backend.getPath(path));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return this;
    }

    @Override
    public FileSystem delete(String path) {
        try {
            Files.delete(backend.getPath(path));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return this;
    }
}
