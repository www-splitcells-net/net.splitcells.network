/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.dem.resource;

import lombok.val;
import net.splitcells.dem.testing.annotations.UnitTest;
import net.splitcells.dem.testing.annotations.UnitTestFactory;
import net.splitcells.dem.utils.StringUtils;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.TestFactory;

import java.util.stream.Stream;

import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.resource.FileSystemUnionView.fileSystemUnionView;
import static net.splitcells.dem.resource.FileSystemViaMemory.fileSystemViaMemory;
import static net.splitcells.dem.testing.Assertions.requireThrow;
import static net.splitcells.dem.testing.TestTypes.UNIT_TEST;
import static net.splitcells.dem.utils.StringUtils.toBytes;

public class FileSystemUnionViewTest {
    @UnitTestFactory public Stream<DynamicTest> test() {
        return new FileSystemTest().fileSystemWriteTests(() -> {
            val fs1 = fileSystemViaMemory();
            val fs2 = fileSystemViaMemory();
            val testSubject = fileSystemUnionView(true, list(fs1, fs2));
            return new FileSystemTest.FileSystemAccess(fs1, testSubject);
        });
    }

    @UnitTestFactory public Stream<DynamicTest> testVarArg() {
        return new FileSystemTest().fileSystemWriteTests(() -> {
            val fs1 = fileSystemViaMemory();
            val fs2 = fileSystemViaMemory();
            val testSubject = fileSystemUnionView(true, fs1, fs2);
            return new FileSystemTest.FileSystemAccess(fs1, testSubject);
        });
    }

    @UnitTest public void testIsDirectory() {
        val fs1 = fileSystemViaMemory();
        val fs2 = fileSystemViaMemory();
        val testSubject = fileSystemUnionView(true, true, fs1, fs2);
        val testDirectory = "test-directory/";
        fs1.createDirectoryPath(testDirectory);
        fs2.createDirectoryPath(testDirectory);
        requireThrow(() -> testSubject.isDirectory(testDirectory));
    }

    @UnitTest public void testIsFile() {
        val fs1 = fileSystemViaMemory();
        val fs2 = fileSystemViaMemory();
        val testSubject = fileSystemUnionView(true, true, fs1, fs2);
        val testFile = "test-file";
        fs1.writeToFile(testFile, toBytes(testFile));
        fs2.writeToFile(testFile, toBytes(testFile));
        requireThrow(() -> testSubject.isFile(testFile));
    }
}
