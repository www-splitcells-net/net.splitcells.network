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

import net.splitcells.dem.Dem;
import net.splitcells.dem.ProcessResult;
import net.splitcells.dem.environment.Environment;
import net.splitcells.dem.lang.Xml;
import net.splitcells.dem.resource.Files;
import net.splitcells.dem.resource.host.ProcessPath;
import net.splitcells.gel.data.allocation.Allocationss;
import net.splitcells.gel.data.allocations.AllocationsIRefFactory;
import net.splitcells.gel.data.database.DatabaseMetaAspect;
import net.splitcells.gel.data.database.Databases;
import net.splitcells.gel.data.lookup.LookupFactory;
import net.splitcells.gel.data.lookup.LookupIFactory;
import net.splitcells.gel.data.lookup.Lookups;
import net.splitcells.gel.solution.SolutionAspect;
import net.splitcells.gel.solution.Solutions;
import net.splitcells.gel.solution.history.Histories;
import net.splitcells.gel.solution.history.HistoryRefFactory;

import java.util.function.Consumer;

import static net.splitcells.dem.Dem.environment;
import static net.splitcells.dem.lang.namespace.NameSpaces.SEW;
import static net.splitcells.dem.resource.Files.writeToFile;
import static net.splitcells.dem.utils.ConstructorIllegal.constructorIllegal;

public final class GelEnv {
    private GelEnv() {
        throw constructorIllegal();
    }

    public static void process(Runnable program) {
        process(program, standardDeveloperConfigurator());
    }

    public static ProcessResult process(Runnable program, Consumer<Environment> configurator) {
        return Dem.process(() -> {
            program.run();
            try {
                // Wait in order for log files to be written completely.
                Thread.sleep(3_000L);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException(e);
            }
        }, configurator);
    }

    /**
     * Uses a folder of the user in order to store files, in order to prevent unnecessary file changes
     * in the project repo, if the user executes Gel with an IDE and default settings.
     *
     * @return
     */
    public static Consumer<Environment> standardDeveloperConfigurator() {
        return env -> {
            env.config()
                    .withConfigValue(Histories.class, new HistoryRefFactory())
                    .withConfigValue(Allocationss.class, new AllocationsIRefFactory())
                    .withConfigValue(Lookups.class, new LookupIFactory());
            env.config().configValue(Databases.class).withAspect(DatabaseMetaAspect::databaseIRef);
            env.config().configValue(Solutions.class).withAspect(SolutionAspect::solutionAspect);
        };
    }
}
