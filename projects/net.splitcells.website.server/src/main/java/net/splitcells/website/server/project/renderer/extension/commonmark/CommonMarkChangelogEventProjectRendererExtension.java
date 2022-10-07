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
package net.splitcells.website.server.project.renderer.extension.commonmark;

import net.splitcells.dem.data.order.Comparators;
import net.splitcells.dem.data.set.Set;
import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.lang.perspective.Perspective;
import net.splitcells.dem.resource.ContentType;
import net.splitcells.website.server.Config;
import net.splitcells.website.server.project.LayoutUtils;
import net.splitcells.website.server.project.ProjectRenderer;
import net.splitcells.website.server.project.RenderingResult;
import net.splitcells.website.server.project.renderer.extension.ProjectRendererExtension;

import java.nio.file.Path;
import java.util.Optional;

import static net.splitcells.dem.data.set.Sets.setOfUniques;
import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.resource.ContentType.HTML_TEXT;
import static net.splitcells.dem.resource.Files.is_file;
import static net.splitcells.dem.resource.Paths.readString;
import static net.splitcells.dem.utils.ExecutionException.executionException;
import static net.splitcells.website.server.project.RenderingResult.renderingResult;
import static net.splitcells.website.server.project.renderer.extension.commonmark.CommonMarkIntegration.commonMarkIntegration;

public class CommonMarkChangelogEventProjectRendererExtension implements ProjectRendererExtension {

    public static CommonMarkChangelogEventProjectRendererExtension commonMarkChangelogEventRenderer() {
        return new CommonMarkChangelogEventProjectRendererExtension();
    }

    private final CommonMarkIntegration renderer = commonMarkIntegration();

    private CommonMarkChangelogEventProjectRendererExtension() {
    }

    @Override
    public Optional<RenderingResult> renderFile(String path, ProjectRenderer projectRenderer, Config config) {
        if (path.endsWith("CHANGELOG.events.html") && is_file(projectRenderer.projectFolder().resolve("CHANGELOG.md"))) {
            final var pathContent = readString(projectRenderer.projectFolder().resolve("CHANGELOG.md"));
            final var events = renderer.events(pathContent, projectRenderer, path, config);
            try {
                return Optional.of(
                        renderingResult(renderEvents(events).getBytes(ContentType.UTF_8.codeName())
                                , HTML_TEXT.codeName()));
            } catch (Exception e) {
                throw executionException(e);
            }
        }
        return Optional.empty();
    }

    public List<Event> extractEvent(String path, ProjectRenderer projectRenderer, Config config) {
        if (path.endsWith("CHANGELOG.events.html") && is_file(projectRenderer.projectFolder().resolve("CHANGELOG.md"))) {
            final var pathContent = readString(projectRenderer.projectFolder().resolve("CHANGELOG.md"));
            return renderer.events(pathContent, projectRenderer, path, config);
        }
        return list();
    }

    public String renderEvents(List<Event> events) {
        events.sort(Comparators.comparator((a, b) -> b.dateTime().compareTo(a.dateTime())));
        final var renderedEvents = events.stream().map(Event::node)
                .map(e -> renderer.render(e))
                .reduce((a, b) -> a + "\n" + b);
        if (renderedEvents.isEmpty()) {
            return "";
        }
        return renderedEvents.get();
    }

    @Override
    public Perspective extendProjectLayout(Perspective layout, ProjectRenderer projectRenderer) {
        if (is_file(projectRenderer.projectFolder().resolve("CHANGELOG.md"))) {
            LayoutUtils.extendPerspectiveWithPath(layout
                    , Path.of(projectRenderer.resourceRootPath().substring(1)).resolve("CHANGELOG.events.html"));
        }
        return layout;
    }

    @Override
    public Set<Path> projectPaths(ProjectRenderer projectRenderer) {
        final Set<Path> projectPaths = setOfUniques();
        if (is_file(projectRenderer.projectFolder().resolve("CHANGELOG.md"))) {
            projectPaths.add(Path.of(projectRenderer.resourceRootPath().substring(1)).resolve("CHANGELOG.events.html"));
        }
        return projectPaths;
    }

    @Override
    public Set<Path> relevantProjectPaths(ProjectRenderer projectRenderer) {
        return projectPaths(projectRenderer);
    }
}
