/*
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.dem.resource;

import lombok.val;
import net.splitcells.dem.testing.TestSuiteI;
import org.junit.jupiter.api.DynamicTest;

import java.nio.file.Path;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static net.splitcells.dem.data.atom.Bools.require;
import static net.splitcells.dem.data.atom.Bools.requireNot;
import static net.splitcells.dem.data.set.Sets.setOfUniques;
import static net.splitcells.dem.data.set.Sets.toSetOfUniques;
import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.resource.communication.Closeable.close;
import static net.splitcells.dem.testing.Assertions.requireEquals;
import static net.splitcells.dem.testing.Assertions.requireThrow;
import static net.splitcells.dem.utils.StreamUtils.concat;
import static net.splitcells.dem.utils.StringUtils.parseString;

public class FileSystemTest extends TestSuiteI {

    public void testExists(Supplier<FileSystemAccess> factory) {
        require(factory.get().reader.exists());
    }

    public void testExistsForSubFileSystem(Supplier<FileSystemAccess> factory) {
        requireThrow(() -> {
            val testSubject = factory.get();
            testSubject.writer.writeToFile("test", "content".getBytes());
            testSubject.writer.createDirectoryPath("test");
        });
        require(factory.get().writer.createDirectoryPath("test").subFileSystem("test").exists());
        require(factory.get().writer.createDirectoryPath("test").subFileSystemView("test").exists());
        require(factory.get().writer.createDirectoryPath("test").subFileSystem(Path.of("test")).exists());
        requireNot(factory.get().writer.subFileSystem("test").exists());
        requireNot(factory.get().writer.subFileSystem(Path.of("test")).exists());
    }

    public void testIsFile(Supplier<FileSystemAccess> factory) {
        val testSubject = factory.get();
        testSubject.writer.writeToFile("test", "content".getBytes());
        require(testSubject.reader.isFile("test"));
        requireNot(testSubject.reader.isFile("not-existing"));
    }

    public void testDelete(Supplier<FileSystemAccess> factory) {
        val testSubject = factory.get();
        testSubject.writer.writeToFile("test", "content".getBytes());
        require(testSubject.reader.isFile("test"));
        testSubject.writer.delete("test");
        requireNot(testSubject.reader.isFile("test"));
        requireThrow(() -> factory.get().writer.delete("not-existing-file"));
    }

    public void testReadFileAsBytes(Supplier<FileSystemAccess> factory) {
        val testSubject = factory.get();
        testSubject.writer.writeToFile("test", "content".getBytes());
        requireEquals(parseString(testSubject.reader.readFileAsBytes("test")), "content");
        requireThrow(() -> requireEquals(parseString(factory.get().reader.readFileAsBytes("test")), "content"));
    }

    public void testReadString(Supplier<FileSystemAccess> factory) {
        val testSubject = factory.get();
        val testContent = "content";
        testSubject.writer.writeToFile("test", testContent.getBytes());
        requireEquals(testSubject.reader.readString("test"), testContent);
        requireThrow(() -> factory.get().reader.readString("test"));
    }

    public void testInputStream(Supplier<FileSystemAccess> factory) {
        val testSubject = factory.get();
        val testContent = "content";
        testSubject.writer.writeToFile("test", testContent.getBytes());
        requireEquals(Files.readAsString(testSubject.reader.inputStream("test")), testContent);
        requireThrow(() -> {
            val failTester = factory.get();
            close(() -> failTester.reader.inputStream("test"));
        });
    }

    public void testWriteToFile(Supplier<FileSystemAccess> factory) {
        val testSubject = factory.get();
        testSubject.writer.writeToFile("test", "content".getBytes());
        requireThrow(() -> factory.get().writer.writeToFile("missing-folder/test", "content".getBytes()));
    }

    public void testAppendToFile(Supplier<FileSystemAccess> factory) {
        val testSubject = factory.get();
        testSubject.writer.appendToFile(Path.of("test"), "test-".getBytes());
        testSubject.writer.appendToFile(Path.of("test"), "content".getBytes());
        requireEquals(testSubject.reader.readString("test"), "test-content");
        requireThrow(() -> factory.get().writer.appendToFile(Path.of("missing-folder/test"), "content".getBytes()));
    }

    public void testCreateDirectoryPath(Supplier<FileSystemAccess> factory) {
        val testSubject = factory.get();
        testSubject.writer.createDirectoryPath("test-directory");
        require(testSubject.reader.isDirectory(Path.of("test-directory")));
        testSubject.writer.createDirectoryPath("test-directory");
        require(testSubject.reader.isDirectory("test-directory"));
    }

    public void testWalkRecursively(Supplier<FileSystemAccess> factory) {
        val testSubject = factory.get();
        val testFolder = "test/folder/path/";
        val testFile = testFolder + "file";
        val testContent = "test-content";
        testSubject.writer.createDirectoryPath(testFolder);
        testSubject.writer.writeToFile(testFile, testContent.getBytes());
        val expectedResult = setOfUniques(
                "test"
                , "test/folder"
                , "test/folder/path"
                , "test/folder/path/file");
        testSubject.reader.walkRecursively(Path.of("./"))
                .map(Path::toString)
                .collect(toSetOfUniques())
                .requireContentsOf(expectedResult);
        testSubject.reader.walkRecursively(Path.of("/./"))
                .map(Path::toString)
                .collect(toSetOfUniques())
                .requireContentsOf(expectedResult);
        testSubject.reader.walkRecursively()
                .map(Path::toString)
                .collect(toSetOfUniques())
                .requireContentsOf(expectedResult);
        requireThrow(() -> testSubject.reader.walkRecursively(Path.of("./not-existing-folder"))
                .map(Path::toString)
                .collect(toSetOfUniques())
                .requireContentsOf(expectedResult));
    }

    /**
     * This record makes it possible to test read only {@link FileSystemView},
     * where the data comes from a {@link FileSystem}.
     * This only works, when {@link FileSystemView} is kind of a wrapper for {@link FileSystem}.
     * See {@link FileSystemUnionView} as an example for this.
     *
     * @param writer
     * @param reader
     */
    public record FileSystemAccess(FileSystem writer, FileSystemView reader) {

    }

    public Stream<DynamicTest> fileSystemWriteTests(Supplier<FileSystemAccess> factory) {
        return concat(dynamicTests(this::testExists, factory)
                , dynamicTests(this::testExistsForSubFileSystem, factory)
                , dynamicTests(this::testCreateDirectoryPath, factory)
                , dynamicTests(this::testIsFile, factory)
                , dynamicTests(this::testReadFileAsBytes, factory)
                , dynamicTests(this::testWriteToFile, factory)
                , dynamicTests(this::testReadString, factory)
                , dynamicTests(this::testInputStream, factory)
                , dynamicTests(this::testDelete, factory)
                , dynamicTests(this::testAppendToFile, factory)
                , dynamicTests(this::testWalkRecursively, factory)
        );
    }
}
