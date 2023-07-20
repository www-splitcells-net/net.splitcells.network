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

import static net.splitcells.dem.utils.NotImplementedYet.notImplementedYet;

/**
 * Provides an {@link FileSystem} API for {@link Class#getResource(String)}.
 */
public class FileSystemViaClassResources implements FileSystem {
    public static FileSystem fileSystemBasedOnResources(Class<?> clazz) {
        return new FileSystemViaClassResources(clazz);
    }

    private final Class<?> clazz;

    private FileSystemViaClassResources(Class<?> clazz) {
        this.clazz = clazz;
    }

    @Override
    public InputStream inputStream(Path path) {
        throw notImplementedYet();
    }

    @Override
    public String readString(Path path) {
        throw notImplementedYet();
    }

    @Override
    public FileSystem writeToFile(Path path, byte[] content) {
        throw notImplementedYet();
    }

    @Override
    public boolean exists() {
        throw notImplementedYet();
    }

    @Override
    public boolean isFile(Path path) {
        throw notImplementedYet();
    }

    @Override
    public boolean isDirectory(Path path) {
        throw notImplementedYet();
    }

    @Override
    public Stream<Path> walkRecursively() {
        throw notImplementedYet();
    }

    @Override
    public Stream<Path> walkRecursively(Path path) {
        throw notImplementedYet();
    }

    @Override
    public byte[] readFileAsBytes(Path path) {
        throw notImplementedYet();
    }

    @Override
    public FileSystem subFileSystem(Path path) {
        throw notImplementedYet();
    }
}
