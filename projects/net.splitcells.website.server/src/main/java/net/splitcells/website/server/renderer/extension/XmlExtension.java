package net.splitcells.website.server.renderer.extension;

import net.splitcells.dem.data.set.Set;
import net.splitcells.dem.data.set.Sets;
import net.splitcells.dem.resource.Files;
import net.splitcells.website.server.renderer.FileStructureTransformer;
import net.splitcells.website.server.renderer.ProjectRenderer;
import net.splitcells.website.server.renderer.RenderingResult;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Optional;

import static io.vertx.core.http.HttpHeaders.TEXT_HTML;
import static java.nio.charset.StandardCharsets.UTF_8;
import static net.splitcells.dem.resource.Paths.readString;
import static net.splitcells.website.server.renderer.RenderingResult.renderingResult;

public class XmlExtension implements ProjectRendererExtension {
    public static XmlExtension xmlExtension(FileStructureTransformer renderer) {
        return new XmlExtension(renderer);
    }

    private final FileStructureTransformer renderer;

    private XmlExtension(FileStructureTransformer renderer) {
        this.renderer = renderer;
    }

    @Override
    public Optional<RenderingResult> renderFile(String path, ProjectRenderer projectRenderer) {
        if (path.endsWith(".html")) {
            final var xmlFile = projectRenderer
                    .projectFolder()
                    .resolve("src/main")
                    .resolve("xml")
                    .resolve(path.substring(0, path.lastIndexOf(".html")) + ".xml");
            if (Files.is_file(xmlFile)) {
                return Optional.of(renderingResult(renderer
                                .transform(readString(xmlFile)).getBytes(UTF_8)
                        , TEXT_HTML.toString()));
            }
        }
        return Optional.empty();
    }

    @Override
    public Set<Path> projectPaths(ProjectRenderer projectRenderer) {
        final var projectPaths = Sets.<Path>setOfUniques();
        final var sourceFolder = projectRenderer.projectFolder().resolve("src/main").resolve("xml");
        // TODO Move this code block into a function, in order to avoid
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
