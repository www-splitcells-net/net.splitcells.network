package net.splitcells.website.server.project.renderer;

import net.splitcells.dem.data.set.Set;
import net.splitcells.dem.data.set.Sets;
import net.splitcells.website.server.Config;
import net.splitcells.website.server.project.ProjectRenderer;
import net.splitcells.website.server.project.RenderingResult;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;

import static io.vertx.core.http.HttpHeaders.TEXT_HTML;
import static java.nio.file.Files.readAllBytes;
import static net.splitcells.dem.data.set.Sets.setOfUniques;
import static net.splitcells.dem.resource.Files.isDirectory;
import static net.splitcells.dem.resource.Files.is_file;
import static net.splitcells.website.server.project.RenderingResult.renderingResult;

/**
 * Projects the file tree located "src/main/resources/html/" of the project's folder.
 * <p>
 * TODO Split resources into actual HTML documents and other binary data.
 */
public class ResourceProjectRendererExtension implements ProjectRendererExtension {
    public static ResourceProjectRendererExtension resourceRenderer() {
        return new ResourceProjectRendererExtension();
    }

    private ResourceProjectRendererExtension() {

    }

    @Override
    public Optional<RenderingResult> renderFile(String path, ProjectRenderer projectRenderer, Config config) {
        final var requestedFile = projectRenderer
                .projectFolder()
                .resolve("src/main/resources/html/")
                .resolve(path);
        if (is_file(requestedFile)) {
            try {
                final String format;
                if (path.endsWith(".svg")) {
                    format = "image/svg+xml";
                } else if (path.endsWith(".jpg")) {
                    format = "image/jpeg";
                } else {
                    format = TEXT_HTML.toString();
                }
                return Optional.of(renderingResult(readAllBytes(requestedFile), format));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return Optional.empty();
    }

    @Override
    public Set<Path> projectPaths(ProjectRenderer projectRenderer) {
        final var projectPaths = Sets.<Path>setOfUniques();
        final var sourceFolder = projectRenderer
                .projectFolder()
                .resolve("src/main/resources/html/");
        if (isDirectory(sourceFolder)) {
            try {
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
