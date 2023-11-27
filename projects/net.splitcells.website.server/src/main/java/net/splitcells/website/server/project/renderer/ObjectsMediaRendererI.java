/*
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.website.server.project.renderer;

import net.splitcells.dem.data.set.Set;
import net.splitcells.dem.data.set.Sets;
import net.splitcells.dem.data.set.map.Map;
import net.splitcells.dem.resource.FileSystemView;
import net.splitcells.dem.resource.communication.log.LogLevel;
import net.splitcells.website.server.Config;
import net.splitcells.website.server.project.LayoutConfig;
import net.splitcells.website.server.project.ProjectRenderer;
import net.splitcells.website.server.processor.BinaryMessage;
import net.splitcells.website.server.projects.ProjectsRenderer;

import java.nio.file.Path;
import java.util.Optional;

import static net.splitcells.dem.data.set.map.Maps.map;
import static net.splitcells.dem.resource.FileSystems.fileSystemOnLocalHost;
import static net.splitcells.dem.resource.communication.log.Logs.logs;

public class ObjectsMediaRendererI implements ProjectRenderer {

    public static ObjectsMediaRendererI objectsMediaRenderer(Path basePath) {
        return new ObjectsMediaRendererI(basePath);
    }

    private final String pathPrefix;
    private final Map<Path, DiscoverableMediaRenderer> objects = map();

    private ObjectsMediaRendererI(Path basePath) {
        this.pathPrefix = basePath.toString();
    }

    @Override
    public FileSystemView projectFileSystem() {
        return fileSystemOnLocalHost(Path.of("/invalid/"));
    }

    public ObjectsMediaRendererI withMediaObject(DiscoverableMediaRenderer object) {
        objects.put(Path.of(pathPrefix + "/" + object.path().stream().reduce((a, b) -> a + "/" + b).orElseThrow()), object);
        return this;
    }

    @Override
    public Optional<byte[]> renderString(String arg) {
        logs().append(getClass().getName() + "#renderString not implemented.", LogLevel.WARNING);
        return Optional.empty();
    }

    @Override
    public Optional<byte[]> renderHtmlBodyContent(String bodyContent, Optional<String> title, Optional<String> path
            , Config config
            , ProjectsRenderer projectsRenderer) {
        logs().append(getClass().getName() + "#renderHtmlBodyContent not implemented.", LogLevel.WARNING);
        return Optional.empty();
    }

    @Override
    public Optional<byte[]> renderXml(String xml, LayoutConfig layoutConfig, Config config) {
        logs().append(getClass().getName() + "#renderXml not implemented.", LogLevel.WARNING);
        return Optional.empty();
    }

    @Override
    public Optional<byte[]> renderRawXml(String xml, Config config) {
        logs().append(getClass().getName() + "#renderRawXml not implemented.", LogLevel.WARNING);
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
    public Optional<BinaryMessage> render(String path) {
        return Optional.empty();
    }

    @Override
    public Optional<BinaryMessage> render(String argPath, Config config, ProjectRenderer projectRenderer) {
        final var path = Path.of(argPath);
        if (objects.containsKey(path)) {
            final var objectMedia = objects.get(path);
            return objectMedia.render(projectRenderer, config);
        }
        return Optional.empty();
    }

    @Override
    public String resourceRootPath() {
        return "/";
    }
}
