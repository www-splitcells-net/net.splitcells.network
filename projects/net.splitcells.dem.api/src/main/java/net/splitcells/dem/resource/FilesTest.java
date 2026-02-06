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

import lombok.val;
import net.splitcells.dem.lang.annotations.JavaLegacy;
import net.splitcells.dem.testing.annotations.UnitTest;

import java.io.IOException;
import java.nio.file.Path;

import static net.splitcells.dem.data.atom.Bools.require;
import static net.splitcells.dem.data.atom.Bools.requireNot;
import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.data.set.list.Lists.toList;
import static net.splitcells.dem.lang.tree.TreeI.tree;
import static net.splitcells.dem.resource.Files.*;
import static net.splitcells.dem.resource.Files.deleteDirectory;
import static net.splitcells.dem.resource.Paths.path;
import static net.splitcells.dem.testing.Assertions.requireEquals;
import static net.splitcells.dem.testing.Assertions.requireThrow;
import static net.splitcells.dem.utils.ExecutionException.execException;
import static net.splitcells.dem.utils.StringUtils.multiple;
import static net.splitcells.dem.utils.StringUtils.parseString;

@JavaLegacy
public class FilesTest {

    @UnitTest public void testGenerateFolderPath() {
        requireThrow(() -> {
            val testPath = Path.of("\\" + multiple("//\\\\", 2000));
            processInTemporaryFolder(p -> generateFolderPath(testPath));
        });
        processInTemporaryFolder(p -> {
            val testFolder = p.resolve("test-folder");
            generateFolderPath(testFolder);
            isDirectory(testFolder);
        });
    }

    @UnitTest public void testNewLine() {
        requireEquals(newLine(), "\n");
    }

    @UnitTest public void testDeleteDirectory() {
        requireThrow(() -> {
            processInTemporaryFolder(p -> {
                val testFolder = p.resolve("test-folder");
                val blockingFile = testFolder.resolve("blocking-file");
                generateFolderPath(testFolder);
                appendToFile(blockingFile, "def");
                deleteDirectory(blockingFile);
                deleteDirectory(testFolder);
            });
        });
        processInTemporaryFolder(p -> {
            val testFolder = p.resolve("test-folder");
            generateFolderPath(testFolder);
            isDirectory(testFolder);
            deleteDirectory(testFolder);
            requireNot(isDirectory(testFolder));
        });
    }

    @UnitTest public void testProcessInTemporaryFolderError() {
        requireThrow(() -> {
            processInTemporaryFolder(p -> {
                throw execException();
            });
        });
    }

    @UnitTest public void testCreateDirectory() {
        processInTemporaryFolder(p -> createDirectory(p.resolve("test")));
    }

    /**
     * The test tries to create an error by trying to create folder with a {@link Path},
     * that is valid in Java,
     * but it is not valid according to the operating system.
     */
    @UnitTest public void testCreateDirectoryInvalid() {
        requireThrow(() -> {
            val testPath = Path.of("\\" + multiple("//\\\\", 2000));
            processInTemporaryFolder(p -> createDirectory(testPath));
        });
    }

    @UnitTest public void testEnsureAbsenceWithInvalidPath() {
        ensureAbsence(Path.of("\\//"));
    }

    @UnitTest public void testEnsureAbsence() {
        processInTemporaryFolder(p -> {
            val testFolder = p.resolve("test");
            createDirectory(testFolder);
            require(folderExists(testFolder));
            ensureAbsence(testFolder);
            requireNot(folderExists(testFolder));
        });
    }

    @UnitTest public void testAppendToFile() {
        requireThrow(() -> {
            val testFile = Path.of("/not/existing/file/90348237852ß34785427839572");
            processInTemporaryFolder(p -> appendToFile(testFile, "abc"));
        });
        processInTemporaryFolder(p -> {
            val testFile = p.resolve("test-file");
            appendToFile(testFile, "def");
            appendToFile(testFile, "hij");
            requireEquals(readFileAsString(testFile), "defhij");
        });
    }

    @UnitTest public void testWriteToFile() {
        requireThrow(() -> processInTemporaryFolder(p -> {
            val testFile = p.resolve("not/existing/file/90348237852ß34785427839572");
            writeToFile(testFile, "abc");
        }));
        processInTemporaryFolder(p -> {
            val testFile = p.resolve("test-file");
            writeToFile(testFile, "def");
            writeToFile(testFile, "hij");
            requireEquals(readFileAsString(testFile), "hij");
        });
    }

