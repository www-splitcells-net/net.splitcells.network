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
import net.splitcells.dem.data.atom.Bool;
import net.splitcells.dem.data.atom.Bools;
import net.splitcells.dem.lang.annotations.JavaLegacyArtifact;
import net.splitcells.dem.testing.Assertions;
import net.splitcells.dem.testing.annotations.UnitTest;

import java.nio.file.Path;

import static net.splitcells.dem.data.atom.Bools.require;
import static net.splitcells.dem.data.atom.Bools.requireNot;
import static net.splitcells.dem.resource.Files.*;
import static net.splitcells.dem.resource.Files.ensureAbsence;
import static net.splitcells.dem.testing.Assertions.requireThrow;
import static net.splitcells.dem.utils.ExecutionException.execException;

@JavaLegacyArtifact
public class FilesTest {

    @UnitTest public void testProcessInTemporaryFolderError() {
        requireThrow(() -> {
            processInTemporaryFolder(p -> {
                throw execException();
            });
        });
    }

    @UnitTest public void testCreateDirectory() {
        processInTemporaryFolder(p -> createDirectory(Path.of("test")));
    }

    @UnitTest public void testCreateDirectoryInvalid() {
        requireThrow(() -> {
            processInTemporaryFolder(p -> createDirectory(Path.of("//\\")));
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
}
