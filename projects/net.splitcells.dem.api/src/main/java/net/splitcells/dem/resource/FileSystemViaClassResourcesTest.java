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

import net.splitcells.dem.environment.config.StaticFlags;
import net.splitcells.dem.testing.TestSuiteI;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.TestFactory;

import java.nio.file.Path;
import java.util.stream.Stream;

import static net.splitcells.dem.Dem.MAVEN_GROUP_ID;
import static net.splitcells.dem.DemApiFileSystem.DEM_API;
import static net.splitcells.dem.data.atom.Bools.require;
import static net.splitcells.dem.data.atom.Bools.requireNot;
import static net.splitcells.dem.data.set.list.Lists.toList;
import static net.splitcells.dem.environment.config.StaticFlags.runWithEnforcingUnityConsistency;
import static net.splitcells.dem.resource.FileSystemViaClassResourcesFactoryImpl._fileSystemViaClassResourcesFactoryImpl;
import static net.splitcells.dem.resource.Files.readAsString;
import static net.splitcells.dem.testing.Assertions.*;
import static net.splitcells.dem.testing.TestTypes.INTEGRATION_TEST;
import static net.splitcells.dem.testing.TestTypes.UNIT_TEST;
import static net.splitcells.dem.utils.StreamUtils.concat;

public class FileSystemViaClassResourcesTest extends TestSuiteI {
    private static final String TEST_FILE_PATH = "src/main/resources/net/splitcells/dem/api/test-file.txt";
    private static final String NOT_EXISTING_FOLDER = "not/existing/folder";
    private static final String TEST_FILE_CONTENT = "This is a test file of the 20th of July 2023.";
    private static final String DEM_API_FOLDER = "src/main/resources/net/splitcells/dem/api";

    @Tag(UNIT_TEST)
    @TestFactory
    public Stream<DynamicTest> testStdFactory() {
        return testFactory(_fileSystemViaClassResourcesFactoryImpl());
    }

    public Stream<DynamicTest> testFactory(FileSystemViaClassResourcesFactoryApi factory) {
        return concat(dynamicTests(this::testInputStream, factory)
                , dynamicTests(this::testInputStreamForSubFileSystem, factory)
                , dynamicTests(this::testReadString, factory)
                , dynamicTests(this::testReadStringIfPresent, factory)
                , dynamicTests(this::testReadStringIfPresentWithoutUnitEnforcement, factory)
                , dynamicTests(this::testReadStringForSubFileSystem, factory)
                , dynamicTests(this::testExists, factory)
                , dynamicTests(this::testExistsForSubFileSystem, factory)
                , dynamicTests(this::testIsFile, factory)
                , dynamicTests(this::testIsDirectory, factory)
                , dynamicTests(this::testIsFileForSubFileSystem, factory)
                , dynamicTests(this::testWalkRecursively, factory)
                , dynamicTests(this::testWalkRecursivelyOnFile, factory)
                , dynamicTests(this::testWalkRecursivelyForSubFileSystem, factory)
                , dynamicTests(this::testSubFileSystemView, factory)
                , dynamicTests(this::testReadFileAsBytes, factory)
                , dynamicTests(this::testInvalidConstruction, factory));
    }

    public void testInvalidConstruction(FileSystemViaClassResourcesFactoryApi factory) {
        factory.fileSystemViaClassResources(FileSystemViaClassResourcesTest.class, "invalid-group", "invalid-artifact");
    }

    public void testInputStream(FileSystemViaClassResourcesFactoryApi factory) {
        final var testSubject = factory.fileSystemViaClassResources(FileSystemViaClassResourcesTest.class
                , MAVEN_GROUP_ID, DEM_API);
        requireEquals(readAsString(testSubject.inputStream(TEST_FILE_PATH))
                , TEST_FILE_CONTENT);
    }

