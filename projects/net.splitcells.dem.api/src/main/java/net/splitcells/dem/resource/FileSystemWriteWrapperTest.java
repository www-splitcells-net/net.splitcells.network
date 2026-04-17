/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.dem.resource;

import lombok.val;
import net.splitcells.dem.testing.annotations.UnitTest;
import net.splitcells.dem.testing.annotations.UnitTestFactory;
import org.junit.jupiter.api.DynamicTest;

import java.util.stream.Stream;

import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.resource.FileSystemUnionView.fileSystemUnionView;
import static net.splitcells.dem.resource.FileSystemViaMemory.fileSystemViaMemory;
import static net.splitcells.dem.resource.FileSystemWriteWrapper.fileSystemWriteWrapper;

public class FileSystemWriteWrapperTest {
    @UnitTestFactory public Stream<DynamicTest> test() {
        return new FileSystemTest().fileSystemWriteTests(() -> {
            val writer = fileSystemViaMemory();
            val reader = fileSystemWriteWrapper(writer);
            return new FileSystemTest.FileSystemAccess(writer, reader);
        });
    }
}
