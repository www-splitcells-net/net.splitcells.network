/*
 * Copyright (c) 2021 Mārtiņš Avots (Martins Avots) and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0, or the MIT License,
 * which is available at https://spdx.org/licenses/MIT.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR MIT
 */
package net.splitcells.gel;

import net.splitcells.dem.Dem;
import net.splitcells.dem.ProcessResult;
import net.splitcells.dem.environment.Environment;
import net.splitcells.dem.lang.Xml;
import net.splitcells.dem.resource.Paths;
import net.splitcells.dem.resource.Files;
import net.splitcells.dem.resource.host.ProcessPath;
import net.splitcells.gel.data.allocation.Allocationss;
import net.splitcells.gel.data.allocations.AllocationsIRefFactory;
import net.splitcells.gel.data.database.DatabaseMetaAspect;
import net.splitcells.gel.data.database.Databases;
import net.splitcells.gel.data.lookup.LookupRefFactory;
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

    public static ProcessResult analyseProcess(Runnable program, Consumer<Environment> configurator) {
        return Dem.process(() -> {
            Files.createDirectory(environment().config().configValue(ProcessPath.class));
            writeToFile(environment().config().configValue(ProcessPath.class).resolve("index.xml"), Xml.rElement(SEW, "article"));
            program.run();
            try {
                // Wait in order for log files to be written completely.
                Thread.sleep(3_000L);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }, configurator);
    }

    public static ProcessResult process(Runnable program, Consumer<Environment> configurator) {
        return Dem.process(() -> {
            program.run();
            try {
                // Wait in order for log files to be written completely.
                Thread.sleep(3_000L);
            } catch (InterruptedException e) {
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
                    .withConfigValue(Lookups.class, new LookupRefFactory());
            env.config().configValue(Databases.class).withAspect(DatabaseMetaAspect::databaseIRef);
            env.config().configValue(Solutions.class).withAspect(SolutionAspect::solutionAspect);
        };
    }
}
