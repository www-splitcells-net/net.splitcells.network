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
import net.splitcells.dem.utils.StreamUtils;

import java.io.InputStream;
import java.nio.file.Path;
import java.util.stream.Stream;

import static net.splitcells.dem.data.set.list.Lists.toList;
import static net.splitcells.dem.lang.perspective.TreeI.perspective;
import static net.splitcells.dem.utils.ExecutionException.executionException;
import static net.splitcells.dem.utils.NotImplementedYet.notImplementedYet;

/**
 * TODO See {@link FileSystemUnion}. I implemented this, because I thought I needed this,
 * but this is not the case yet.
 * It will be needed for the webserver.
 *
 */
public class FileSystemUnionView implements FileSystemView {

    private static final String UNAMBIGUOUS_PATH = "Could find unambiguous file system match.";
    private  static final String MATCHES = "matches";
    private  static final String PATH = "matches";

    public static FileSystemUnionView fileSystemUnionView(List<FileSystemView> fileSystems) {
        return new FileSystemUnionView(fileSystems);
    }

    private final List<FileSystemView> fileSystems;

    private FileSystemUnionView(List<FileSystemView> fileSystemsArg) {
        fileSystems = fileSystemsArg;
    }

    private FileSystemView getFileSystemWithExistingFile(Path path) {
        final var matches = fileSystems.stream()
                .filter(f -> f.isFile(path))
                .collect(toList());
        if (matches.size() != 1) {
            throw executionException(perspective(UNAMBIGUOUS_PATH)
                    .withProperty(PATH, path.toString())
                    .withProperty(MATCHES, matches.toString()));
        }
        return matches.get(0);
    }

    @Override
    public InputStream inputStream(Path path) {
        return getFileSystemWithExistingFile(path).inputStream(path);
    }

    @Override
    public String readString(Path path) {
        return getFileSystemWithExistingFile(path).readString(path);
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
        final var matches = fileSystems.stream()
                .map(f -> f.isFile(path))
                .filter(f -> f)
                .collect(toList());
        if (matches.size() != 1) {
            throw executionException(perspective(UNAMBIGUOUS_PATH)
                    .withProperty(PATH, path.toString())
                    .withProperty(MATCHES, matches.toString()));
        }
        return matches.hasElements();
    }

    @Override
    public boolean isDirectory(Path path) {
        final var matches = fileSystems.stream()
                .map(f -> f.isDirectory(path))
                .filter(f -> f)
                .collect(toList());
        if (matches.size() != 1) {
            throw executionException(perspective(UNAMBIGUOUS_PATH)
                    .withProperty(PATH, path.toString())
                    .withProperty(MATCHES, matches.toString()));
        }
        return matches.hasElements();
    }

    @Override
    public Stream<Path> walkRecursively() {
        final var walks = fileSystems.stream()
                .map(f -> f.walkRecursively())
                .collect(toList());
        return StreamUtils.concat(walks);
    }

    @Override
    public Stream<Path> walkRecursively(Path path) {
        final var walks = fileSystems.stream()
                .map(f -> f.walkRecursively(path))
                .collect(toList());
        return StreamUtils.concat(walks);
    }

    @Override
    public byte[] readFileAsBytes(Path path) {
        return getFileSystemWithExistingFile(path).readFileAsBytes(path);
    }

    @Override
    public FileSystemView subFileSystemView(String path) {
        throw notImplementedYet();
    }
}
