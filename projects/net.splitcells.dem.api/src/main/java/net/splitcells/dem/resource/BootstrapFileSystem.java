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
import net.splitcells.dem.environment.config.framework.Option;

import static net.splitcells.dem.Dem.configValue;

/**
 * <p>This is the generic default storage for all files created and permanently owned by the program.
 * Files stored in any other {@link FileSystem} are considered to be owned by other external programs.
 * Keep in mind, that some {@link FileSystem}s are {@link FileSystem#subFileSystem(String)}
 * of {@link BootstrapFileSystem} and these are logically considered to be owned by the program as well.
 * Of course, this {@link FileSystem} does not have to be backed by an actual {@link FileSystem} on the local computer.
 * It could also be backed by a remote file system or even a database.</p>
 * <p>If files are stored for a specific use, consider creating a new {@link FileSystem} {@link Option},
 * that is a {@link FileSystem#subFileSystem(String)} of {@link BootstrapFileSystem}.
 * See {@link ConfigFileSystem} as an example.</p>
 * <p>This {@link FileSystem} can be thought to be the backend for other {@link FileSystem}s.
 * The split is used, in order to separate the content of the program's persistence layer from the
 * file based backend of the persistence layer.
 * For instance, the {@link ConfigFileSystem} is in the subfolder "config" of {@link BootstrapFileSystem} by default.
 * If the {@link BootstrapFileSystem} is backed by a checked out git repo,
 * the `.git` folder of the git repo is visible in {@link BootstrapFileSystem},
 * but may not be visible in any {@link FileSystem#subFileSystem(String)}.</p>
 */
public class BootstrapFileSystem implements Option<FileSystem> {
    @Override
    public FileSystem defaultValue() {
        return FileSystems.fileSystemOnLocalHost(Paths.userHome(".local", "state", configValue(ProgramName.class)));
    }
}
