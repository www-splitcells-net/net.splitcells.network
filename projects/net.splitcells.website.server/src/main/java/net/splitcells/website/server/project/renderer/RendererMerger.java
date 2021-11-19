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
package net.splitcells.website.server.project.renderer;

import net.splitcells.dem.data.set.Set;
import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.data.set.list.Lists;
import net.splitcells.dem.lang.perspective.Perspective;
import net.splitcells.website.server.project.ProjectRenderer;
import net.splitcells.website.server.project.RenderingResult;

import java.nio.file.Path;
import java.util.Optional;

import static net.splitcells.dem.data.set.Sets.setOfUniques;
import static net.splitcells.dem.data.set.list.Lists.list;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * <p>
 * Provides one interface to multiple {@link Renderer}.
 * When a query is requested a matching {@link Renderer} is queried.
 * </p>
 * <p>
 * Only one {@link Renderer} is allowed to matched at a time.
 * </p>
 */
public class RendererMerger implements Renderer {
    public static RendererMerger rendererMerger() {
        return new RendererMerger();
    }

    private final List<Renderer> renderers = list();

    private RendererMerger() {

    }

    @Override
    public Optional<RenderingResult> renderFile(String path, ProjectRenderer projectRenderer) {
        final var rendering = renderers.stream()
                .map(e -> e.renderFile(path, projectRenderer))
                .filter(e -> e.isPresent())
                .collect(Lists.toList());
        if (rendering.size() > 1) {
            final var matchedExtensions = renderers.stream()
                    .filter(r -> r.renderFile(path, projectRenderer).isPresent())
                    .map(Object::toString)
                    .reduce((a, b) -> a + ", " + b)
                    .get();
            throw new RuntimeException("Multiple matches are present: "
                    + projectRenderer.resourceRootPath2().toString()
                    + ": "
                    + matchedExtensions);
        }
        if (rendering.isEmpty()) {
            return Optional.empty();
        } else {
            return rendering.get(0);
        }
    }

    @Override
    public Perspective extendProjectLayout(Perspective layout, ProjectRenderer projectRenderer) {
        renderers.forEach(e -> e.extendProjectLayout(layout, projectRenderer));
        return layout;
    }

    @Override
    public Set<Path> projectPaths(ProjectRenderer projectRenderer) {
        final Set<Path> projectPaths = setOfUniques();
        renderers.forEach(e -> {
            projectPaths.addAll(e.projectPaths(projectRenderer));
        });
        return projectPaths;
    }

    public void registerExtension(Renderer extension) {
        renderers.add(extension);
    }
}