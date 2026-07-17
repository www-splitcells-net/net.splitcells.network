/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.website.server.projects.extension.impls;

import net.splitcells.dem.data.set.Set;
import net.splitcells.website.server.Config;
import net.splitcells.website.server.project.LayoutConfig;
import net.splitcells.website.server.processor.BinaryMessage;
import net.splitcells.website.server.projects.ProjectsRendererI;
import net.splitcells.website.server.projects.RenderRequest;
import net.splitcells.website.server.projects.extension.ProjectsRendererExtension;

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
    public Optional<BinaryMessage> renderFile(String path, ProjectsRendererI projectsRendererI, Config config) {
        if (PATH.equals(path) && config.layout().isPresent()) {
            return projectsRendererI.renderContent
                    ("<ol xmlns=\"http://www.w3.org/1999/xhtml\">"
                                    + projectsRendererI.projectsPaths().stream()
                                    .map(p -> {
                                        // TODO This is an hack. All layout paths should already be relative.
                                        final var ps = p.toString();
                                        if (ps.startsWith("/")) {
                                            return ps;
                                        }
                                        return "/" + ps;
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
    public boolean requiresAuthentication(RenderRequest request) {
        return false;
    }

    @Override
    public Set<Path> projectPaths(ProjectsRendererI projectsRendererI) {
        if (projectsRendererI.config().layout().isPresent()) {
            return setOfUniques(Path.of(PATH.substring(1)));
        }
        return setOfUniques();
    }
}
