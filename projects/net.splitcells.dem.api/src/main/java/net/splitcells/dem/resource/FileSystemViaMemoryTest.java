/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.dem.resource;

import lombok.val;
import net.splitcells.dem.testing.TestSuiteI;
import net.splitcells.dem.testing.annotations.UnitTest;
import net.splitcells.dem.testing.annotations.UnitTestFactory;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import java.util.stream.Stream;

import static net.splitcells.dem.resource.FileSystemViaMemory.fileSystemViaMemory;

public class FileSystemViaMemoryTest {
    @UnitTestFactory public Stream<DynamicTest> fileSystemTests() {
        return new FileSystemTest().fileSystemWriteTests(() -> {
            val fileSystem = fileSystemViaMemory();
            return new FileSystemTest.FileSystemAccess(fileSystem, fileSystem);
        });
    }
}
