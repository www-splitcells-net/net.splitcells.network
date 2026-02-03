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
import java.util.Optional;
import java.util.stream.Stream;

import static net.splitcells.dem.lang.tree.TreeI.tree;
import static net.splitcells.dem.utils.ExecutionException.execException;

/**
 * <p>No 2 instances of {@link FileSystem} should have the same class name
 * (i.e. multiple classes being named "FileSystem"),
 * because some decompilers have problems,
 * when such classes are referenced inside the same class.</p>
 */
public interface FileSystemView {
    default InputStream inputStream(String path) {
        return inputStream(Path.of(path));
    }

    InputStream inputStream(Path path);

    default Optional<String> readStringIfPresent(Path path) {
        if (isFile(path)) {
            return Optional.of(readString(path));
        }
        return Optional.empty();
    }

    default String readString(String path) {
        return readString(Path.of(path));
    }

    String readString(Path path);


    boolean exists();

    default boolean isFile(String path) {
        return isFile(Path.of(path));
    }

    boolean isFile(Path path);

    boolean isDirectory(Path path);

    default boolean isDirectory(String path) {
        return isDirectory(Path.of(path));
    }

    Stream<Path> walkRecursively();

    default Stream<Path> walkRecursively(String path) {
        return walkRecursively(Path.of(path));
    }

    Stream<Path> walkRecursively(Path path);

    byte[] readFileAsBytes(Path path);

    default byte[] readFileAsBytes(String path) {
        return readFileAsBytes(Path.of(path));
    }

    FileSystemView subFileSystemView(String path);
}
