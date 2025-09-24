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
package net.splitcells.gel.ext;

import net.splitcells.dem.Dem;
import net.splitcells.dem.ProcessResult;
import net.splitcells.dem.data.atom.Bools;
import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.environment.Cell;
import net.splitcells.dem.environment.Environment;
import net.splitcells.dem.environment.config.IsDeterministic;
import net.splitcells.dem.resource.ContentType;
import net.splitcells.dem.resource.Files;
import net.splitcells.dem.resource.communication.log.IsEchoToFile;
import net.splitcells.dem.resource.communication.log.LogLevel;
import net.splitcells.dem.resource.communication.log.MessageFilter;
import net.splitcells.dem.resource.host.ProcessPath;
import net.splitcells.dem.utils.random.DeterministicRootSourceSeed;
import net.splitcells.gel.data.lookup.LookupTables;
import net.splitcells.gel.data.table.TableMetaAspect;
import net.splitcells.gel.data.table.Tables;
import net.splitcells.gel.solution.SolutionAspect;
import net.splitcells.gel.solution.Solutions;
import net.splitcells.gel.solution.history.Histories;
import net.splitcells.gel.solution.history.HistoryRefFactory;
import net.splitcells.website.server.Config;
import net.splitcells.website.server.ServerService;
import net.splitcells.website.server.processor.BinaryMessage;
import net.splitcells.website.server.project.ProjectRenderer;
import net.splitcells.website.server.project.renderer.DiscoverableMediaRenderer;
import net.splitcells.website.server.project.renderer.DiscoverableRenderer;
import net.splitcells.website.server.project.renderer.ObjectsMediaRenderer;
import net.splitcells.website.server.project.renderer.ObjectsRenderer;

import java.util.Optional;
import java.util.function.Consumer;

import static net.splitcells.dem.Dem.environment;
import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.website.server.processor.BinaryMessage.binaryMessage;

public class GelExtCell implements Cell {
    public static void process(Runnable program) {
        process(program, standardDeveloperConfigurator());
    }

    public static ProcessResult process(Runnable program, Consumer<Environment> configurator) {
        return Dem.process(program
                , standardDeveloperConfigurator()
                        .andThen(setupProcessRepo())
                        .andThen(configurator));
    }

    public static Consumer<Environment> setupProcessRepo() {
        return config -> {
            Files.createDirectory(environment().config().configValue(ProcessPath.class));
        };
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
                    .withConfigValue(MessageFilter.class
                            , a -> a.path().equals(list("debugging")) || a.priority().greaterThanOrEqual(LogLevel.INFO))
                    .withConfigValue(IsEchoToFile.class, true)
                    .withConfigValue(IsDeterministic.class, Optional.of(Bools.truthful()))
                    .withConfigValue(DeterministicRootSourceSeed.class, 1000L);
            env.config().withConfigValue(Histories.class, new HistoryRefFactory());
            env.config().configValue(Tables.class).withAspect(TableMetaAspect::databaseIRef);
            env.config().configValue(Solutions.class).withAspect(SolutionAspect::solutionAspect);
            configureForWebserver(env);
        };
    }

    /**
     * TODO {@link ObjectsRenderer} are causing too many error logs and are therefore disabled for now.
     *
     * @param env
     */
    public static void configureForWebserver(Environment env) {
        env.config().withInitedOption(ServerService.class);
        if (false) {
            env.config().configValue(Tables.class)
                    .withConnector(database -> ObjectsRenderer.registerObject(new DiscoverableRenderer() {
                        @Override
                        public String render() {
                            return database.toHtmlTable().toHtmlString();
                        }

                        @Override
                        public Optional<String> title() {
                            return Optional.of(database.path().toString());
                        }

                        @Override
                        public List<String> path() {
                            return database.path();
                        }
                    }));
            env.config().configValue(Solutions.class)
                    .withConnector(solution -> ObjectsRenderer.registerObject(new DiscoverableRenderer() {
                        @Override
                        public String render() {
                            return solution.toHtmlTable().toHtmlString();
                        }

                        @Override
                        public Optional<String> title() {
                            return Optional.of(solution.path().toString());
                        }

                        @Override
                        public List<String> path() {
                            return solution.path();
                        }
                    }));
            env.config().configValue(Solutions.class)
                    .withConnector(solution -> ObjectsRenderer.registerObject(new DiscoverableRenderer() {
                        @Override
                        public String render() {
                            return solution.constraint().renderToHtml().toHtmlString();
                        }

                        @Override
                        public Optional<String> title() {
                            return Optional.of(solution.path().toString());
                        }

                        @Override
                        public List<String> path() {
                            return solution.path().withAppended("constraint");
                        }
                    }));
            env.config().configValue(Solutions.class)
                    .withConnector(solution -> ObjectsMediaRenderer.registerMediaObject(new DiscoverableMediaRenderer() {
                        @Override
                        public Optional<BinaryMessage> render(ProjectRenderer projectRenderer, Config config) {
                            return Optional.of(binaryMessage(solution.toCSV().getBytes(), ContentType.CSV.codeName()));
                        }

                        @Override
                        public List<String> path() {
                            final var path = solution.path().shallowCopy();
                            return path.withAppended(path.removeAt(path.size() - 1) + ".csv");
                        }
                    }));
            env.config().configValue(LookupTables.class).withConnector(lookupTable -> ObjectsRenderer.registerObject(new DiscoverableRenderer() {
                @Override
                public String render() {
                    return lookupTable.toHtmlTable().toHtmlString();
                }

                @Override
                public Optional<String> title() {
                    return Optional.of(lookupTable.path().toString());
                }

                @Override
                public List<String> path() {
                    return lookupTable.path();
                }
            }));
        }
    }

    @Override
    public String groupId() {
        return "net.splitcells";
    }

    @Override
    public String artifactId() {
        return "gel.ext";
    }
    @Override
    public void accept(Environment env) {

    }
}
