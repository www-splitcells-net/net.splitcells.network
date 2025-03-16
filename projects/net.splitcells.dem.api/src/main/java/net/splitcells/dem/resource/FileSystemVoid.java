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

import static net.splitcells.dem.resource.communication.log.Logs.logs;
import static net.splitcells.dem.utils.BinaryUtils.emptyByteArray;
import static net.splitcells.dem.utils.NotImplementedYet.notImplementedYet;

/**
 * Provides a placeholder file system, that never creates errors and is always empty.
 * If you need a temporary {@link FileSystem} with no content, use {@link FileSystemViaMemory} instead.
 */
public class FileSystemVoid implements FileSystem {
    public static FileSystem fileSystemVoid() {
        return new FileSystemVoid();
    }

    private boolean isUsageWarned = false;

    private FileSystemVoid() {

    }

    private void warnUsage() {
        if (!isUsageWarned) {
            logs().appendUnimplementedWarning(FileSystemVoid.class);
            isUsageWarned = true;
        }
    }

    @Override
    public FileSystem writeToFile(Path path, byte[] content) {
        warnUsage();
        return this;
    }

    @Override
    public FileSystem appendToFile(Path path, byte[] content) {
        warnUsage();
        return this;
    }

    @Override
    public FileSystem subFileSystem(Path path) {
        warnUsage();
        return this;
    }

    @Override
    public InputStream inputStream(Path path) {
        warnUsage();
        return new InputStream() {
            @Override
            public int read() {
                return -1;  // end of stream
            }
        };
    }

    @Override
    public String readString(Path path) {
        warnUsage();
        return "";
    }

    @Override
    public boolean exists() {
        warnUsage();
        return false;
    }

    @Override
    public boolean isFile(Path path) {
        warnUsage();
        return false;
    }

    @Override
    public boolean isDirectory(Path path) {
        warnUsage();
        return false;
    }

    @Override
    public Stream<Path> walkRecursively() {
        warnUsage();
        return Stream.empty();
    }

    @Override
    public Stream<Path> walkRecursively(Path path) {
        warnUsage();
        return Stream.empty();
    }

    @Override
    public byte[] readFileAsBytes(Path path) {
        warnUsage();
        return emptyByteArray();
    }

    @Override
    public FileSystemView subFileSystemView(String path) {
        warnUsage();
        return this;
    }

    @Override
    public FileSystem createDirectoryPath(String path) {
        warnUsage();
        return this;
    }

    @Override
    public FileSystem delete(String path) {
        warnUsage();
        return this;
    }
}
