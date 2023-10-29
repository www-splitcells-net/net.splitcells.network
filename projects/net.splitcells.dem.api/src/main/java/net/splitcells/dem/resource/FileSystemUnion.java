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

import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.data.set.list.Lists;
import net.splitcells.dem.utils.StreamUtils;

import java.io.InputStream;
import java.nio.file.Path;
import java.util.Optional;
import java.util.stream.Stream;

import static net.splitcells.dem.data.set.list.Lists.listWithValuesOf;
import static net.splitcells.dem.utils.NotImplementedYet.notImplementedYet;
import static net.splitcells.dem.utils.StreamUtils.concat;

public class FileSystemUnion implements FileSystemView {

    public static FileSystemView fileSystemsUnion(FileSystemView... fileSystems) {
        return new FileSystemUnion(listWithValuesOf(fileSystems));
    }

    public static FileSystemView fileSystemUnion(List<FileSystemView> fileSystems) {
        return new FileSystemUnion(fileSystems);
    }

    private final List<FileSystemView> fileSystems;

    private FileSystemUnion(List<FileSystemView> fileSystemsArg) {
        fileSystems = fileSystemsArg;
    }

    private Optional<FileSystemView> findFileSystemWithExistingFile(Path path) {
        return fileSystems.stream().filter(f -> f.isFile(path)).findFirst();
    }

    @Override
    public InputStream inputStream(Path path) {
        return findFileSystemWithExistingFile(path).orElseThrow().inputStream(path);
    }

    @Override
    public String readString(Path path) {
        return findFileSystemWithExistingFile(path).orElseThrow().readString(path);
    }

    @Override
    public boolean exists() {
        return !fileSystems.isEmpty();
    }

    @Override
    public boolean isFile(Path path) {
        return findFileSystemWithExistingFile(path).isPresent();
    }

    @Override
    public boolean isDirectory(Path path) {
        return fileSystems.stream().anyMatch(f -> f.isDirectory(path));
    }

    @Override
    public Stream<Path> walkRecursively() {
        return concat(fileSystems.stream().map(FileSystemView::walkRecursively).collect(Lists.toList()));
    }

    @Override
    public Stream<Path> walkRecursively(Path path) {
        return concat(fileSystems.stream()
                .filter(f -> f.isDirectory(path))
                .map(f -> f.walkRecursively(path))
                .collect(Lists.toList()));
    }

    @Override
    public byte[] readFileAsBytes(Path path) {
        return findFileSystemWithExistingFile(path).orElseThrow().readFileAsBytes(path);
    }

    @Override
    public FileSystemView subFileSystemView(String path) {
        throw notImplementedYet();
    }
}
