/*
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.dem.resource;

import lombok.val;
import net.splitcells.dem.testing.TestSuiteI;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.TestFactory;

import java.util.stream.Stream;

import static net.splitcells.dem.resource.FileSystemViaMemory.fileSystemViaMemory;
import static net.splitcells.dem.testing.TestTypes.UNIT_TEST;
import static net.splitcells.dem.utils.StreamUtils.concat;

public class FileSystemViaMemoryTest extends TestSuiteI {
    @Tag(UNIT_TEST) @TestFactory public Stream<DynamicTest> fileSystemWriteTests() {
        return new FileSystemWriteTest().fileSystemWriteTests(() -> fileSystemViaMemory());
    }
}