    public void testInputStreamForSubFileSystem(FileSystemViaClassResourcesFactoryApi factory) {
        final var testSubject = factory.fileSystemViaClassResources(FileSystemViaClassResourcesTest.class
                        , MAVEN_GROUP_ID, DEM_API)
                .subFileSystemView("src/main/resources/net/splitcells/dem");
        requireEquals(readAsString(testSubject.inputStream("api/test-file.txt"))
                , TEST_FILE_CONTENT);
    }

    public void testReadString(FileSystemViaClassResourcesFactoryApi factory) {
        requireEquals(factory.fileSystemViaClassResources(FileSystemViaClassResourcesTest.class
                                , MAVEN_GROUP_ID, DEM_API)
                        .readString(TEST_FILE_PATH)
                , TEST_FILE_CONTENT);
        requireThrow(() -> factory.fileSystemViaClassResources(FileSystemViaClassResourcesTest.class
                        , MAVEN_GROUP_ID, DEM_API)
                .readString(NOT_EXISTING_FOLDER));
    }

    public void testReadStringIfPresent(FileSystemViaClassResourcesFactoryApi factory) {
        requireEquals(factory.fileSystemViaClassResources(FileSystemViaClassResourcesTest.class
                                , MAVEN_GROUP_ID, DEM_API)
                        .readStringIfPresent(Path.of(TEST_FILE_PATH)).orElseThrow()
                , TEST_FILE_CONTENT);
        requireAbsenceOf(factory.fileSystemViaClassResources(FileSystemViaClassResourcesTest.class
                        , MAVEN_GROUP_ID, DEM_API)
                .readStringIfPresent(Path.of("not existent file path")));
    }

    public void testReadStringIfPresentWithoutUnitEnforcement(FileSystemViaClassResourcesFactoryApi factory) {
        runWithEnforcingUnityConsistency(false, () -> {
            testReadStringIfPresent(factory);
        });
    }

    public void testReadStringForSubFileSystem(FileSystemViaClassResourcesFactoryApi factory) {
        requireEquals(factory.fileSystemViaClassResources(FileSystemViaClassResourcesTest.class
                                , MAVEN_GROUP_ID, DEM_API)
                        .subFileSystemView(DEM_API_FOLDER)
                        .readString("test-file.txt")
                , TEST_FILE_CONTENT);
    }

    public void testExists(FileSystemViaClassResourcesFactoryApi factory) {
        require(factory.fileSystemViaClassResources(FileSystemViaClassResourcesTest.class
                        , MAVEN_GROUP_ID, DEM_API)
                .exists());
    }

    public void testExistsForSubFileSystem(FileSystemViaClassResourcesFactoryApi factory) {
        require(factory.fileSystemViaClassResources(FileSystemViaClassResourcesTest.class
                        , MAVEN_GROUP_ID, DEM_API)
                .subFileSystemView("src/main/resources/net/splitcells/")
                .exists());
    }

    public void testIsFile(FileSystemViaClassResourcesFactoryApi factory) {
        final var testSubject = factory.fileSystemViaClassResources(FileSystemViaClassResourcesTest.class
                , MAVEN_GROUP_ID, DEM_API);
        require(testSubject.isFile(TEST_FILE_PATH));
        requireNot(testSubject.isFile("src/main/resources/net/splitcells/dem/api/test-file-not-existing.txt"));
        requireNot(testSubject.isFile(DEM_API_FOLDER));
    }

    public void testIsDirectory(FileSystemViaClassResourcesFactoryApi factory) {
        final var testSubject = factory.fileSystemViaClassResources(FileSystemViaClassResourcesTest.class
                , MAVEN_GROUP_ID, DEM_API);
        requireNot(testSubject.isDirectory(TEST_FILE_PATH));
        requireNot(testSubject.isDirectory("src/main/resources/net/splitcells/dem/api/test-file-not-existing.txt"));
        require(testSubject.isDirectory(DEM_API_FOLDER));
        requireNot(testSubject.isDirectory("src/main/resources/net/splitcells/dem/api-not-existing"));
    }

