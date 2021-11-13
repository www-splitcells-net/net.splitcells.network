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
package net.splitcells.website.server.renderer.extension.commonmark;

import net.splitcells.dem.data.set.Set;
import net.splitcells.dem.lang.perspective.Perspective;
import net.splitcells.website.server.renderer.LayoutUtils;
import net.splitcells.website.server.renderer.ProjectRenderer;
import net.splitcells.website.server.renderer.extension.ProjectRendererExtension;
import net.splitcells.website.server.renderer.RenderingResult;

import java.nio.file.Path;
import java.util.Optional;

import static io.vertx.core.http.HttpHeaders.TEXT_HTML;
import static net.splitcells.dem.data.set.Sets.setOfUniques;
import static net.splitcells.dem.resource.Paths.readString;
import static net.splitcells.dem.resource.Files.is_file;
import static net.splitcells.website.server.renderer.RenderingResult.renderingResult;
import static net.splitcells.website.server.renderer.extension.commonmark.CommonMarkRenderer.commonMarkRenderer;

/**
 * TODO Add support for header outline.
 */
public class CommonMarkReadmeExtension implements ProjectRendererExtension {

    public static CommonMarkReadmeExtension commonMarkReadmeExtension() {
        return new CommonMarkReadmeExtension();
    }

    private final CommonMarkRenderer renderer = commonMarkRenderer();

    private CommonMarkReadmeExtension() {
    }

    @Override
    public Optional<RenderingResult> renderFile(String path, ProjectRenderer projectRenderer) {
        if (path.endsWith("README.html") && is_file(projectRenderer.projectFolder().resolve("README.md"))) {
            final var pathContent = readString(projectRenderer.projectFolder().resolve("README.md"));
            return Optional.of(
                    renderingResult(renderer.render(pathContent, projectRenderer, path)
                            , TEXT_HTML.toString()));
        }
        return Optional.empty();
    }

    @Override
    public Perspective extendProjectLayout(Perspective layout, ProjectRenderer projectRenderer) {
        if (is_file(projectRenderer.projectFolder().resolve("README.md"))) {
            LayoutUtils.extendPerspectiveWithPath(layout
                    , Path.of(projectRenderer.resourceRootPath().substring(1)).resolve("README.html"));
        }
        return layout;
    }

    @Override
    public Set<Path> projectPaths(Path projectRoot) {
        final Set<Path> projectPaths = setOfUniques();
        if (is_file(projectRoot.resolve("README.md"))) {
            projectPaths.add(projectRoot.resolve("README.html"));
        }
        return projectPaths;
    }
}
