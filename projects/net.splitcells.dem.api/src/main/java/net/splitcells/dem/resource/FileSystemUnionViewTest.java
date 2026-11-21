/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.dem.resource;

import static net.splitcells.dem.testing.Assertions.requireEquals;
import static net.splitcells.dem.data.atom.Bools.requireNot;
import static net.splitcells.dem.data.atom.Bools.require;
import java.nio.file.Path;
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

    @UnitTestFactory public Stream<DynamicTest> testVarArg2() {
        return new FileSystemTest().fileSystemWriteTests(() -> {
            val fs1 = fileSystemViaMemory();
            val fs2 = fileSystemViaMemory();
            val testSubject = fileSystemUnionView(true, true, fs1, fs2);
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

    /**
     * Requested paths have to be the base path of the union or one of its children.
     * {@code ..} is allowed, as long as the resolved path stays inside the base path.
     */
    @UnitTest public void testContainmentInsideBasePath() {
        val fs1 = fileSystemViaMemory();
        val fs2 = fileSystemViaMemory();
        fs1.createDirectoryPath("inner/sub");
        fs2.createDirectoryPath("sibling");
        fs1.writeToFile("inner/inside", toBytes("inside"));
        fs2.writeToFile("sibling/secret", toBytes("secret"));
        val inner = fileSystemUnionView(true, true, fs1, fs2).subFileSystemView("inner");
        requireEquals(inner.readString("sub/../inside"), "inside");
        require(inner.isDirectory(Path.of("sub/..")));
        requireEquals(inner.subFileSystemView("sub/..").readString("inside"), "inside");
        requireThrow(() -> inner.isFile(Path.of("/sibling/secret")));
        requireThrow(() -> inner.isDirectory(Path.of("../sibling")));
        requireThrow(() -> inner.isFile(Path.of("../sibling/secret")));
        requireThrow(() -> inner.readString("../sibling/secret"));
        requireThrow(() -> inner.readString("sub/../../sibling/secret"));
        requireThrow(() -> inner.readFileAsBytes("../sibling/secret"));
        requireThrow(() -> inner.inputStream("../sibling/secret"));
        requireThrow(() -> inner.walkRecursively(".."));
        requireThrow(() -> inner.subFileSystemView("../sibling"));
        requireThrow(() -> inner.subFileSystemView("sub").readString("../inside"));
        requireThrow(() -> inner.license("../sibling/secret"));
    }

    /**
     * The root of a union is not allowed to be left either.
     * Such paths are not allowed to be silently clamped to the root.
     */
    @UnitTest public void testContainmentAtRoot() {
        val fs1 = fileSystemViaMemory();
        val fs2 = fileSystemViaMemory();
        fs1.writeToFile("x", toBytes("x"));
        val root = fileSystemUnionView(true, true, fs1, fs2);
        requireEquals(root.readString("./x"), "x");
        requireEquals(root.readString("a/../x"), "x");
        requireThrow(() -> root.isFile(Path.of("a/../../x")));
        requireThrow(() -> root.isDirectory(Path.of("..")));
        requireThrow(() -> root.isFile(Path.of("../x")));
        requireThrow(() -> root.readString("../x"));
        requireThrow(() -> root.readString("a/../../x"));
        requireThrow(() -> root.subFileSystemView(".."));
        requireThrow(() -> root.walkRecursively(".."));
    }
}
