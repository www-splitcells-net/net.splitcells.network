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

import net.splitcells.dem.Dem;
import net.splitcells.dem.environment.config.framework.Option;
import net.splitcells.dem.lang.annotations.ReturnsThis;

import java.nio.file.Path;
import java.util.Optional;

import static net.splitcells.dem.utils.NotImplementedYet.notImplementedYet;
import static net.splitcells.dem.utils.StringUtils.toBytes;

/**
 * <p>No 2 instances of {@link FileSystem} should have the same class name
 * (i.e. multiple classes being named "FileSystem"),
 * because some decompilers have problems,
 * when such classes are referenced inside the same class.</p>
 * <p>When one works with {@link java.nio.file.Path} and {@link java.nio.file.Files} directly,
 * the current path (denoted as `./`) and the root path is always specific to the operation system and
 * the current process of the operation system.
 * This can lead to overly complex path handling, which is prone to errors.
 * Especially when one considers, how {@link java.nio.file.Path#resolve(Path)} works
 * with absolute paths.</p>
 * <p>The {@link FileSystem} tries to avoid this situation, by providing access to files,
 * that is not specific to the operation system and by only working with simplified paths.</p>
 * <p>The basic idea is, to only work with basic relative paths, that have no parent notations (= `../`).
 * Every time the user uses a path, the respective current folder or filesystem has to be specified.
 * This creates a situation, where for every purpose a file system is provided.
 * These are isolated from each other.
 * Thereby, different things like the config files or cache files can be handled via different file systems.
 * In the ideal case, the actual storage of these file is only configured and thereby interchangeable.</p>
 * <p>By default the methods do not provide any guarantee of being synchronous or asynchronous.
 * Implementations, may implement such methods in any way they like.
 * The reason for this is the fact, that asynchronous execution can be implemented using an synchronous API.</p>
 * <p>TODO Only allow a narrow subset of all possible paths by default.
 * Especially, don't allow pointing outside the file system via `../` and
 * discourage any usage of `..` and absolute paths.
 * </p>
 * <p>TODO Check on start of {@link Dem#process(Runnable)},
 * if every or none {@link FileSystem} {@link Option} is set and warn accordingly.</p>
 */
public interface FileSystem extends FileSystemView {

    @ReturnsThis
    FileSystem writeToFile(Path path, byte[] content);

    @ReturnsThis
    default FileSystem copyFileFromTo(Trail from, Trail to) {
        throw notImplementedYet();
    }

    @ReturnsThis
    FileSystem appendToFile(Path path, byte[] content);

    @ReturnsThis
    default FileSystem writeToFile(String path, byte[] content) {
        return writeToFile(Path.of(path), content);
    }

    @ReturnsThis
    default FileSystem replaceFile(String path, byte[] content) {
        deleteIfExists(path);
        return writeToFile(Path.of(path), content);
    }

    @ReturnsThis
    default FileSystem writeToFile(Path path, String content) {
        return writeToFile(path, toBytes(content));
    }

    FileSystem subFileSystem(Path path);

    default FileSystem subFileSystem(String path) {
        return this.subFileSystem(Path.of(path));
    }

    default FileSystem createDirectoryPath(String path) {
        throw notImplementedYet();
    }

    default FileSystem deleteIfExists(String path) {
        if (isFile(path)) {
            delete(path);
        }
        return this;
    }

    FileSystem delete(String path);

    /**
     * This method is used, in order make {@link FileSystem} compatible with tools,
     * that can only work with {@link Path}.
     * These tools are external dependencies and this method should not be used by core code.
     *
     * @return Returns the {@link Path}, that points to the same directory as this {@link FileSystem},
     * if this {@link FileSystem} supports creating such a {@link Path} object.
     */
    default Optional<Path> javaLegacyPath() {
        return Optional.empty();
    }

    /**
     * This method is used, in order make {@link FileSystem} compatible with tools,
     * that can only work with {@link Path}.
     * These tools are external dependencies and this method should not be used by core code.
     *
     * @param path The path relative to this {@link FileSystem}, for which the legacy access object is to be created.
     * @return This is a {@link Path}, that provides read or write access to the request path.
     */
    default Optional<Path> javaLegacyPath(Path path) {
        return Optional.empty();
    }
}
