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

import net.splitcells.dem.environment.config.framework.Option;

import static net.splitcells.dem.resource.FileSystemVoid.fileSystemVoid;

/**
 * <p>This {@link FileSystem} is the main storage and is used to persist the program's state.
 * It is the default storage for all files created and permanently owned by the program.
 * Files stored in other {@link FileSystem} should be considered to be owned by other external programs.</p>
 * <p>If the {@link MainFileSystem} is based on another {@link FileSystem},
 * then only {@link BootstrapFileSystem} is allowed to be used.
 * The split is used, in order to separate the content of the program's state from the backend of the state storage.</p>
 */
public class MainFileSystem implements Option<FileSystem> {
    @Override
    public FileSystem defaultValue() {
        return fileSystemVoid();
    }
}