    public void testIsFileForSubFileSystem(FileSystemViaClassResourcesFactoryApi factory) {
        final var testSubject = factory.fileSystemViaClassResources(FileSystemViaClassResourcesTest.class
                        , MAVEN_GROUP_ID, DEM_API)
                .subFileSystemView("src/main/resources/net/");
        require(testSubject.isFile("splitcells/dem/api/test-file.txt"));
    }

    public void testWalkRecursively(FileSystemViaClassResourcesFactoryApi factory) {
        final var rootPath = "src/main/resources/net/splitcells/dem/resource/FileSystemViaClassResourcesTest" +
                "/testWalkRecursively/";
        factory.fileSystemViaClassResources(FileSystemViaClassResourcesTest.class
                        , MAVEN_GROUP_ID, DEM_API)
                .walkRecursively(rootPath)
                .collect(toList())
                .requireContentsOf(Path.of(rootPath)
                        , Path.of(rootPath + "1")
                        , Path.of(rootPath + "1/2")
                        , Path.of(rootPath + "1/2/3")
                        , Path.of(rootPath + "1/2/3/test.txt")
                );
        factory.fileSystemViaClassResources(FileSystemViaClassResourcesTest.class
                        , MAVEN_GROUP_ID, DEM_API)
                .walkRecursively(Path.of(rootPath))
                .collect(toList())
                .requireContentsOf(Path.of(rootPath)
                        , Path.of(rootPath + "1")
                        , Path.of(rootPath + "1/2")
                        , Path.of(rootPath + "1/2/3")
                        , Path.of(rootPath + "1/2/3/test.txt")
                );
        factory.fileSystemViaClassResources(FileSystemViaClassResourcesTest.class
                        , MAVEN_GROUP_ID, DEM_API)
                .walkRecursively()
                .collect(toList())
                .requireContentsOf(Path.of(rootPath)
                        , Path.of(rootPath + "1")
                        , Path.of(rootPath + "1/2")
                        , Path.of(rootPath + "1/2/3")
                        , Path.of(rootPath + "1/2/3/test.txt")
                );
        factory.fileSystemViaClassResources(FileSystemViaClassResourcesTest.class, MAVEN_GROUP_ID, DEM_API)
                .walkRecursively().collect(toList()).requireAnyContent();
    }

    public void testWalkRecursivelyOnFile(FileSystemViaClassResourcesFactoryApi factory) {
        final var rootPath = "src/main/resources/net/splitcells/dem/resource/FileSystemViaClassResourcesTest" +
                "/testWalkRecursively/another-test.txt";
        factory.fileSystemViaClassResources(FileSystemViaClassResourcesTest.class
                        , MAVEN_GROUP_ID, DEM_API)
                .walkRecursively(rootPath)
                .collect(toList())
                .requireContentsOf(Path.of(rootPath));
    }

    public void testWalkRecursivelyForSubFileSystem(FileSystemViaClassResourcesFactoryApi factory) {
        final var rootPath = "testWalkRecursively/";
        factory.fileSystemViaClassResources(FileSystemViaClassResourcesTest.class
                        , MAVEN_GROUP_ID, DEM_API)
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
                        , MAVEN_GROUP_ID, DEM_API)
                .subFileSystemView("src/main/resources/net/splitcells");
        require(testSubject.isFile("dem/api/test-file.txt"));
    }

    public void testReadFileAsBytes(FileSystemViaClassResourcesFactoryApi factory) {
        final var testSubject = factory.fileSystemViaClassResources(FileSystemViaClassResourcesTest.class
                , MAVEN_GROUP_ID, DEM_API);
        requireEquals(new String(testSubject.readFileAsBytes(TEST_FILE_PATH))
                , TEST_FILE_CONTENT);
        requireThrow(() -> new String(testSubject.readFileAsBytes(NOT_EXISTING_FOLDER)));
    }
}
