package net.splitcells.website.server.renderer;

import net.splitcells.dem.data.set.Set;
import net.splitcells.dem.data.set.list.Lists;
import net.splitcells.dem.data.set.map.Map;
import net.splitcells.dem.lang.Xml;
import net.splitcells.dem.lang.namespace.NameSpaces;
import net.splitcells.dem.lang.perspective.Perspective;
import net.splitcells.dem.resource.host.Files;
import net.splitcells.dem.resource.host.interaction.LogLevel;
import net.splitcells.website.Validator;
import net.splitcells.website.server.renderer.extension.ExtensionMerger;
import net.splitcells.website.server.renderer.extension.commonmark.CommonMarkRenderer;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.Optional;

import static io.vertx.core.http.HttpHeaders.TEXT_HTML;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.nio.file.Files.readAllBytes;
import static java.util.stream.Stream.concat;
import static net.splitcells.dem.data.set.Sets.toSetOfUniques;
import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.data.set.list.Lists.toList;
import static net.splitcells.dem.data.set.map.Maps.map;
import static net.splitcells.dem.lang.namespace.NameSpaces.STRING;
import static net.splitcells.dem.lang.perspective.PerspectiveI.perspective;
import static net.splitcells.dem.resource.host.Files.isDirectory;
import static net.splitcells.dem.resource.host.Files.is_file;
import static net.splitcells.dem.resource.host.interaction.Domsole.domsole;
import static net.splitcells.website.server.renderer.extension.ExtensionMerger.extensionMerger;
import static net.splitcells.website.server.renderer.extension.UserCommandExtension.userCommandExtension;
import static net.splitcells.website.server.renderer.RenderingResult.renderingResult;
import static net.splitcells.website.server.renderer.extension.commonmark.CommonMarkReadmeExtension.commonMarkReadmeExtension;
import static net.splitcells.website.server.renderer.extension.commonmark.CommonMarkRenderer.commonMarkRenderer;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public interface ProjectRenderer {

    static ProjectRenderer projectRenderer(String renderer, Path projectFolder, Path xslLibs, Path resources
            , String resourceRootPath
            , Validator validator) {
        return new ProjectRendererI(renderer, projectFolder.resolve("src/main"), xslLibs, resources, resourceRootPath
                , true
                , false
                , validator
                , projectFolder);
    }

    static ProjectRenderer projectRenderer(String renderer, Path projectFolder, Path xslLibs, Path resources
            , String resourceRootPath
            , Validator validator
            , Map<String, String> parameters) {
        return new ProjectRendererI(renderer, projectFolder.resolve("src/main"), xslLibs, resources, resourceRootPath
                , true
                , false
                , validator
                , projectFolder
                , parameters);
    }

    static ProjectRenderer projectRenderer(String renderer, Path projectFolder, Path xslLibs, Path resources, String resourceRootPath
            , boolean typedFolder
            , boolean flatRepository
            , Validator validator
            , Map<String, String> parameters) {
        return new ProjectRendererI(renderer, projectFolder.resolve("src/main"), xslLibs, resources, resourceRootPath, typedFolder, flatRepository, validator
                , projectFolder
                , parameters);
    }

    /**
     * @param current
     * @param relativeProjectPath Path relative to the project folders src/xml folder. This path also represent absolute path in projects namespace.
     */
    static Perspective extendPerspectiveWithPath(Perspective current, Path relativeProjectPath) {
        for (final var element : list(relativeProjectPath.toString().split("/"))
                .stream()
                .filter(e -> !"".contentEquals(e))
                .collect(toList())) {
            final var children = current.children().stream()
                    .filter(child -> child.nameIs(NameSpaces.VAL, NameSpaces.NATURAL))
                    .filter(child -> child.propertyInstances(NameSpaces.NAME, NameSpaces.NATURAL).stream()
                            .anyMatch(property -> property.value().get().name().equals(element)))
                    .collect(Lists.toList());
            final Perspective child;
            if (children.isEmpty()) {
                child = perspective(NameSpaces.VAL, NameSpaces.NATURAL)
                        .withProperty(NameSpaces.NAME, NameSpaces.NATURAL, element);
                current.withChild(child);
            } else {
                child = children.get(0);
            }
            current = child;
        }
        current.withChild(
                perspective(NameSpaces.LINK, NameSpaces.DEN)
                        .withChild(perspective(NameSpaces.URL, NameSpaces.DEN)
                                .withChild(perspective("/" + relativeProjectPath.toString(), STRING))));
        return current;
    }

    Path project();

    Path projectFolder();

    Optional<RenderingResult> render(String path);

    Optional<byte[]> renderString(String arg);

    Optional<byte[]> renderHtmlBodyContent(String bodyContent, Optional<String> title);

    String resourceRootPath();

    Perspective projectLayout();

    Set<Path> projectPaths();
}
