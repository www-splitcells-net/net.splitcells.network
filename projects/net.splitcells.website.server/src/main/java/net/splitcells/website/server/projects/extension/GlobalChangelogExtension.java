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
package net.splitcells.website.server.projects.extension;

import net.splitcells.dem.data.set.Set;
import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.data.set.list.Lists;
import net.splitcells.website.server.Config;
import net.splitcells.website.server.processor.BinaryMessage;
import net.splitcells.website.server.project.renderer.extension.commonmark.CommonMarkChangelogEventProjectRendererExtension;
import net.splitcells.website.server.projects.ProjectsRendererI;
import net.splitcells.website.server.projects.RenderRequest;

import java.nio.file.Path;
import java.util.Optional;

import static io.vertx.core.http.HttpHeaders.TEXT_HTML;
import static net.splitcells.dem.data.set.Sets.setOfUniques;
import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.website.server.processor.BinaryMessage.binaryMessage;
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
    public Optional<BinaryMessage> renderFile(String path, ProjectsRendererI projectsRendererI, Config config) {
        if (PATH.equals(path)) {
            final var events = projectsRendererI.projectRenderers().stream()
                    .map(pr -> eventUtils.extractEvent(pr.resourceRootPath2().resolve("CHANGELOG.events.html").toString(), pr, config))
                    .reduce(List::withAppended)
                    .orElseGet(Lists::list);
            return Optional.of(
                    binaryMessage(projectsRendererI.renderHtmlBodyContent("<ol>" + eventUtils.renderEvents(events) + "</ol>"
                                    , Optional.of("Global Changelog")
                                    , Optional.of(path)
                                    , config).orElseThrow()
                            , TEXT_HTML.toString()));
        }
        return Optional.empty();
    }

    @Override
    public boolean requiresAuthentication(RenderRequest request) {
        return false;
    }

    @Override
    public Set<Path> projectPaths(ProjectsRendererI projectsRendererI) {
        // Avoid first slash.
        return setOfUniques(Path.of(PATH.substring(1)));
    }
}
