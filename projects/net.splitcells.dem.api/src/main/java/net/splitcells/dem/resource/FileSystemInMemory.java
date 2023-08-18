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

import java.io.InputStream;
import java.nio.file.Path;
import java.util.stream.Stream;

import static net.splitcells.dem.resource.communication.log.Domsole.domsole;
import static net.splitcells.dem.utils.NotImplementedYet.notImplementedYet;

public class FileSystemInMemory implements FileSystem {
    public static FileSystem fileSystemInMemory() {
        return new FileSystemInMemory();
    }

    private FileSystemInMemory() {

    }

    @Override
    public FileSystem writeToFile(Path path, byte[] content) {
        domsole().appendUnimplementedWarning(FileSystemInMemory.class);
        return this;
    }

    @Override
    public FileSystem subFileSystem(Path path) {
        domsole().appendUnimplementedWarning(FileSystemInMemory.class);
        return this;
    }

    @Override
    public InputStream inputStream(Path path) {
        throw notImplementedYet();
    }

    @Override
    public String readString(Path path) {
        domsole().appendUnimplementedWarning(FileSystemInMemory.class);
        return "";
    }

    @Override
    public boolean exists() {
        domsole().appendUnimplementedWarning(FileSystemInMemory.class);
        return false;
    }

    @Override
    public boolean isFile(Path path) {
        domsole().appendUnimplementedWarning(FileSystemInMemory.class);
        return false;
    }

    @Override
    public boolean isDirectory(Path path) {
        domsole().appendUnimplementedWarning(FileSystemInMemory.class);
        return false;
    }

    @Override
    public Stream<Path> walkRecursively() {
        domsole().appendUnimplementedWarning(FileSystemInMemory.class);
        return Stream.empty();
    }

    @Override
    public Stream<Path> walkRecursively(Path path) {
        domsole().appendUnimplementedWarning(FileSystemInMemory.class);
        return Stream.empty();
    }

    @Override
    public byte[] readFileAsBytes(Path path) {
        domsole().appendUnimplementedWarning(FileSystemInMemory.class);
        return new byte[]{};
    }

    @Override
    public FileSystemView subFileSystemView(String path) {
        domsole().appendUnimplementedWarning(FileSystemInMemory.class);
        return this;
    }

    @Override
    public FileSystem createDirectoryPath(String path) {
        domsole().appendUnimplementedWarning(FileSystemInMemory.class);
        return this;
    }
}
