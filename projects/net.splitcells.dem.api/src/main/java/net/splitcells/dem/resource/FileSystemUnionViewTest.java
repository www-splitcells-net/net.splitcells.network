/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.dem.resource;

import lombok.val;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.TestFactory;

import java.util.stream.Stream;

import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.resource.FileSystemUnionView.fileSystemUnionView;
import static net.splitcells.dem.resource.FileSystemViaMemory.fileSystemViaMemory;
import static net.splitcells.dem.testing.TestTypes.UNIT_TEST;

public class FileSystemUnionViewTest {
    @TestFactory public Stream<DynamicTest> test() {
        return new FileSystemTest().fileSystemWriteTests(() -> {
            val fs1 = fileSystemViaMemory();
            val fs2 = fileSystemViaMemory();
            val testSubject = fileSystemUnionView(true, list(fs1, fs2));
            return new FileSystemTest.FileSystemAccess(fs1, testSubject);
        });
    }

    @TestFactory public Stream<DynamicTest> testVarArg() {
        return new FileSystemTest().fileSystemWriteTests(() -> {
            val fs1 = fileSystemViaMemory();
            val fs2 = fileSystemViaMemory();
            val testSubject = fileSystemUnionView(true, fs1, fs2);
            return new FileSystemTest.FileSystemAccess(fs1, testSubject);
        });
    }
}
