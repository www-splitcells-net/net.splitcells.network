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

import net.splitcells.dem.testing.annotations.IntegrationTest;

import java.nio.file.Path;

import static net.splitcells.dem.data.atom.Bools.require;
import static net.splitcells.dem.data.set.list.Lists.toList;
import static net.splitcells.dem.resource.FileSystemViaClassResources.fileSystemViaClassResources;
import static net.splitcells.dem.resource.Files.readAsString;
import static net.splitcells.dem.testing.Assertions.requireEquals;

public class FileSystemViaClassResourcesTest {
    @IntegrationTest
    public void testInputStream() {
        final var testSubject = fileSystemViaClassResources(FileSystemViaClassResourcesTest.class);
        requireEquals(readAsString(testSubject.inputStream("net/splitcells/dem/api/test-file.txt"))
                , "This is a test file of the 20th of July 2023.");
    }

    @IntegrationTest
    public void testReadString() {
        requireEquals(fileSystemViaClassResources(FileSystemViaClassResourcesTest.class)
                        .readString("net/splitcells/dem/api/test-file.txt")
                , "This is a test file of the 20th of July 2023.");
    }

    @IntegrationTest
    public void testExists() {
        require(fileSystemViaClassResources(FileSystemViaClassResourcesTest.class).exists());
    }

    @IntegrationTest
    public void testIsFile() {
        final var testSubject = fileSystemViaClassResources(FileSystemViaClassResourcesTest.class);
        require(testSubject.isFile("net/splitcells/dem/api/test-file.txt"));
    }

    @IntegrationTest
    public void testWalkRecursively() {
        final var rootPath = "net/splitcells/dem/resource/FileSystemViaClassResourcesTest/testWalkRecursively/";
        fileSystemViaClassResources(FileSystemViaClassResourcesTest.class)
                .walkRecursively(rootPath)
                .collect(toList())
                .requireContentsOf(Path.of(rootPath)
                        , Path.of(rootPath + "1")
                        , Path.of(rootPath + "1/2")
                        , Path.of(rootPath + "1/2/3")
                        , Path.of(rootPath + "1/2/3/test.txt")
                );
    }
}
