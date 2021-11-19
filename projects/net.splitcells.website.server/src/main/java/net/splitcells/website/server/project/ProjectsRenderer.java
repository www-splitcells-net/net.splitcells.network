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
package net.splitcells.website.server.project;

import net.splitcells.dem.data.set.Set;
import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.lang.perspective.Perspective;
import net.splitcells.dem.resource.Files;
import net.splitcells.dem.resource.communication.interaction.LogLevel;
import net.splitcells.website.server.Server;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.data.set.list.Lists.toList;
import static net.splitcells.dem.data.set.map.Maps.map;
import static net.splitcells.dem.lang.namespace.NameSpaces.*;
import static net.splitcells.dem.lang.perspective.PerspectiveI.perspective;
import static net.splitcells.dem.resource.Paths.generateFolderPath;
import static net.splitcells.dem.resource.Paths.path;
import static net.splitcells.dem.resource.Files.createDirectory;
import static net.splitcells.dem.resource.Files.writeToFile;
import static net.splitcells.dem.resource.host.interaction.Domsole.domsole;
import static net.splitcells.dem.utils.ExecutionException.executionException;
import static net.splitcells.website.server.project.LayoutUtils.extendPerspectiveWithPath;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * TODO Create extension system meta project rendering.
 */
public class ProjectsRenderer {

    private static final String LAYOUT_PATH = "/net/splitcells/website/layout/build";

    public static ProjectsRenderer projectsRenderer(String name, ProjectRenderer fallbackRenderer, List<ProjectRenderer> renderers) {
        return new ProjectsRenderer(name, fallbackRenderer, renderers);
    }

    public void build() {
        final var generatedFiles = Paths.get("target", "generated");
        Files.createDirectory(generatedFiles);
        writeToFile(generatedFiles.resolve("layout." + profile + ".xml"), createLayout().toDom());
        generateFolderPath(Paths.get("target", "generated"));
    }

    private Perspective createLayout() {
        final var layout = perspective(VAL, NATURAL);
        this.projectsPaths().forEach(p -> extendPerspectiveWithPath(layout, p));
        return layout;
    }

    public void serveTo(Path target) {
        build();
        projectsPaths().stream()
                .map(path -> "/" + path.toString())
                .map(path -> {
                    if (path.endsWith(".xml") || path.endsWith(".md")) {
                        return net.splitcells.dem.resource.Paths.removeFileSuffix(path) + ".html";
                    }
                    return path;
                })
                .forEach(path -> {
                    try {
                        final var targetPath = path(target, path.substring(1));
                        createDirectory(targetPath.getParent());
                        if (targetPath.toString().contains("README")) {
                            throw executionException("");
                        }
                        writeToFile(targetPath, render(path).get().getContent());
                    } catch (Exception e) {
                        throw new RuntimeException(target.toString() + path, e);
                    }
                });
    }

    public void serveToHttpAt(int port) {
        build();
        new Server().serveToHttpAt(port, requestPath -> render(requestPath));
    }

    public void serveAsAuthenticatedHttpsAt(int port) {
        build();
        new Server().serveAsAuthenticatedHttpsAt(port, requestPath -> render(requestPath));
    }

    @Deprecated
    private final String profile;
    private final List<ProjectRenderer> renderers;
    private final ProjectRenderer fallbackRenderer;

    private ProjectsRenderer(String name, ProjectRenderer fallbackRenderer, List<ProjectRenderer> renderers) {
        this.profile = name;
        this.fallbackRenderer = fallbackRenderer;
        this.renderers = renderers;
    }

    public Optional<RenderingResult> render(String path) {
        try {
            if (path.equals(LAYOUT_PATH)) {
                domsole().append(perspective("Refreshing layout."), LogLevel.INFO);
                this.build();
                return Optional.empty();
            }
            final var matchingRoots = renderers
                    .stream()
                    .filter(root -> path.startsWith(root.resourceRootPath()))
                    .collect(toList());
            if (matchingRoots.isEmpty()) {
                // System.out.println("No match for: " + path);
                //System.out.println("Patterns: " + renderers.keySet());
                return fallbackRenderer.render(path);
            }
            // System.out.println("Match for: " + path);
            // System.out.println("Match on: " + matchingRoots.get(0));
            // Sort descending.
            matchingRoots.sort((a, b) -> Integer.valueOf(a.resourceRootPath().length()).compareTo(b.resourceRootPath().length()));
            matchingRoots.reverse();
            final var renderingResult = matchingRoots.stream()
                    .map(renderer -> renderer.render(path))
                    .filter(Optional::isPresent)
                    .findFirst();
            if (renderingResult.isEmpty()) {
                domsole().append(perspective("Path could not be found: " + path), LogLevel.ERROR);
                return Optional.empty();
            }
            return renderingResult.get();
        } catch (Exception e) {
            throw new RuntimeException(path, e);
        }
    }

    public Set<Path> projectsPaths() {
        return renderers.stream()
                .map(renderer -> renderer.projectPaths())
                .reduce((a, b) -> a.with(b)).get();
    }

    private List<Perspective> s(Perspective current, String element) {
        final var children = current.children().stream()
                .filter(child -> child.nameIs(VAL, NATURAL))
                .filter(child -> child.propertyInstances(NAME, NATURAL).stream()
                        .anyMatch(property -> property.value().get().name().equals(element)))
                .collect(toList());
        assertThat(children).hasSizeLessThan(2);
        return children;
    }
}
