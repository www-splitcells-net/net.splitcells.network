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

import lombok.val;
import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.testing.Assertions;
import net.splitcells.dem.testing.annotations.IntegrationTest;
import net.splitcells.dem.testing.annotations.UnitTest;
import net.splitcells.dem.testing.annotations.UnitTestFactory;
import net.splitcells.dem.utils.ExecutionException;
import org.junit.jupiter.api.DynamicTest;

import java.nio.file.Path;
import java.util.stream.Stream;

import static java.util.stream.IntStream.range;
import static net.splitcells.dem.data.set.Sets.setOfUniques;
import static net.splitcells.dem.data.set.Sets.toSetOfUniques;
import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.resource.FileSystemViaMemory.fileSystemViaMemory;
import static net.splitcells.dem.resource.FileSystems.fileSystemOnLocalHost;
import static net.splitcells.dem.resource.FileSystems.temporaryFileSystem;
import static net.splitcells.dem.resource.Files.*;
import static net.splitcells.dem.testing.Assertions.requireThrow;
import static net.splitcells.dem.testing.Assertions.requireEquals;
import static net.splitcells.dem.utils.ExecutionException.execException;
import static net.splitcells.dem.utils.StringUtils.toBytes;

public class FileSystemsTest {
    @UnitTestFactory public Stream<DynamicTest> testFileSystemOnLocalHost() {
        final List<Path> tmps = list();
        return new FileSystemTest().fileSystemWriteTests(() -> {
            val tmp = unclosedTemporaryFolder();
            tmps.add(tmp);
            val fileSystem = fileSystemOnLocalHost(tmp);
            return new FileSystemTest.FileSystemAccess(fileSystem, fileSystem);
        }, () -> tmps.forEach(Files::deleteDirectory));
    }

    @UnitTestFactory public Stream<DynamicTest> testTemporaryFileSystem() {
        return new FileSystemTest().fileSystemWriteTests(() -> {
            val fileSystem = temporaryFileSystem();
            return new FileSystemTest.FileSystemAccess(fileSystem, fileSystem);
        });
    }

    @UnitTest public void testInvalidLocalhostFolder() {
        requireThrow(() -> fileSystemOnLocalHost(Path.of("/not-existing-local-folder-12431234235325")));
    }

    @UnitTest public void testUsersStateFiles() {
        val testSubject = FileSystems.usersStateFiles();
        val testFile = Path.of("./test-file.txt");
        val testContent = "testUsersStateFiles";
        testSubject.writeToFile(testFile, testContent);
        requireEquals(testSubject.readString(testFile), testContent);
    }
}
