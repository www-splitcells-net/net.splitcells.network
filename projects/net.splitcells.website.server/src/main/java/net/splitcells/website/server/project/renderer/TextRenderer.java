package net.splitcells.website.server.project.renderer;

import net.splitcells.dem.data.set.Set;
import net.splitcells.dem.data.set.Sets;
import net.splitcells.dem.lang.Xml;
import net.splitcells.dem.lang.namespace.NameSpaces;
import net.splitcells.dem.resource.Files;
import net.splitcells.dem.resource.Paths;
import net.splitcells.website.server.project.FileStructureTransformer;
import net.splitcells.website.server.project.ProjectRenderer;
import net.splitcells.website.server.project.RenderingResult;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;

import static io.vertx.core.http.HttpHeaders.TEXT_HTML;
import static java.nio.charset.StandardCharsets.UTF_8;
import static net.splitcells.website.server.project.RenderingResult.renderingResult;

/**
 * Projects the file tree located "src/main/txt/" of the project's folder.
 * The projected path's replaces the "txt" file suffix with "html".
 * All files need to end with ".txt".
 */
public class TextRenderer implements Renderer {
    public static TextRenderer textExtension(FileStructureTransformer renderer) {
        return new TextRenderer(renderer);
    }

    private final FileStructureTransformer renderer;

    private TextRenderer(FileStructureTransformer renderer) {
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
                final var content = Xml.rElement(NameSpaces.NATURAL, "csv");
                content.appendChild(Xml.textNode(Paths.readString(textFile)));
                return Optional.of(renderingResult(renderer
                                .transform(Xml.toPrettyString(content))
                                .getBytes(UTF_8)
                        , TEXT_HTML.toString()));
            }
        } else {
            final var textFile = projectRenderer
                    .projectFolder()
                    .resolve("src/main/txt")
                    .resolve(path);
            if (Files.is_file(textFile)) {
                final var content = Xml.rElement(NameSpaces.NATURAL, "text");
                content.appendChild(Xml.textNode(Paths.readString(textFile)));
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
                java.nio.file.Files.walk(sourceFolder)
                        .filter(java.nio.file.Files::isRegularFile)
                        .map(file -> sourceFolder.relativize(file.getParent().resolve(file.getFileName().toString())))
                        .forEach(projectPaths::addAll);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return projectPaths;
    }
}
