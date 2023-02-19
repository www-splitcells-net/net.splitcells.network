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
package net.splitcells.gel;

import net.splitcells.dem.ProcessResult;
import net.splitcells.dem.data.atom.Bools;
import net.splitcells.dem.environment.Environment;
import net.splitcells.dem.environment.config.IsDeterministic;
import net.splitcells.dem.resource.Paths;
import net.splitcells.dem.resource.host.ProcessHostPath;
import net.splitcells.dem.resource.communication.log.IsEchoToFile;
import net.splitcells.dem.resource.communication.log.MessageFilter;
import net.splitcells.dem.utils.random.DeterministicRootSourceSeed;
import net.splitcells.gel.test.functionality.ColloquiumPlanningTest;

import java.util.Optional;
import java.util.function.Consumer;

import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.utils.ConstructorIllegal.constructorIllegal;
import static net.splitcells.gel.GelEnv.process;

public final class GelDev {
    private GelDev() {
        throw constructorIllegal();
    }

    public static void process(Runnable program) {
        GelEnv.process(program, standardDeveloperConfigurator());
    }

    public static ProcessResult process(Runnable program, Consumer<Environment> configurator) {
        return GelEnv.analyseProcess(program, standardDeveloperConfigurator().andThen(configurator));
    }

    public static Consumer<Environment> standardDeveloperConfigurator() {
        return GelEnv.standardDeveloperConfigurator().andThen(env -> {
            env.config()
                    .withConfigValue(MessageFilter.class
                            , a -> a.path().equals(list("debugging")))
                    .withConfigValue(IsEchoToFile.class, true)
                    .withConfigValue(IsDeterministic.class, Optional.of(Bools.truthful()))
                    .withConfigValue(DeterministicRootSourceSeed.class, 1000L);
        });
    }
}
