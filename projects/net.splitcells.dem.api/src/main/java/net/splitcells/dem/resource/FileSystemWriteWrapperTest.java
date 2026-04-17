/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.dem.resource;

import lombok.val;
import net.splitcells.dem.testing.annotations.UnitTest;
import net.splitcells.dem.testing.annotations.UnitTestFactory;
import org.junit.jupiter.api.DynamicTest;

import java.nio.file.Path;
import java.util.stream.Stream;

import static net.splitcells.dem.data.atom.Bools.require;
import static net.splitcells.dem.data.atom.Bools.requireNot;
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

    @UnitTest public void testWriteBlockage() {
        val writer = fileSystemViaMemory();
        val wrapper = fileSystemWriteWrapper(writer);
        val subWrapper = wrapper.subFileSystem("./");
        val notExistingFile = Path.of("./not-existing-file");
        val existingFile = Path.of("./existing-file");
        writer.writeToFile(existingFile, "".getBytes());
        wrapper.writeToFile(notExistingFile, "".getBytes());
        subWrapper.writeToFile(notExistingFile, "".getBytes());
        wrapper.appendToFile(notExistingFile, "".getBytes());
        subWrapper.appendToFile(notExistingFile, "".getBytes());
        wrapper.delete(existingFile.toString());
        subWrapper.delete(existingFile.toString());
        require(writer.isFile(existingFile));
        requireNot(writer.isFile(notExistingFile));
    }
}
