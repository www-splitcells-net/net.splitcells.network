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
        domsole().append(ObjectsRendererI.class.getName() + "#renderString not implemented.", LogLevel.WARNING);
        return Optional.empty();
    }

    @Override
    public Optional<byte[]> renderHtmlBodyContent(String bodyContent, Optional<String> title, Optional<String> path, Config config) {
        domsole().append(ObjectsRendererI.class.getName() + "#renderHtmlBodyContent not implemented.", LogLevel.WARNING);
        return Optional.empty();
    }

    @Override
    public Optional<byte[]> renderXml(String xml, LayoutConfig layoutConfig, Config config) {
        domsole().append(ObjectsRendererI.class.getName() + "#renderXml not implemented.", LogLevel.WARNING);
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
            return Optional.of(renderingResult(projectRenderer.renderHtmlBodyContent(object.render()
                            , object.title()
                            , Optional.of(argPath)
                            , config).get()
                    , TEXT_HTML.toString()));
        }
        return Optional.empty();
    }

    @Override
    public String resourceRootPath() {
        return "/";
    }
}
