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

import java.nio.file.Path;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static net.splitcells.dem.data.atom.Bools.require;
import static net.splitcells.dem.data.atom.Bools.requireNot;
import static net.splitcells.dem.resource.FileSystemViaMemory.fileSystemViaMemory;
import static net.splitcells.dem.testing.TestTypes.UNIT_TEST;
import static net.splitcells.dem.utils.StreamUtils.concat;

public class FileSystemWriteTest extends TestSuiteI {

    public void testExists(Supplier<FileSystem> factory) {
        require(factory.get().exists());
    }

    public void testExistsForSubFileSystem(Supplier<FileSystem> factory) {
        require(factory.get().createDirectoryPath("test").subFileSystem("test").exists());
        require(factory.get().createDirectoryPath("test").subFileSystem(Path.of("test")).exists());
        requireNot(factory.get().subFileSystem("test").exists());
        requireNot(factory.get().subFileSystem(Path.of("test")).exists());
    }

    public void testCreateDirectoryPath(Supplier<FileSystem> factory) {
        require(factory.get().createDirectoryPath("test-directory").isDirectory(Path.of("test-directory")));
        require(factory.get().createDirectoryPath("test-directory").isDirectory("test-directory"));
    }

    public Stream<DynamicTest> fileSystemWriteTests(Supplier<FileSystem> factory) {
        return concat(dynamicTests(this::testExists, factory)
                , dynamicTests(this::testExistsForSubFileSystem, factory)
                , dynamicTests(this::testCreateDirectoryPath, factory)
        );
    }
}
