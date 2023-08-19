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
package net.splitcells.network.worker;

import net.splitcells.dem.Dem;
import net.splitcells.dem.environment.config.ProgramName;
import net.splitcells.dem.resource.FileSystems;
import net.splitcells.dem.resource.host.SystemUtils;

import static net.splitcells.dem.Dem.config;
import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.resource.FileSystems.fileSystemOnLocalHost;
import static net.splitcells.dem.resource.Paths.userHome;
import static net.splitcells.dem.testing.Test.testUnits;
import static net.splitcells.dem.utils.ConstructorIllegal.constructorIllegal;
import static net.splitcells.network.worker.Logger.logger;

/**
 * Executes tests and logs their result.
 */
public class Builder {
    public static void main(String... args) {
        if (args.length != 1) {
            throw new IllegalArgumentException("Exactly one argument is required, but " + args.length + " were given. The argument is the id of the test executor.");
        }
        Dem.process(() -> testUnits(list(logger()))
                , env -> env.config().withConfigValue(ProgramName.class, args[0])
                        .withConfigValue(NetworkLog.class
                                , fileSystemOnLocalHost(config()
                                        .configValue(ProjectsFolder.class)
                                        .resolve("net.splitcells.network.log"))));
    }

    private Builder() {
        throw constructorIllegal();
    }
}
