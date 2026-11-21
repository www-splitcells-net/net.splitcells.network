/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.dem.resource;

import lombok.val;
import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.testing.Assertions;
import net.splitcells.dem.testing.annotations.UnitTest;
import net.splitcells.dem.testing.annotations.UnitTestFactory;
import org.junit.jupiter.api.DynamicTest;

import java.nio.file.Path;
import java.util.stream.Stream;

import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.resource.Files.unclosedTemporaryFolder;
import static net.splitcells.dem.resource.PathFileSystem.*;
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

    @UnitTest public void testContainment() {
        val parent = unclosedTemporaryFolder();
        try {
            Files.createDirectory(parent.resolve("root"));
            Files.writeToFile(parent.resolve("secret.txt"), "secret");
            val testSubject = pathFileSystem(parent.resolve("root"));
            testSubject.writeToFile(Path.of("inside.txt"), "inside");
            requireEquals(testSubject.readString(Path.of("a/../inside.txt")), "inside");
            requireThrow(() -> testSubject.isDirectory(Path.of("..")));
            requireThrow(() -> testSubject.isFile(Path.of("../secret.txt")));
            requireThrow(() -> testSubject.readString(Path.of("../secret.txt")));
            requireThrow(() -> testSubject.readFileAsBytes(Path.of("a/../../secret.txt")));
            requireThrow(() -> testSubject.inputStream(Path.of("../secret.txt")));
            requireThrow(() -> testSubject.readString(parent.resolve("secret.txt").toAbsolutePath()));
            requireThrow(() -> testSubject.writeToFile(Path.of("../written.txt"), "x"));
            requireThrow(() -> testSubject.subFileSystem(Path.of("..")));
        } finally {
            Files.deleteDirectory(parent);
        }
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
            val testSubject = pathFileSystemForMavenProject(Path.of("../").toAbsolutePath(), "net.splitcells.dem.api");
            val testResult = testSubject.license("src/main/resources/net/splitcells/dem/resource/FileSystemViaClassResourcesTest/testWalkRecursively/another-test.txt");
            requireEquals(testResult.getSpdxCopyrightText().orElseThrow(), "Contributors To The `net.splitcells.*` Projects");
            requireEquals(testResult.getSpdxLicenseIdentifier().orElseThrow(), "EPL-2.0 OR GPL-2.0-or-later");
        }
    }
}
