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

import net.splitcells.dem.environment.config.ProgramName;
import net.splitcells.dem.lang.annotations.JavaLegacyArtifact;
import net.splitcells.dem.lang.annotations.JavaLegacyBody;
import net.splitcells.dem.lang.tree.Tree;
import org.apache.commons.io.FileUtils;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static java.nio.file.Files.createDirectories;
import static net.splitcells.dem.lang.tree.XmlConfig.xmlConfig;
import static net.splitcells.dem.utils.ExecutionException.execException;

/**
 * <p>Some additional methods for the java.nio.file.Files class.</p>
 * <p>This class is an integration of OS functionality.
 * This class should not be used in code directly most of the time.
 * Use {@link FileSystem} instead.</p>
 */
@JavaLegacyArtifact
public interface Files {

    static void processInTemporaryFolder(Consumer<Path> process) {
        try {
            final var temporaryFolder = java.nio.file.Files.createTempDirectory(null);
            try {
                process.accept(temporaryFolder);
            } finally {
                Files.deleteDirectory(temporaryFolder);
            }
        } catch (Throwable t) {
            throw execException(t);
        }
    }

    static void createDirectory(Path directory) {
        try {
            createDirectories(directory);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    static void ensureAbsence(Path path) {
        if (isDirectory(path)) {
            deleteDirectory(path);
        }
    }

    static boolean is_file(Path path) {
        return java.nio.file.Files.isRegularFile(path);
    }

    static Stream<Path> walkDirectChildren(Path path) {
        try {
            return java.nio.file.Files.walk(path, 1).filter(e -> !path.equals(e));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    static Stream<Path> walk_recursively(Path path) {
        try {
            return java.nio.file.Files.walk(path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    static boolean isDirectory(Path path) {
        return java.nio.file.Files.isDirectory(path);
    }

    /**
     * IDEA Create method to move path to trash instead of deleting it.
     */
    static void deleteDirectory(Path dirToDelete) {
        try {
            FileUtils.deleteDirectory(dirToDelete.toFile());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    static void copyDirectory(Path sourceDirectory, Path targetDirectory) {
        try {
            FileUtils.copyDirectory(sourceDirectory.toFile(), targetDirectory.toFile());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    static void writeToFile(Path path, Tree content) {
        writeToFile(path, content.toXmlString(xmlConfig()));
    }

    static void writeToFile(Path path, byte[] content) {
        try (final var writer = new FileOutputStream(path.toFile())) {
            writer.write(content);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    static void writeToFile(Path path, String string) {
        writeToFile(path, string.getBytes());
    }

    /**
     * TODO How do we handle new line symbols on all platforms?
     *
     * @return New Line Symbol
     */
    static String newLine() {
        return "\n";
    }

    static void appendToFile(Path path, byte[] content) {
        try (final var writer = new FileOutputStream(path.toFile(), true)) {
            writer.write(content);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    static void appendToFile(Path path, String content) {
        appendToFile(path, content.getBytes(StandardCharsets.UTF_8));
    }

    static String readFileAsString(Path path) {
        try {
            return java.nio.file.Files.readString(path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    static byte[] readFileAsBytes(Path path) {
        try {
            return java.nio.file.Files.readAllBytes(path);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Deprecated
    static InputStream newInputStream(Path path) {
        try {
            return java.nio.file.Files.newInputStream(path);
        } catch (IOException e) {
            throw execException(e);
        }
    }

    static boolean fileExists(Path path) {
        return java.nio.file.Files.isRegularFile(path);
    }

     static boolean folderExists(Path path) {
        return java.nio.file.Files.isDirectory(path);
    }

    static String readString(Path path) {
        return readFileAsString(path);
    }

    static void copyFileFrom(Path source, Path target) {
        try {
            com.google.common.io.Files.copy(source.toFile(), target.toFile());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    static void generateFolderPath(Path targetFolderDescription) {
        try {
            createDirectories(targetFolderDescription);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * <p>TODO Use something that is derived from {@link BootstrapFileSystem}.</p>
     * <p>Folder containing the user's temporary (`~/.local/state/&lt;ProgramName&gt;`) files for this program based on {@link ProgramName}.
     * This is based on the <a href="https://specifications.freedesktop.org/basedir-spec/basedir-spec-latest.html">XDG Base Directory Specification</a>.
     * The format there should abide by the <a href="https://splitcells.net/net/splitcells/network/guidelines/filesystem.html">Software Project File System Standards</a>.
     * </p>
     * <p>By execution this method the corresponding folders are created, if these are not already present.</p>
     *
     * @return Returns the user's state files.
     */
    static Path usersStateFiles() {
        final var usersStateFiles = Paths.userHome().resolve(".local/state/net.splitcells.dem");
        generateFolderPath(usersStateFiles);
        return usersStateFiles;
    }

    static String readAsString(InputStream inputStream) {
        try {
            return new String(inputStream.readAllBytes());
        } catch (IOException e) {
            throw execException(e);
        }
    }
}
