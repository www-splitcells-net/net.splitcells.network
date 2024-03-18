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

import net.splitcells.dem.data.set.Set;
import net.splitcells.dem.lang.perspective.Perspective;
import net.splitcells.dem.resource.communication.log.Logs;
import net.splitcells.website.server.project.LayoutUtils;
import net.splitcells.website.server.project.ProjectRenderer;
import net.splitcells.website.server.processor.BinaryMessage;
import net.splitcells.website.server.project.renderer.extension.ProjectRendererExtension;
import net.splitcells.website.server.projects.ProjectsRenderer;

import java.nio.file.Path;
import java.util.Optional;

import static net.splitcells.dem.data.set.Sets.setOfUniques;
import static net.splitcells.dem.lang.perspective.PerspectiveI.perspective;
import static net.splitcells.dem.resource.ContentType.HTML_TEXT;
import static net.splitcells.dem.resource.communication.log.Logs.logs;
import static net.splitcells.website.server.processor.BinaryMessage.binaryMessage;
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
    public Optional<BinaryMessage> renderFile(String path, ProjectsRenderer projectsRenderer, ProjectRenderer projectRenderer) {
        final var readmePath = Path.of(rootFile + ".md");
        if (path.endsWith(rootFile + ".html")) {
            if (!path.startsWith(projectRenderer.resourceRootPath2().toString().replace('\\', '/').substring(1))) {
                logs().appendWarning(perspective("Matching project's root path is not a prefix of the requested path.")
                        .withProperty("requested path", path)
                        .withProperty("matching project's root path", projectRenderer.resourceRootPath2().toString()));
            }
            if (projectRenderer.projectFileSystem().isFile(readmePath)) {
                final var pathContent = projectRenderer.projectFileSystem().readString(readmePath);
                return Optional.of(
                        binaryMessage(renderer.render(pathContent, projectRenderer, path, projectsRenderer.config()
                                        , projectsRenderer)
                                , HTML_TEXT.codeName()));
            }
        }
        return Optional.empty();
    }

    @Override
    public Perspective extendProjectLayout(Perspective layout, ProjectRenderer projectRenderer) {
        if (projectRenderer.projectFileSystem().isFile(Path.of(rootFile + ".md"))) {
            LayoutUtils.extendPerspectiveWithPath(layout
                    , Path.of(projectRenderer.resourceRootPath().substring(1)).resolve(rootFile + ".html"));
        }
        return layout;
    }

    @Override
    public Set<Path> projectPaths(ProjectRenderer projectRenderer) {
        final Set<Path> projectPaths = setOfUniques();
        if (projectRenderer.projectFileSystem().isFile(Path.of(rootFile + ".md"))) {
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
