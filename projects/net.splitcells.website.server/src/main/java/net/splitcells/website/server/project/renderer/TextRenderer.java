package net.splitcells.website.server.project.renderer;

import net.splitcells.dem.data.set.Set;
import net.splitcells.dem.data.set.Sets;
import net.splitcells.dem.lang.Xml;
import net.splitcells.dem.lang.namespace.NameSpaces;
import net.splitcells.dem.resource.Files;
import net.splitcells.dem.resource.Paths;
import net.splitcells.website.server.Config;
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
    public Optional<RenderingResult> renderFile(String path, ProjectRenderer projectRenderer, Config config) {
        Optional<Path> fileToRender = Optional.empty();
        if (path.endsWith(".html")) {
            // TODO This should return the raw text file.
            final var textFile = projectRenderer
                    .projectFolder()
                    .resolve("src/main/txt")
                    .resolve(path.substring(0, path.lastIndexOf(".html")) + ".txt");
            if (Files.is_file(textFile)) {
                fileToRender = Optional.of(textFile);
            }
        } else {
            final var textFile = projectRenderer
                    .projectFolder()
                    .resolve("src/main/txt")
                    .resolve(path);
            if (Files.is_file(textFile)) {
                fileToRender = Optional.of(textFile);
            }
        }
        if (fileToRender.isPresent()) {
            final var content = Xml.rElement(NameSpaces.NATURAL, "text");
            final var metaElement = Xml.rElement(NameSpaces.SEW, "meta");
            final var pathElement = Xml.rElement(NameSpaces.SEW, "path");
            pathElement.appendChild(Xml.textNode(path));
            metaElement.appendChild(pathElement);
            content.appendChild(metaElement);
            content.appendChild(Xml.textNode(Paths.readString(fileToRender.get())));
            return Optional.of(renderingResult(renderer
                            .transform(Xml.toPrettyString(content))
                            .getBytes(UTF_8)
                    , TEXT_HTML.toString()));
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
