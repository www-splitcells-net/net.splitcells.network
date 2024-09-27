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
package net.splitcells.website.server.project.renderer.extension.commonmark;

import net.splitcells.dem.data.order.Comparators;
import net.splitcells.dem.data.set.Set;
import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.lang.tree.Tree;
import net.splitcells.dem.resource.ContentType;
import net.splitcells.website.server.Config;
import net.splitcells.website.server.project.LayoutUtils;
import net.splitcells.website.server.project.ProjectRenderer;
import net.splitcells.website.server.processor.BinaryMessage;
import net.splitcells.website.server.project.renderer.extension.ProjectRendererExtension;

import java.nio.file.Path;
import java.util.Optional;

import static net.splitcells.dem.data.set.Sets.setOfUniques;
import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.resource.ContentType.HTML_TEXT;
import static net.splitcells.dem.utils.ExecutionException.executionException;
import static net.splitcells.website.server.processor.BinaryMessage.binaryMessage;
import static net.splitcells.website.server.project.renderer.extension.commonmark.CommonMarkIntegration.commonMarkIntegration;

public class CommonMarkChangelogEventProjectRendererExtension implements ProjectRendererExtension {

    public static CommonMarkChangelogEventProjectRendererExtension commonMarkChangelogEventRenderer() {
        return new CommonMarkChangelogEventProjectRendererExtension();
    }

    private static final Path CHANGELOG = Path.of("CHANGELOG.md");

    private final CommonMarkIntegration renderer = commonMarkIntegration();


    private CommonMarkChangelogEventProjectRendererExtension() {
    }

    @Override
    public Optional<BinaryMessage> renderFile(String path, ProjectRenderer projectRenderer, Config config) {
        if (path.endsWith("CHANGELOG.events.html")
                && projectRenderer.projectFileSystem().isFile(CHANGELOG)) {
            final var pathContent = projectRenderer.projectFileSystem().readString(CHANGELOG);
            final var events = renderer.events(pathContent, projectRenderer, path, config);
            try {
                return Optional.of(
                        binaryMessage(renderEvents(events).getBytes(ContentType.UTF_8.codeName())
                                , HTML_TEXT.codeName()));
            } catch (Exception e) {
                throw executionException(e);
            }
        }
        return Optional.empty();
    }

    public List<Event> extractEvent(String path, ProjectRenderer projectRenderer, Config config) {
        if (path.endsWith("CHANGELOG.events.html")
                && projectRenderer.projectFileSystem().isFile(CHANGELOG)) {
            final var pathContent = projectRenderer.projectFileSystem().readString(CHANGELOG);
            return renderer.events(pathContent, projectRenderer, path, config);
        }
        return list();
    }

    public String renderEvents(List<Event> events) {
        events.sort(Comparators.legacyComparator((a, b) -> b.dateTime().compareTo(a.dateTime())));
        final var renderedEvents = events.stream().map(Event::node)
                .map(e -> renderer.render(e))
                .reduce((a, b) -> a + "\n" + b);
        if (renderedEvents.isEmpty()) {
            return "";
        }
        return renderedEvents.get();
    }

    @Override
    public Tree extendProjectLayout(Tree layout, ProjectRenderer projectRenderer) {
        if (projectRenderer.projectFileSystem().isFile(CHANGELOG)) {
            LayoutUtils.extendPerspectiveWithPath(layout
                    , Path.of(projectRenderer.resourceRootPath().substring(1)).resolve("CHANGELOG.events.html"));
        }
        return layout;
    }

    @Override
    public Set<Path> projectPaths(ProjectRenderer projectRenderer) {
        final Set<Path> projectPaths = setOfUniques();
        if (projectRenderer.projectFileSystem().isFile(CHANGELOG)) {
            projectPaths.add(Path.of(projectRenderer.resourceRootPath()
                    .substring(1)).resolve("CHANGELOG.events.html"));
        }
        return projectPaths;
    }

    @Override
    public Set<Path> relevantProjectPaths(ProjectRenderer projectRenderer) {
        return projectPaths(projectRenderer);
    }
}
