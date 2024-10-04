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
package net.splitcells.website.server.project.renderer;

import net.splitcells.dem.data.set.Set;
import net.splitcells.dem.data.set.Sets;
import net.splitcells.dem.data.set.map.Map;
import net.splitcells.dem.resource.ContentType;
import net.splitcells.dem.resource.FileSystem;
import net.splitcells.dem.resource.communication.log.LogLevel;
import net.splitcells.website.server.Config;
import net.splitcells.website.server.project.LayoutConfig;
import net.splitcells.website.server.project.ProjectRenderer;
import net.splitcells.website.server.processor.BinaryMessage;
import net.splitcells.website.server.projects.ProjectsRenderer;

import java.nio.file.Path;
import java.util.Optional;

import static net.splitcells.dem.data.set.map.Maps.map;
import static net.splitcells.dem.lang.tree.TreeI.tree;
import static net.splitcells.dem.resource.FileSystemVoid.fileSystemVoid;
import static net.splitcells.dem.resource.communication.log.Logs.logs;
import static net.splitcells.dem.utils.ExecutionException.executionException;
import static net.splitcells.website.server.processor.BinaryMessage.binaryMessage;

public class ObjectsRendererI implements ProjectRenderer {
    public static ObjectsRendererI objectsRenderer(Path basePath) {
        return new ObjectsRendererI(basePath);
    }

    private final String pathPrefix;
    private final Map<Path, DiscoverableRenderer> objects = map();

    private ObjectsRendererI(Path basePath) {
        this.pathPrefix = basePath.toString();
    }

    @Override
    public FileSystem projectFileSystem() {
        return fileSystemVoid();
    }

    public synchronized ObjectsRendererI withObject(DiscoverableRenderer object) {
        final var path = Path.of(pathPrefix + "/" + object.path().stream().reduce((a, b) -> a + "/" + b).orElseThrow());
        Optional<Path> alternativePath = Optional.empty();
        if (objects.containsKey(path)) {
            // This makes it easier to analyse problems, when the same path is present multiple times.
            int i = 0;
            do {
                alternativePath = Optional.of(Path.of(pathPrefix + "/" + object.path().stream().reduce((a, b) -> a + "/" + b)
                        .orElseThrow()
                        + "."
                        + ++i));
            } while (objects.containsKey(alternativePath.orElseThrow()));
            logs().appendWarning(tree("Discoverable path is already registered. Using alternative path for rendering instead.")
                            .withProperty("object", object.toString())
                            .withProperty("path", path.toString())
                            .withProperty("alternative path", alternativePath.orElseThrow().toString())
                    , executionException("Discoverable path is already registered."));
            objects.put(alternativePath.orElseThrow(), object);
        } else {
            objects.put(path, object);
        }
        return this;
    }

    @Override
    public synchronized Optional<byte[]> renderString(String arg) {
        logs().append(getClass().getName() + "#renderString not implemented.", LogLevel.WARNING);
        return Optional.empty();
    }

    @Override
    public synchronized Optional<byte[]> renderHtmlBodyContent(String bodyContent, Optional<String> title
            , Optional<String> path
            , Config config
            , ProjectsRenderer projectsRenderer) {
        logs().append(getClass().getName() + "#renderHtmlBodyContent not implemented.", LogLevel.WARNING);
        return Optional.empty();
    }

    @Override
    public synchronized Optional<byte[]> renderXml(String xml, LayoutConfig layoutConfig, Config config) {
        logs().append(getClass().getName() + "#renderXml not implemented.", LogLevel.WARNING);
        return Optional.empty();
    }

    @Override
    public synchronized Optional<byte[]> renderRawXml(String xml, Config config) {
        logs().append(ObjectsRendererI.class.getName() + "#renderRawXml not implemented.", LogLevel.WARNING);
        return Optional.empty();
    }

    @Override
    public synchronized Set<Path> projectPaths() {
        return objects.keySet().stream().map(p -> {
                    final var ps = p.toString();
                    if (ps.startsWith("/")) {
                        return ps.substring(1);
                    }
                    return ps;
                })
                .map(Path::of)
                .collect(Sets.toSetOfUniques());
    }

    @Override
    public synchronized Set<Path> relevantProjectPaths() {
        return Sets.setOfUniques(objects.keySet());
    }

    @Override
    public synchronized Optional<BinaryMessage> render(String argPath) {
        return Optional.empty();
    }

    @Override
    public synchronized Optional<BinaryMessage> render(String argPath, ProjectsRenderer projectsRenderer) {
        final var path = Path.of(argPath);
        if (objects.containsKey(path)) {
            final var object = objects.get(path);
            final String relativeArgPath;
            if (argPath.startsWith("/")) {
                relativeArgPath = argPath.substring(1);
            } else {
                relativeArgPath = argPath;
            }
            return Optional.of(binaryMessage(projectsRenderer.renderHtmlBodyContent(object.render()
                            , object.title()
                            , Optional.of(relativeArgPath)
                            , projectsRenderer.config()).orElseThrow()
                    , ContentType.HTML_TEXT.toString()));
        }
        return Optional.empty();
    }

    @Override
    public String resourceRootPath() {
        return "/";
    }
}
