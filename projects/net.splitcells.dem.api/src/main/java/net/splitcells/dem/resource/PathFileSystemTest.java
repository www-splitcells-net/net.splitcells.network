/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.dem.resource;

import lombok.val;
import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.testing.annotations.UnitTest;
import net.splitcells.dem.testing.annotations.UnitTestFactory;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.util.stream.Stream;

import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.resource.Files.unclosedTemporaryFolder;
import static net.splitcells.dem.resource.PathFileSystem.pathFileSystem;
import static net.splitcells.dem.resource.PathFileSystem.temporaryFileSystem;
import static net.splitcells.dem.testing.Assertions.requireEquals;
import static net.splitcells.dem.testing.Assertions.requireThrow;

public class PathFileSystemTest {
    @UnitTestFactory public Stream<DynamicTest> testPathFileSystem() {
        final List<Path> tmps = list();
        return new FileSystemTest().fileSystemWriteTests(() -> {
            val tmp = unclosedTemporaryFolder();
            tmps.add(tmp);
            val fileSystem = pathFileSystem(tmp);
            return new FileSystemTest.FileSystemAccess(fileSystem, fileSystem);
        }, () -> tmps.forEach(Files::deleteDirectory));
    }

    @UnitTestFactory public Stream<DynamicTest> testTemporaryFileSystem() {
        final List<FileSystemResource> resources = list();
        return new FileSystemTest().fileSystemWriteTests(() -> {
                    val fileSystem = temporaryFileSystem();
                    resources.add(fileSystem);
                    return new FileSystemTest.FileSystemAccess(fileSystem, fileSystem);
                }
                , () -> resources.forEach(f -> f.close()));
    }

    @UnitTest public void testInvalidLocalhostFolder() {
        requireThrow(() -> pathFileSystem(Path.of("/not-existing-local-folder-12431234235325")));
    }

    @UnitTest public void testUsersStateFiles() {
        val testSubject = PathFileSystem.usersStateFiles();
        val testFile = Path.of("./test-file.txt");
        val testContent = "testUsersStateFiles";
        testSubject.writeToFile(testFile, testContent);
        requireEquals(testSubject.readString(testFile), testContent);
    }

    /**
     * TODO It would be better to create a copy of the file system via the class loader in a temporary folder.
     * This way the test would be more portable.
     */
    @UnitTest public void testLicense() {
        if (net.splitcells.dem.testing.Test.isExecutedViaMaven()) {
            // The target folder is only available with Maven.
        }
    }
}
