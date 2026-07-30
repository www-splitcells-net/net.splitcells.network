/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.website.server.project.renderer;

import lombok.val;
import net.splitcells.dem.data.set.Set;
import net.splitcells.dem.data.set.Sets;
import net.splitcells.dem.data.set.list.ListView;
import net.splitcells.dem.data.set.map.Map;
import net.splitcells.dem.object.Discoverable;
import net.splitcells.dem.resource.ContentType;
import net.splitcells.dem.resource.FileSystem;
import net.splitcells.dem.resource.communication.log.LogLevel;
import net.splitcells.dem.utils.ExecutionException;
import net.splitcells.website.server.Config;
import net.splitcells.website.server.processor.BinaryMessage;
import net.splitcells.website.server.project.LayoutConfig;
import net.splitcells.website.server.project.ProjectRenderer;
import net.splitcells.website.server.projects.ProjectsRenderer;

import java.nio.file.Path;
import java.util.Optional;

import static net.splitcells.dem.data.set.Sets.setOfUniques;
import static net.splitcells.dem.data.set.list.Lists.toList;
import static net.splitcells.dem.data.set.map.Maps.map;
import static net.splitcells.dem.lang.tree.TreeI.tree;
import static net.splitcells.dem.resource.FileSystemVoid.fileSystemVoid;
import static net.splitcells.dem.resource.communication.log.Logs.logs;
import static net.splitcells.dem.utils.StringUtils.removeSuffix;
import static net.splitcells.dem.utils.StringUtils.toBytes;
import static net.splitcells.website.server.client.HtmlClientImpl.websiteServerUrl;
import static net.splitcells.website.server.processor.BinaryMessage.binaryMessage;

public class ObjectsRendererI implements ProjectRenderer {
    private static final String CSV_SUFFIX = ".csv.html";

    public static ObjectsRendererI objectsRenderer(Path basePath) {
        return new ObjectsRendererI(basePath);
    }

    private final String pathPrefix;
    private final Map<Path, DiscoverableRenderer> objects = map();
    private final Map<Path, CsvRenderer> csvRenderers = map();
    private final Map<Object, Path> subjectPaths = map();

    private ObjectsRendererI(Path basePath) {
        this.pathPrefix = basePath.toString();
    }

    public Path publicLinkOf(DiscoverableRenderer object) {
        return objects.anyKeyBy(object);
    }

    public String publicLinkOf(Discoverable object) {
        return websiteServerUrl() + "/" + publicPath(object.path());
    }

    private String publicPath(ListView<String> path) {
        return pathPrefix + "/" + path.stream().reduce((a, b) -> a + "/" + b).orElseThrow();
    }

    @Override
    public FileSystem projectFileSystem() {
        return fileSystemVoid();
    }

    public synchronized ObjectsRendererI withObject(DiscoverableRenderer object) {
        return withObject(object, Optional.empty());
    }

    public synchronized ObjectsRendererI withObject(DiscoverableRenderer object, Optional<Object> subject) {
        var path = Path.of(publicPath(object.path()));
        if (objects.hasKey(path)) {
            // This makes it easier to analyse problems, when the same path is present multiple times.
            int i = 0;
            do {
                path = Path.of(publicPath(object.path()) + "." + ++i);
            } while (objects.hasKey(path));
            logs().warn(tree("Discoverable path is already registered. Using alternative path for rendering instead.")
                            .withProperty("object", object.toString())
                            .withProperty("path", path.toString())
                            .withProperty("alternative path", path.toString())
                    , ExecutionException.execException("Discoverable path is already registered."));
        }
        val finalPath = path;
        objects.put(finalPath, object);
        subject.ifPresent(s -> subjectPaths.put(s, finalPath));
        return this;
    }

    public synchronized ObjectsRendererI withObject(CsvRenderer object) {
        var path = Path.of(publicPath(object.path()));
        if (csvRenderers.hasKey(path)) {
            // This makes it easier to analyse problems, when the same path is present multiple times.
            int i = 0;
            do {
                path = Path.of(publicPath(object.path()) + "." + ++i);
            } while (csvRenderers.hasKey(path));
            logs().warn(tree("Discoverable path is already registered. Using alternative path for rendering instead.")
                            .withProperty("object", object.toString())
                            .withProperty("path", path.toString())
                            .withProperty("alternative path", path.toString())
                    , ExecutionException.execException("Discoverable path is already registered."));
        }
        csvRenderers.put(path, object);
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

    private String normalize(Path arg) {
        final var argString = arg.toString();
        if (argString.startsWith("/")) {
            return argString.substring(1);
        }
        return argString;
    }

    private String addCsvHtml(String arg) {
        if (arg.endsWith(".csv")) {
            return arg + ".html";
        }
        return arg;
    }

    @Override
    public synchronized Set<Path> projectPaths() {
        final var basePaths = objects.keySet2().mapped(this::normalize);
        final var csvHtmlPaths = csvRenderers.keySet2()
                .stream()
                .map(this::normalize)
                .map(this::addCsvHtml)
                .collect(toList());
        return basePaths.withAppended(csvHtmlPaths)
                .stream()
                .map(Path::of)
                .collect(Sets.toSetOfUniques());
    }

    @Override
    public synchronized Set<Path> relevantProjectPaths() {
        return setOfUniques(objects.keySet())
                .with(csvRenderers.keySet2());
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
        if (argPath.endsWith(CSV_SUFFIX)) {
            final var dataPath = removeSuffix(".html", argPath);
            final var dataPath2 = Path.of(dataPath);
            if (csvRenderers.containsKey(dataPath2)) {
                return Optional.of(projectsRenderer.renderCsvGraph(argPath, dataPath, csvRenderers.get(dataPath2).title().orElse("")));
            }
        }
        if (csvRenderers.containsKey(path)) {
            return Optional.of(binaryMessage(toBytes(csvRenderers.get(path).renderCsv()), ContentType.HTML_TEXT.toString()));
        }
        return Optional.empty();
    }

    @Override
    public String resourceRootPath() {
        return "/";
    }
}
