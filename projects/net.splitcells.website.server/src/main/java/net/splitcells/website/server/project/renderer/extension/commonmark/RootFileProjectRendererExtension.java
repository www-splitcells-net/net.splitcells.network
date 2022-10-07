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

import net.splitcells.dem.data.set.Set;
import net.splitcells.dem.lang.perspective.Perspective;
import net.splitcells.dem.resource.Files;
import net.splitcells.website.server.Config;
import net.splitcells.website.server.project.LayoutUtils;
import net.splitcells.website.server.project.ProjectRenderer;
import net.splitcells.website.server.project.RenderingResult;
import net.splitcells.website.server.project.renderer.extension.ProjectRendererExtension;

import java.nio.file.Path;
import java.util.Optional;

import static net.splitcells.dem.data.set.Sets.setOfUniques;
import static net.splitcells.dem.resource.ContentType.HTML_TEXT;
import static net.splitcells.dem.resource.Files.is_file;
import static net.splitcells.dem.resource.Paths.readString;
import static net.splitcells.website.server.project.RenderingResult.renderingResult;
import static net.splitcells.website.server.project.renderer.extension.commonmark.CommonMarkIntegration.commonMarkIntegration;

/**
 * TODO Add support for header outline.
 */
public class RootFileProjectRendererExtension implements ProjectRendererExtension {
    public static RootFileProjectRendererExtension rootFileProjectRendererExtension(String rootFile) {
        return new RootFileProjectRendererExtension(rootFile);
    }

    private final CommonMarkIntegration renderer = commonMarkIntegration();
    private final String rootFile;

    private RootFileProjectRendererExtension(String rootFile) {
        this.rootFile = rootFile;
    }

    @Override
    public Optional<RenderingResult> renderFile(String path, ProjectRenderer projectRenderer, Config config) {
        final var readmePath = projectRenderer.projectFolder().resolve(rootFile + ".md");
        if (path.endsWith(rootFile + ".html")) {
            if (Files.is_file(readmePath)) {
                final var pathContent = readString(readmePath);
                return Optional.of(
                        renderingResult(renderer.render(pathContent, projectRenderer, path, config)
                                , HTML_TEXT.codeName()));
            }
        }
        return Optional.empty();
    }

    @Override
    public Perspective extendProjectLayout(Perspective layout, ProjectRenderer projectRenderer) {
        if (is_file(projectRenderer.projectFolder().resolve(rootFile + ".md"))) {
            LayoutUtils.extendPerspectiveWithPath(layout
                    , Path.of(projectRenderer.resourceRootPath().substring(1)).resolve(rootFile + ".html"));
        }
        return layout;
    }

    @Override
    public Set<Path> projectPaths(ProjectRenderer projectRenderer) {
        final Set<Path> projectPaths = setOfUniques();
        if (is_file(projectRenderer.projectFolder().resolve(rootFile + ".md"))) {
            if (projectRenderer.resourceRootPath().length() == 0) {
                projectPaths.add(Path.of(rootFile + ".html"));
            } else {
                projectPaths.add(Path.of(projectRenderer.resourceRootPath().substring(1)).resolve(rootFile + ".html"));
            }
        }
        return projectPaths;
    }

    @Override
    public Set<Path> relevantProjectPaths(ProjectRenderer projectRenderer) {
        return projectPaths(projectRenderer);
    }
}
