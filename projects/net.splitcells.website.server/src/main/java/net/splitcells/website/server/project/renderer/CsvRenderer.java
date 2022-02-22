package net.splitcells.website.server.project.renderer;

import net.splitcells.dem.data.set.Set;
import net.splitcells.dem.data.set.Sets;
import net.splitcells.website.server.config.Context;
import net.splitcells.website.server.project.ProjectRenderer;
import net.splitcells.website.server.project.RenderingResult;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;

import static io.vertx.core.http.HttpHeaders.TEXT_HTML;
import static java.nio.file.Files.readAllBytes;
import static net.splitcells.dem.resource.Files.isDirectory;
import static net.splitcells.dem.resource.Files.is_file;
import static net.splitcells.website.server.project.RenderingResult.renderingResult;

public class CsvRenderer implements Renderer {
    public static CsvRenderer csvRenderer() {
        return new CsvRenderer();
    }

    private CsvRenderer() {

    }

    @Override
    public Optional<RenderingResult> renderFile(String path, ProjectRenderer projectRenderer, Context context) {
        final var requestedFile = projectRenderer
                .projectFolder()
                .resolve("src/main/csv/")
                .resolve(path);
        if (is_file(requestedFile)) {
            try {
                return Optional.of(renderingResult(readAllBytes(requestedFile), TEXT_HTML.toString()));
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
                .resolve("src/main/csv/");
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
