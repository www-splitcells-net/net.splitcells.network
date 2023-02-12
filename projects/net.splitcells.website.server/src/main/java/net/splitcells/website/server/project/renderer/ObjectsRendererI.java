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
import net.splitcells.dem.resource.communication.interaction.LogLevel;
import net.splitcells.website.server.Config;
import net.splitcells.website.server.project.LayoutConfig;
import net.splitcells.website.server.project.ProjectRenderer;
import net.splitcells.website.server.project.RenderingResult;

import java.nio.file.Path;
import java.util.Optional;

import static io.vertx.core.http.HttpHeaders.TEXT_HTML;
import static net.splitcells.dem.data.set.map.Maps.map;
import static net.splitcells.dem.resource.communication.log.Domsole.domsole;
import static net.splitcells.website.server.project.RenderingResult.renderingResult;

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
    public Path projectFolder() {
        return Path.of("/invalid/");
    }

    public ObjectsRendererI withObject(DiscoverableRenderer object) {
        objects.put(Path.of(pathPrefix + "/" + object.path().stream().reduce((a, b) -> a + "/" + b).orElseThrow()), object);
        return this;
    }

    @Override
    public Optional<byte[]> renderString(String arg) {
        domsole().append(getClass().getName() + "#renderString not implemented.", LogLevel.WARNING);
        return Optional.empty();
    }

    @Override
    public Optional<byte[]> renderHtmlBodyContent(String bodyContent, Optional<String> title, Optional<String> path, Config config) {
        domsole().append(getClass().getName() + "#renderHtmlBodyContent not implemented.", LogLevel.WARNING);
        return Optional.empty();
    }

    @Override
    public Optional<byte[]> renderXml(String xml, LayoutConfig layoutConfig, Config config) {
        domsole().append(getClass().getName() + "#renderXml not implemented.", LogLevel.WARNING);
        return Optional.empty();
    }

    @Override
    public Optional<byte[]> renderRawXml(String xml, Config config) {
        domsole().append(ObjectsRendererI.class.getName() + "#renderRawXml not implemented.", LogLevel.WARNING);
        return Optional.empty();
    }

    @Override
    public Set<Path> projectPaths() {
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
    public Set<Path> relevantProjectPaths() {
        return Sets.setOfUniques(objects.keySet());
    }

    @Override
    public Optional<RenderingResult> render(String argPath) {
        return Optional.empty();
    }

    @Override
    public Optional<RenderingResult> render(String argPath, Config config, ProjectRenderer projectRenderer) {
        final var path = Path.of(argPath);
        if (objects.containsKey(path)) {
            final var object = objects.get(path);
            final String relativeArgPath;
            if (argPath.startsWith("/")) {
                relativeArgPath = argPath.substring(1);
            } else {
                relativeArgPath = argPath;
            }
            return Optional.of(renderingResult(projectRenderer.renderHtmlBodyContent(object.render()
                            , object.title()
                            , Optional.of(relativeArgPath)
                            , config).orElseThrow()
                    , TEXT_HTML.toString()));
        }
        return Optional.empty();
    }

    @Override
    public String resourceRootPath() {
        return "/";
    }
}
