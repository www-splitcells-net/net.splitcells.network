/*
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.dem.resource;

import lombok.val;
import net.splitcells.dem.resource.communication.Closeable;
import net.splitcells.dem.testing.TestSuiteI;
import net.splitcells.dem.utils.StringUtils;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.TestFactory;

import java.nio.file.Path;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static net.splitcells.dem.data.atom.Bools.require;
import static net.splitcells.dem.data.atom.Bools.requireNot;
import static net.splitcells.dem.resource.FileSystemViaMemory.fileSystemViaMemory;
import static net.splitcells.dem.resource.communication.Closeable.close;
import static net.splitcells.dem.testing.Assertions.requireEquals;
import static net.splitcells.dem.testing.Assertions.requireThrow;
import static net.splitcells.dem.testing.TestTypes.UNIT_TEST;
import static net.splitcells.dem.utils.StreamUtils.concat;
import static net.splitcells.dem.utils.StringUtils.parseString;

public class FileSystemWriteTest extends TestSuiteI {

    public void testExists(Supplier<FileSystem> factory) {
        require(factory.get().exists());
    }

    public void testExistsForSubFileSystem(Supplier<FileSystem> factory) {
        requireThrow(() -> {
            val testSubject = factory.get();
            testSubject.writeToFile("test", "content".getBytes());
            testSubject.createDirectoryPath("test");
        });
        require(factory.get().createDirectoryPath("test").subFileSystem("test").exists());
        require(factory.get().createDirectoryPath("test").subFileSystemView("test").exists());
        require(factory.get().createDirectoryPath("test").subFileSystem(Path.of("test")).exists());
        requireNot(factory.get().subFileSystem("test").exists());
        requireNot(factory.get().subFileSystem(Path.of("test")).exists());
    }

    public void testIsFile(Supplier<FileSystem> factory) {
        val testSubject = factory.get();
        testSubject.writeToFile("test", "content".getBytes());
        require(testSubject.isFile("test"));
        requireNot(testSubject.isFile("not-existing"));
    }

    public void testReadFileAsBytes(Supplier<FileSystem> factory) {
        val testSubject = factory.get();
        testSubject.writeToFile("test", "content".getBytes());
        requireEquals(parseString(testSubject.readFileAsBytes("test")), "content");
        requireThrow(() -> {
            val failTester = factory.get();
            requireEquals(parseString(failTester.readFileAsBytes("test")), "content");
        });
    }

    public void testReadString(Supplier<FileSystem> factory) {
        val testSubject = factory.get();
        val testContent = "content";
        testSubject.writeToFile("test", testContent.getBytes());
        requireEquals(testSubject.readString("test"), testContent);
        requireThrow(() -> {
            val failTester = factory.get();
            failTester.readString("test");
        });
    }

    public void testInputStream(Supplier<FileSystem> factory) {
        val testSubject = factory.get();
        val testContent = "content";
        testSubject.writeToFile("test", testContent.getBytes());
        requireEquals(Files.readAsString(testSubject.inputStream("test")), testContent);
        requireThrow(() -> {
            val failTester = factory.get();
            close(() -> failTester.inputStream("test"));
        });
    }

    public void testWriteToFile(Supplier<FileSystem> factory) {
        val testSubject = factory.get();
        testSubject.writeToFile("test", "content".getBytes());
        requireThrow(() -> {
            val failTester = factory.get();
            failTester.writeToFile("missing-folder/test", "content".getBytes());
        });
    }

    public void testCreateDirectoryPath(Supplier<FileSystem> factory) {
        require(factory.get().createDirectoryPath("test-directory").isDirectory(Path.of("test-directory")));
        require(factory.get().createDirectoryPath("test-directory").isDirectory("test-directory"));
    }

    public Stream<DynamicTest> fileSystemWriteTests(Supplier<FileSystem> factory) {
        return concat(dynamicTests(this::testExists, factory)
                , dynamicTests(this::testExistsForSubFileSystem, factory)
                , dynamicTests(this::testCreateDirectoryPath, factory)
                , dynamicTests(this::testIsFile, factory)
                , dynamicTests(this::testReadFileAsBytes, factory)
                , dynamicTests(this::testWriteToFile, factory)
                , dynamicTests(this::testReadString, factory)
                , dynamicTests(this::testInputStream, factory)
        );
    }
}
