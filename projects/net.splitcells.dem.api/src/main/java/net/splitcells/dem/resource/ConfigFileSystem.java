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

import net.splitcells.dem.Dem;
import net.splitcells.dem.environment.config.framework.Option;

import static net.splitcells.dem.resource.FileSystemVoid.fileSystemVoid;

/**
 * <p>This {@link FileSystem} is the main file storage for the program's configuration.
 * This is not used to persist the program's state or to store user data.
 * The reason behind this segregation is the fact,
 * that the user's rights to access user data,
 * should not have any effect to the user's rights to access config data.
 * One could say, that access to the config files is more security sensitive as the access to the user files,
 * because the former may influence the latter.</p>
 * <p>If the {@link ConfigFileSystem} is based on another {@link FileSystem},
 * then only {@link BootstrapFileSystem} is allowed to be used.</p>
 */
public class ConfigFileSystem implements Option<FileSystem> {
    @Override
    public FileSystem defaultValue() {
        return Dem.configValue(BootstrapFileSystem.class).subFileSystem("config");
    }
}
