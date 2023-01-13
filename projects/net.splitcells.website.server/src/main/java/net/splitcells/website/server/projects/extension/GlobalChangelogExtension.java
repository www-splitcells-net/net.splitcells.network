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
import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.data.set.list.Lists;
import net.splitcells.website.server.Config;
import net.splitcells.website.server.project.RenderingResult;
import net.splitcells.website.server.project.renderer.extension.commonmark.CommonMarkChangelogEventProjectRendererExtension;
import net.splitcells.website.server.projects.ProjectsRendererI;

import java.nio.file.Path;
import java.util.Optional;

import static io.vertx.core.http.HttpHeaders.TEXT_HTML;
import static net.splitcells.dem.data.set.Sets.setOfUniques;
import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.website.server.project.RenderingResult.renderingResult;
import static net.splitcells.website.server.project.renderer.extension.commonmark.CommonMarkChangelogEventProjectRendererExtension.commonMarkChangelogEventRenderer;

public class GlobalChangelogExtension implements ProjectsRendererExtension {
    public static GlobalChangelogExtension globalChangelogExtension() {
        return new GlobalChangelogExtension();
    }

    /**
     * TODO HACK This should depend on {@link Config#rootPath()}.
     */
    private static final String PATH = "/net/splitcells/CHANGELOG.global.html";

    private GlobalChangelogExtension() {

    }

    private final CommonMarkChangelogEventProjectRendererExtension eventUtils = commonMarkChangelogEventRenderer();

    @Override
    public Optional<RenderingResult> renderFile(String path, ProjectsRendererI projectsRendererI, Config config) {
        if (PATH.equals(path)) {
            final var events = projectsRendererI.projectRenderers().stream()
                    .map(pr -> eventUtils.extractEvent(pr.resourceRootPath2().resolve("CHANGELOG.events.html").toString(), pr, config))
                    .reduce(List::withAppended)
                    .orElseGet(Lists::list);
            return Optional.of(
                    renderingResult(projectsRendererI.renderHtmlBodyContent("<ol>" + eventUtils.renderEvents(events) + "</ol>"
                                    , Optional.of("Global Changelog")
                                    , Optional.of(path)
                                    , config).orElseThrow()
                            , TEXT_HTML.toString()));
        }
        return Optional.empty();
    }

    @Override
    public Set<Path> projectPaths(ProjectsRendererI projectsRendererI) {
        // Avoid first slash.
        return setOfUniques(Path.of(PATH.substring(1)));
    }
}