    @UnitTest public void testReadFileAsString() {
        requireThrow(() -> processInTemporaryFolder(p -> {
            readFileAsString(p.resolve("not/existing/file/54b"));
        }));
        processInTemporaryFolder(p -> {
            val file = p.resolve("test-file");
            val content = "423t";
            writeToFile(file, content);
            requireEquals(readFileAsString(file), content);
        });
    }

    @UnitTest public void testReadFileAsBytes() {
        requireThrow(() -> processInTemporaryFolder(p -> {
            readFileAsBytes(p.resolve("not/existing/file/54c"));
        }));
        processInTemporaryFolder(p -> {
            val file1 = p.resolve("test-file-1");
            val file2 = p.resolve("test-file-2");
            val content = "2353";
            writeToFile(file1, content);
            writeToFile(file2, tree(content));
            requireEquals(parseString(readFileAsBytes(file1)), content);
        });
    }

    @UnitTest public void testNewInputStream() {
        processInTemporaryFolder(p -> {
            val file = p.resolve("test-file");
            val content = "3265";
            writeToFile(file, content);
            try {
                requireEquals(parseString(newInputStream(file).readAllBytes()), content);
            } catch (IOException e) {
                throw execException(e);
            }
        });
    }

    @UnitTest public void testWalkDirectChildren() {
        requireThrow(() -> {
            val testFile = Path.of("/not/existing/folder/90348237852ß34785427839572");
            processInTemporaryFolder(p -> walkDirectChildren(testFile));
        });
        processInTemporaryFolder(p -> {
            val testFile1 = p.resolve("test-file-1");
            val testFile2 = p.resolve("test-file-2");
            writeToFile(testFile1, "def");
            writeToFile(testFile2, "hij");
            requireEquals(walkDirectChildren(p).count(), 2L);
        });
    }

    @UnitTest public void testWalkRecursively() {
        requireThrow(() -> {
            val testFile = Path.of("/not/existing/folder/90348237852ß34785427839572");
            processInTemporaryFolder(p -> walkRecursively(testFile));
        });
        processInTemporaryFolder(p -> {
            val directory1 = p.resolve("directory-1");
            val directory2 = directory1.resolve("directory-2");
            createDirectory(directory1);
            createDirectory(directory2);
            val testFile1 = directory1.resolve("test-file-1");
            val testFile2 = directory2.resolve("test-file-2");
            val testFile3 = p.resolve("test-file-3");

            writeToFile(testFile1, "def");
            writeToFile(testFile2, "hij");
            writeToFile(testFile3, "klm");
            requireEquals(walkRecursively(directory1).collect(toList()), list(directory1, testFile1, directory2, testFile2));
        });
    }

    @UnitTest public void testCopyDirectory() {
        requireThrow(() -> {
            val testFile = Path.of("/not/existing/folder/90348237852ß34785427839572");
            processInTemporaryFolder(p -> copyDirectory(testFile, testFile.resolve("something")));
        });
        processInTemporaryFolder(p -> {
            val sourceFolder = p.resolve("source");
            createDirectory(sourceFolder);
            val testFile1 = sourceFolder.resolve("test-file-1");
            val testFile2 = sourceFolder.resolve("test-file-2");
            writeToFile(testFile1, "def");
            writeToFile(testFile2, "hij");
            val targetFolder = p.resolve("target");
            createDirectory(targetFolder);
            copyDirectory(sourceFolder, targetFolder);
            requireEquals(walkDirectChildren(targetFolder).count(), 2L);
        });
    }

    @UnitTest public void testCopyFileFrom() {
        requireThrow(() -> processInTemporaryFolder(p -> {
            val source = p.resolve("not/existing/folder/90348237852ß34785427839572");
            val target = p.resolve("not/existing/folder/1254325");
            copyFileFrom(source, target);
        }));
        processInTemporaryFolder(p -> {
            val content = "klm";
            val source = p.resolve("source");
            val target = p.resolve("target");
            writeToFile(source, content);
            copyFileFrom(source, target);
            requireEquals(readFileAsString(target), content);
        });
    }
}
