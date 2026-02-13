/*
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.dem.resource;

import net.splitcells.dem.testing.TestSuiteI;

import java.nio.file.Path;
import java.util.function.Supplier;

import static net.splitcells.dem.data.atom.Bools.require;

public class FileSystemWriteTest extends TestSuiteI {

    public void testExists(Supplier<FileSystem> factory) {
        require(factory.get().exists());
    }

    /**
     * TODO {@link FileSystemViaMemory#subFileSystem(Path)} has to be implemented first.
     * 
     * @param factory
     */
    public void testExistsForSubFileSystem(Supplier<FileSystem> factory) {
        require(factory.get().createDirectoryPath("test").subFileSystem("test").exists());
    }
}
