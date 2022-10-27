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
package net.splitcells.cin;

import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.environment.Environment;
import net.splitcells.dem.resource.ContentType;
import net.splitcells.gel.data.database.Databases;
import net.splitcells.gel.solution.Solutions;
import net.splitcells.website.server.Config;
import net.splitcells.website.server.project.ProjectRenderer;
import net.splitcells.website.server.project.RenderingResult;
import net.splitcells.website.server.project.renderer.DiscoverableMediaRenderer;
import net.splitcells.website.server.project.renderer.DiscoverableRenderer;
import net.splitcells.website.server.project.renderer.ObjectsMediaRenderer;
import net.splitcells.website.server.project.renderer.ObjectsRenderer;

import java.util.Optional;

import static net.splitcells.dem.utils.ConstructorIllegal.constructorIllegal;
import static net.splitcells.website.server.project.RenderingResult.renderingResult;

public class Cin {
    private Cin() {
        throw constructorIllegal();
    }

    public static void configure(Environment env) {
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
