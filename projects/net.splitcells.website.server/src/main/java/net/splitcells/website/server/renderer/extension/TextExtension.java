package net.splitcells.website.server.renderer.extension;

import net.splitcells.dem.data.set.Set;
import net.splitcells.dem.data.set.Sets;
import net.splitcells.dem.lang.Xml;
import net.splitcells.dem.lang.namespace.NameSpaces;
import net.splitcells.dem.resource.Files;
import net.splitcells.dem.resource.Paths;
import net.splitcells.website.server.renderer.FileStructureTransformer;
import net.splitcells.website.server.renderer.ProjectRenderer;
import net.splitcells.website.server.renderer.RenderingResult;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;

import static io.vertx.core.http.HttpHeaders.TEXT_HTML;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.nio.file.Files.readAllBytes;
import static net.splitcells.dem.resource.Paths.readString;
import static net.splitcells.website.server.renderer.RenderingResult.renderingResult;

public class TextExtension implements ProjectRendererExtension {
    public static TextExtension textExtension(FileStructureTransformer renderer) {
        return new TextExtension(renderer);
    }

    private final FileStructureTransformer renderer;

    private TextExtension(FileStructureTransformer renderer) {
        this.renderer = renderer;
    }

    @Override
    public Optional<RenderingResult> renderFile(String path, ProjectRenderer projectRenderer) {
        if (path.endsWith(".html")) {
            final var textFile = projectRenderer
                    .projectFolder()
                    .resolve("src/main/txt")
                    .resolve(path.substring(0, path.lastIndexOf(".html")) + ".txt");
            if (Files.is_file(textFile)) {
                final var content = Xml.rElement(NameSpaces.NATURAL, "text");
                content.appendChild(Xml.textNode(new String(Paths.readString(textFile))));
                return Optional.of(renderingResult(renderer
                                .transform(Xml.toPrettyString(content))
                                .getBytes(UTF_8)
                        , TEXT_HTML.toString()));
            }
        }
        return Optional.empty();
    }

    @Override
    public Set<Path> projectPaths(ProjectRenderer projectRenderer) {
        final var projectPaths = Sets.<Path>setOfUniques();
        final var sourceFolder = projectRenderer.projectFolder().resolve("src/main").resolve("txt");
        if (Files.isDirectory(sourceFolder)) {
            try {
                java.nio.file.Files.walk(sourceFolder)
                        .filter(java.nio.file.Files::isRegularFile)
                        .map(file -> sourceFolder.relativize(
                                file.getParent()
                                        .resolve(net.splitcells.dem.resource.Paths.removeFileSuffix
                                                (file.getFileName().toString()) + ".html")))
                        .forEach(projectPaths::addAll);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return projectPaths;
    }
}
