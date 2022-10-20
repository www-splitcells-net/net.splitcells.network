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
package net.splitcells.website.server.projects.extension;

import net.splitcells.dem.data.set.Set;
import net.splitcells.dem.lang.namespace.NameSpaces;
import net.splitcells.website.server.Config;
import net.splitcells.website.server.project.LayoutConfig;
import net.splitcells.website.server.project.RenderingResult;
import net.splitcells.website.server.projects.ProjectsRendererI;

import java.nio.file.Path;
import java.util.Optional;

import static net.splitcells.dem.data.set.Sets.setOfUniques;
import static net.splitcells.dem.utils.NotImplementedYet.notImplementedYet;

public class LayoutExtension implements ProjectsRendererExtension {
    public static LayoutExtension layoutExtension() {
        return new LayoutExtension();
    }

    private static final String PATH = "/net/splitcells/website/layout.html";

    private LayoutExtension() {

    }

    @Override
    public Optional<RenderingResult> renderFile(String path, ProjectsRendererI projectsRendererI, Config config) {
        if (PATH.equals(path) && config.layout().isPresent()) {
            return projectsRendererI.renderContent
                    ("<ol xmlns=\"http://www.w3.org/1999/xhtml\">"
                                    + projectsRendererI.projectsPaths().stream()
                                    .map(p -> {
                                        // TODO This is an hack. All layout paths should already be relative.
                                        final var sp = p.toString();
                                        if (sp.startsWith("/")) {
                                            return sp;
                                        }
                                        return "/" + sp;
                                    })
                                    .sorted()
                                    .map(p -> "<li><a href=\"" + p + "\">" + p + "</a></li>")
                                    .reduce((a, b) -> a + b)
                                    .orElse("")
                                    + "</ol>"
                            , LayoutConfig.layoutConfig(PATH));
        }
        return Optional.empty();
    }

    @Override
    public Set<Path> projectPaths(ProjectsRendererI projectsRendererI) {
        if (projectsRendererI.config().layout().isPresent()) {
            return setOfUniques(Path.of(PATH.substring(1)));
        }
        return setOfUniques();
    }
}
