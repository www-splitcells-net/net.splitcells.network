/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.dem.resource;

import lombok.val;
import net.splitcells.dem.testing.Assertions;
import net.splitcells.dem.testing.annotations.UnitTest;

import java.nio.file.Path;

import static net.splitcells.dem.data.atom.Bools.require;
import static net.splitcells.dem.data.atom.Bools.requireNot;
import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.data.set.list.Lists.toList;
import static net.splitcells.dem.resource.FileSystemVoid.fileSystemVoid;

public class FileSystemVoidTest {
    @UnitTest public void test() throws Throwable {
        val testSubject = fileSystemVoid();
        val testPath = Path.of("test-path");
        val testContent = "test-content";
        testSubject.writeToFile(testPath, testContent);
        Assertions.requireEquals(testSubject.inputStream(testPath).readAllBytes().length, 0);
        testSubject.appendToFile(testPath, testContent.getBytes());
        Assertions.requireEquals(testSubject.inputStream(testPath).readAllBytes().length, 0);
        require(testSubject.subFileSystem(testPath).exists());
        require(testSubject.subFileSystemView(testPath.toString()).exists());
        Assertions.requireEquals(testSubject.readString(testPath), "");
        requireNot(testSubject.isFile(testPath));
        testSubject.walkRecursively().collect(toList()).requireEmpty();
        testSubject.walkRecursively(testPath).collect(toList()).requireEmpty();
        Assertions.requireEquals(testSubject.readFileAsBytes(testPath).length, 0);
        testSubject.createDirectoryPath(testPath.toString());
        requireNot(testSubject.isDirectory(testPath));
        testSubject.delete(testPath.toString());
    }
}
