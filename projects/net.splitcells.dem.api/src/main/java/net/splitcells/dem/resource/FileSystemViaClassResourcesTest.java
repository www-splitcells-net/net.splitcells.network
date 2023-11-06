/*
 * Copyright (c) 2021 Contributors To The `net.splitcells.*` Projects
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the Eclipse
 * Public License, v. 2.0 are satisfied: GNU General Public License v2.0 or later
 * which is available at https://www.gnu.org/licenses/old-licenses/gpl-2.0-standalone.html
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.dem.resource;

import net.splitcells.dem.testing.TestSuiteI;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.TestFactory;

import java.nio.file.Path;
import java.util.stream.Stream;

import static net.splitcells.dem.data.atom.Bools.require;
import static net.splitcells.dem.data.atom.Bools.requireNot;
import static net.splitcells.dem.data.set.list.Lists.toList;
import static net.splitcells.dem.resource.FileSystemViaClassResourcesFactoryImpl._fileSystemViaClassResourcesFactoryImpl;
import static net.splitcells.dem.resource.Files.readAsString;
import static net.splitcells.dem.testing.Assertions.requireEquals;
import static net.splitcells.dem.testing.TestTypes.INTEGRATION_TEST;
import static net.splitcells.dem.utils.StreamUtils.concat;

public class FileSystemViaClassResourcesTest extends TestSuiteI {

    @Tag(INTEGRATION_TEST)
    @TestFactory
    public Stream<DynamicTest> testStdFactory() {
        return testFactory(_fileSystemViaClassResourcesFactoryImpl());
    }

    public Stream<DynamicTest> testFactory(FileSystemViaClassResourcesFactoryApi factory) {
        return concat(dynamicTests(this::testInputStream, factory)
                , dynamicTests(this::testInputStreamForSubFileSystem, factory)
                , dynamicTests(this::testReadString, factory)
                , dynamicTests(this::testReadStringForSubFileSystem, factory)
                , dynamicTests(this::testExists, factory)
                , dynamicTests(this::testExistsForSubFileSystem, factory)
                , dynamicTests(this::testIsFile, factory)
                , dynamicTests(this::testIsFileForSubFileSystem, factory)
                , dynamicTests(this::testWalkRecursively, factory)
                , dynamicTests(this::testWalkRecursivelyOnFile, factory)
                , dynamicTests(this::testWalkRecursivelyForSubFileSystem, factory)
                , dynamicTests(this::testSubFileSystemView, factory)
                , dynamicTests(this::testReadFileAsBytes, factory));
    }

    public void testInputStream(FileSystemViaClassResourcesFactoryApi factory) {
        final var testSubject = factory.fileSystemViaClassResources(FileSystemViaClassResourcesTest.class
                , "net.splitcells", "dem.api");
        requireEquals(readAsString(testSubject.inputStream("src/main/resources/net/splitcells/dem/api/test-file.txt"))
                , "This is a test file of the 20th of July 2023.");
    }

    public void testInputStreamForSubFileSystem(FileSystemViaClassResourcesFactoryApi factory) {
        final var testSubject = factory.fileSystemViaClassResources(FileSystemViaClassResourcesTest.class
                        , "net.splitcells", "dem.api")
                .subFileSystemView("src/main/resources/net/splitcells/dem");
        requireEquals(readAsString(testSubject.inputStream("api/test-file.txt"))
                , "This is a test file of the 20th of July 2023.");
    }

    public void testReadString(FileSystemViaClassResourcesFactoryApi factory) {
        requireEquals(factory.fileSystemViaClassResources(FileSystemViaClassResourcesTest.class
                                , "net.splitcells", "dem.api")
                        .readString("src/main/resources/net/splitcells/dem/api/test-file.txt")
                , "This is a test file of the 20th of July 2023.");
    }

    public void testReadStringForSubFileSystem(FileSystemViaClassResourcesFactoryApi factory) {
        requireEquals(factory.fileSystemViaClassResources(FileSystemViaClassResourcesTest.class
                                , "net.splitcells", "dem.api")
                        .subFileSystemView("src/main/resources/net/splitcells/dem/api")
                        .readString("test-file.txt")
                , "This is a test file of the 20th of July 2023.");
    }

    public void testExists(FileSystemViaClassResourcesFactoryApi factory) {
        require(factory.fileSystemViaClassResources(FileSystemViaClassResourcesTest.class, "", "").exists());
    }

    public void testExistsForSubFileSystem(FileSystemViaClassResourcesFactoryApi factory) {
        require(factory.fileSystemViaClassResources(FileSystemViaClassResourcesTest.class
                        , "net.splitcells", "dem.api")
                .exists());
        require(factory.fileSystemViaClassResources(FileSystemViaClassResourcesTest.class
                        , "net.splitcells", "dem.api")
                .subFileSystemView("src/main/resources/net/splitcells/")
                .exists());
    }

    public void testIsFile(FileSystemViaClassResourcesFactoryApi factory) {
        final var testSubject = factory.fileSystemViaClassResources(FileSystemViaClassResourcesTest.class
                , "net.splitcells", "dem.api");
        require(testSubject.isFile("src/main/resources/net/splitcells/dem/api/test-file.txt"));
        requireNot(testSubject.isFile("src/main/resources/net/splitcells/dem/api/test-file-invalid.txt"));
        requireNot(testSubject.isFile("src/main/resources/net/splitcells/dem/api"));
    }

    public void testIsFileForSubFileSystem(FileSystemViaClassResourcesFactoryApi factory) {
        final var testSubject = factory.fileSystemViaClassResources(FileSystemViaClassResourcesTest.class
                        , "net.splitcells", "dem.api")
                .subFileSystemView("src/main/resources/net/");
        require(testSubject.isFile("splitcells/dem/api/test-file.txt"));
    }

    public void testWalkRecursively(FileSystemViaClassResourcesFactoryApi factory) {
        final var rootPath = "src/main/resources/net/splitcells/dem/resource/FileSystemViaClassResourcesTest/testWalkRecursively/";
        factory.fileSystemViaClassResources(FileSystemViaClassResourcesTest.class
                        , "net.splitcells", "dem.api")
                .walkRecursively(rootPath)
                .collect(toList())
                .requireContentsOf(Path.of(rootPath)
                        , Path.of(rootPath + "1")
                        , Path.of(rootPath + "1/2")
                        , Path.of(rootPath + "1/2/3")
                        , Path.of(rootPath + "1/2/3/test.txt")
                );
    }

    public void testWalkRecursivelyOnFile(FileSystemViaClassResourcesFactoryApi factory) {
        final var rootPath = "src/main/resources/net/splitcells/dem/resource/FileSystemViaClassResourcesTest/testWalkRecursively/another-test.txt";
        factory.fileSystemViaClassResources(FileSystemViaClassResourcesTest.class
                        , "net.splitcells", "dem.api")
                .walkRecursively(rootPath)
                .collect(toList())
                .requireContentsOf(Path.of(rootPath));
    }

    public void testWalkRecursivelyForSubFileSystem(FileSystemViaClassResourcesFactoryApi factory) {
        final var rootPath = "testWalkRecursively/";
        factory.fileSystemViaClassResources(FileSystemViaClassResourcesTest.class
                        , "net.splitcells", "dem.api")
                .subFileSystemView("src/main/resources/net/splitcells/dem/resource/FileSystemViaClassResourcesTest")
                .walkRecursively(rootPath)
                .collect(toList())
                .requireContentsOf(Path.of(rootPath)
                        , Path.of(rootPath + "1")
                        , Path.of(rootPath + "1/2")
                        , Path.of(rootPath + "1/2/3")
                        , Path.of(rootPath + "1/2/3/test.txt")
                );
    }

    public void testSubFileSystemView(FileSystemViaClassResourcesFactoryApi factory) {
        final var testSubject = factory.fileSystemViaClassResources(FileSystemViaClassResourcesTest.class
                        , "net.splitcells", "dem.api")
                .subFileSystemView("src/main/resources/net/splitcells");
        require(testSubject.isFile("dem/api/test-file.txt"));
    }

    public void testReadFileAsBytes(FileSystemViaClassResourcesFactoryApi factory) {
        final var testSubject = factory.fileSystemViaClassResources(FileSystemViaClassResourcesTest.class
                , "net.splitcells", "dem.api");
        requireEquals(new String(testSubject.readFileAsBytes("src/main/resources/net/splitcells/dem/api/test-file.txt"))
                , "This is a test file of the 20th of July 2023.");
    }
}
