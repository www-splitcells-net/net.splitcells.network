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
import net.splitcells.dem.environment.config.StaticFlags;
import net.splitcells.dem.lang.annotations.ReturnsThis;
import net.splitcells.website.server.Config;
import net.splitcells.website.server.project.RenderingResult;
import net.splitcells.website.server.projects.ProjectsRendererI;

import java.nio.file.Path;
import java.util.Optional;

import static net.splitcells.dem.data.set.Sets.setOfUniques;
import static net.splitcells.dem.data.set.list.Lists.list;

/**
 * <p>
 * Provides one interface to multiple {@link ProjectsRendererExtension}.
 * When a query is requested a matching {@link ProjectsRendererExtension} is queried.
 * </p>
 * <p>
 * Only one {@link ProjectsRendererExtension} is allowed to matched at a time.
 * </p>
 */
public class ProjectsRendererExtensionMerger implements ProjectsRendererExtension {
    public static ProjectsRendererExtensionMerger projectsRendererExtensionMerger() {
        return new ProjectsRendererExtensionMerger();
    }

    private final List<ProjectsRendererExtension> projectsRendererExtensions = list();

    private ProjectsRendererExtensionMerger() {
    }

    @Override
    public Optional<RenderingResult> renderFile(String path, ProjectsRendererI projectsRendererI, Config config) {
        final var rendering = projectsRendererExtensions.stream()
                .map(e -> e.renderFile(path, projectsRendererI, config))
                .filter(e -> e.isPresent())
                .collect(Lists.toList());
        if (rendering.size() > 1) {
            final var matchedExtensions = projectsRendererExtensions.stream()
                    .filter(r -> r.renderFile(path, projectsRendererI, config).isPresent())
                    .map(Object::toString)
                    .reduce((a, b) -> a + ", " + b)
                    .orElseThrow();
            throw new RuntimeException("Multiple matches are present: " + matchedExtensions);
        }
        if (rendering.isEmpty()) {
            return Optional.empty();
        } else {
            return rendering.get(0);
        }
    }

    @Override
    public Set<Path> projectPaths(ProjectsRendererI projectsRendererI) {
        final Set<Path> projectPaths = setOfUniques();
        projectsRendererExtensions.forEach(e -> {
            final var path = e.projectPaths(projectsRendererI);
            if (StaticFlags.ENFORCING_UNIT_CONSISTENCY) {
                if (path.toString().startsWith("/")) {
                    throw new IllegalStateException("Absolute project paths are not allowed: " + path);
                }
            }
            projectPaths.addAll(path);
        });
        return projectPaths;
    }

    @ReturnsThis
    public ProjectsRendererExtensionMerger withRegisteredExtension(ProjectsRendererExtension extension) {
        projectsRendererExtensions.add(extension);
        return this;
    }
}