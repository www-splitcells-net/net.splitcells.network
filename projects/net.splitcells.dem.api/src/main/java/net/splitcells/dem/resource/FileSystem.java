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

import net.splitcells.dem.lang.annotations.ReturnsThis;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.stream.Stream;

/**
 * <p>Provides access to files, that is not specific to the operation system.
 * When one works with {@link java.nio.file.Path} and {@link java.nio.file.Files},
 * the current path (denoted as `./`) and the root path is always specific to the operation system.
 * This can lead to overly complex path handling,
 * </p>
 * <p>TODO Only allow a narrow subset of all possible paths by default.
 * Especially, don't allow pointing outside the file system via `../` and
 * discourage any usage of `..` and absolute paths.
 * </p>
 */
public interface FileSystem {

    default InputStream inputStream(String path) {
        return inputStream(Path.of(path));
    }

    InputStream inputStream(Path path);

    default String readString(String path) {
        return readString(Path.of(path));
    }

    String readString(Path path);

    @ReturnsThis
    FileSystem writeToFile(Path path, byte[] content);

    @ReturnsThis
    default FileSystem writeToFile(Path path, String content) {
        return writeToFile(path, content.getBytes(StandardCharsets.UTF_8));
    }

    boolean exists();

    boolean isFile(Path path);

    boolean isDirectory(Path path);

    default boolean isDirectory(String path) {
        return isDirectory(Path.of(path));
    }

    Stream<Path> walkRecursively();

    Stream<Path> walkRecursively(Path path);

    byte[] readFileAsBytes(Path path);

    FileSystem subFileSystem(Path path);

    default FileSystem subFileSystem(String path) {
        return this.subFileSystem(Path.of(path));
    }
}
