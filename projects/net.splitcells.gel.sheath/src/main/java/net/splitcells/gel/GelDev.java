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
import net.splitcells.dem.data.atom.Bools;
import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.environment.Environment;
import net.splitcells.dem.environment.config.IsDeterministic;
import net.splitcells.dem.resource.ContentType;
import net.splitcells.dem.resource.Paths;
import net.splitcells.dem.resource.host.ProcessHostPath;
import net.splitcells.dem.resource.communication.log.IsEchoToFile;
import net.splitcells.dem.resource.communication.log.MessageFilter;
import net.splitcells.dem.utils.random.DeterministicRootSourceSeed;
import net.splitcells.gel.data.database.Databases;
import net.splitcells.gel.solution.Solutions;
import net.splitcells.gel.test.functionality.ColloquiumPlanningTest;
import net.splitcells.website.server.Config;
import net.splitcells.website.server.project.ProjectRenderer;
import net.splitcells.website.server.project.RenderingResult;
import net.splitcells.website.server.project.renderer.DiscoverableMediaRenderer;
import net.splitcells.website.server.project.renderer.DiscoverableRenderer;
import net.splitcells.website.server.project.renderer.ObjectsMediaRenderer;
import net.splitcells.website.server.project.renderer.ObjectsRenderer;

import java.util.Optional;
import java.util.concurrent.Semaphore;
import java.util.function.Consumer;

import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.resource.Files.readFileAsString;
import static net.splitcells.dem.resource.Paths.userHome;
import static net.splitcells.dem.utils.ConstructorIllegal.constructorIllegal;
import static net.splitcells.gel.GelEnv.process;
import static net.splitcells.website.Projects.projectsRenderer;
import static net.splitcells.website.server.project.ProjectRenderer.projectRenderer;
import static net.splitcells.website.server.project.RenderingResult.renderingResult;
import static net.splitcells.website.server.project.validator.SourceValidatorViaSchema.validatorViaSchema;

public final class GelDev {
    private GelDev() {
        throw constructorIllegal();
    }

    public static void process(Runnable program) {
        GelEnv.process(program, standardDeveloperConfigurator());
    }

    public static ProcessResult process(Runnable program, Consumer<Environment> configurator) {
        return GelEnv.analyseProcess(() -> {
            Dem.process(program, () -> {
                // TODO This is a hack, because the webserver still depends on private documents, in order to render the website.
                final var publicProjectRepository = userHome(
                        "Documents/projects/net.splitcells.martins.avots.support.system/public/net.splitcells.network/projects");
                final var privateProjectRepository = userHome("Documents/projects/net.splitcells.martins.avots.support.system/private");
                final var xslLib = publicProjectRepository.resolve(
                        "net.splitcells.website.default.content/src/main/xsl/net/splitcells/website/den/translation/to/html/");
                final var validator = validatorViaSchema(
                        publicProjectRepository.resolve("net.splitcells.website.server").resolve("src/main/xsd/den.xsd"));
                final var config = Config.create()
                        .withOpenPort(8448)
                        .withIsSecured(false)
                        .withSiteFolder(Optional.of(userHome("Documents/projects/net.splitcells.martins.avots.support.system/public/net.splitcells.network/projects/").toString()))
                        .withDetailedXslMenu(Optional.of(readFileAsString(userHome("Documents/projects/net.splitcells.martins.avots.support.system/private/net.splitcells.martins.avots.website/src/main/resources/detailed-menu.xsl"))))
                        .withCssFiles(list("net/splitcells/website/css/theme.white.variables.css"
                                , "net/splitcells/website/css/basic.themed.css"
                                , "net/splitcells/website/css/basic.css"
                                , "net/splitcells/website/css/den.css"
                                , "net/splitcells/website/css/layout.default.css"
                                , "net/splitcells/martins/avots/website/css/theme.css"));
                projectsRenderer(publicProjectRepository, "public"
                        , projectRenderer(
                                "public", privateProjectRepository.resolve("net.splitcells.martins.avots.website/")
                                , xslLib
                                , privateProjectRepository
                                        .resolve("net.splitcells.martins.avots.website/src/main/resources/html")
                                , "/"
                                , validator
                                , config)
                        , list(projectRenderer("public"
                                        , privateProjectRepository.resolve("net.splitcells.martins.avots.website/")
                                        , xslLib
                                        , privateProjectRepository
                                                .resolve("net.splitcells.martins.avots.website/src/main/resources/html")
                                        , "/"
                                        , validator
                                        , config)
                                , projectRenderer("public"
                                        , privateProjectRepository.resolve(
                                                "/home/splitcells/Documents/projects/net.splitcells.martins.avots.support.system/private/net.splitcells.martins.avots.website/")
                                        , xslLib
                                        , privateProjectRepository
                                                .resolve("net.splitcells.martins.avots.website/src/main/resources/html")
                                        , "/"
                                        , validator
                                        , config)
                                , Dem.configValue(ObjectsRenderer.class)
                                , Dem.configValue(ObjectsMediaRenderer.class))
                        , validator
                        , config).serveToHttpAt();
            });
        }, standardDeveloperConfigurator().andThen(configurator));
    }

    public static Consumer<Environment> standardDeveloperConfigurator() {
        return GelEnv.standardDeveloperConfigurator().andThen(env -> {
            env.config()
                    .withConfigValue(MessageFilter.class
                            , a -> a.path().equals(list("debugging")))
                    .withConfigValue(IsEchoToFile.class, true)
                    .withConfigValue(IsDeterministic.class, Optional.of(Bools.truthful()))
                    .withConfigValue(DeterministicRootSourceSeed.class, 1000L);
            configureForWebserver(env);
        });
    }

    public static void configureForWebserver(Environment env) {
        env.config().configValue(Databases.class)
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
                    public Optional<RenderingResult> render(ProjectRenderer projectRenderer, Config config) {
                        return Optional.of(renderingResult(solution.toCSV().getBytes(), ContentType.CSV.codeName()));
                    }

                    @Override
                    public List<String> path() {
                        final var path = solution.path().shallowCopy();
                        return path.withAppended(path.removeAt(path.size() - 1) + ".csv");
                    }
                }));
    }
}
