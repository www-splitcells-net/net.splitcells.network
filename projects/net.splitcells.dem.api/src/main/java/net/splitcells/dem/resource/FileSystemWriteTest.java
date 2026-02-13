/*
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.dem.resource;

import net.splitcells.dem.testing.TestSuiteI;

import java.util.function.Supplier;

import static net.splitcells.dem.data.atom.Bools.require;

public class FileSystemWriteTest extends TestSuiteI {

    public void testExists(Supplier<FileSystem> factory) {
        require(factory.get().exists());
    }

    public void testExistsForSubFileSystem(Supplier<FileSystem> factory) {
        require(factory.get().createDirectoryPath("test").subFileSystem("test").exists());
    }
}
