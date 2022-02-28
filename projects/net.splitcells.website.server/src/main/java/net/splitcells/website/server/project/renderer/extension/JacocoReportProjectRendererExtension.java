package net.splitcells.website.server.project.renderer.extension;

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
import static net.splitcells.dem.resource.Files.isDirectory;
import static net.splitcells.dem.resource.Files.is_file;
import static net.splitcells.website.server.project.RenderingResult.renderingResult;

public class JacocoReportProjectRendererExtension implements ProjectRendererExtension {
    public static JacocoReportProjectRendererExtension jacocoReportRenderer() {
        return new JacocoReportProjectRendererExtension();
    }

    private JacocoReportProjectRendererExtension() {

    }

    @Override
    public Optional<RenderingResult> renderFile(String path, ProjectRenderer projectRenderer, Config config) {
        if (("/" + path).startsWith(projectRenderer.resourceRootPath2().resolve("jacoco-report").toString())) {
            final var requestedFile = projectRenderer
                    .projectFolder()
                    .resolve("target/site/jacoco/")
                    .resolve(projectRenderer.resourceRootPath2().resolve("jacoco-report").relativize(Path.of("/" + path + "/")));
            if (is_file(requestedFile)) {
                try {
                    return Optional.of(renderingResult(readAllBytes(requestedFile), TEXT_HTML.toString()));
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public Set<Path> projectPaths(ProjectRenderer projectRenderer) {
        final var projectPaths = Sets.<Path>setOfUniques();
        final var sourceFolder = projectRenderer
                .projectFolder()
                .resolve("target/site/jacoco/");
        if (isDirectory(sourceFolder)) {
            try {
                java.nio.file.Files.walk(sourceFolder)
                        .filter(java.nio.file.Files::isRegularFile)
                        .map(file -> sourceFolder.relativize(file.getParent().resolve(file.getFileName().toString())))
                        .map(path -> projectRenderer.resourceRootPath2().resolve("jacoco-report").resolve(path))
                        .map(path -> Path.of(path.toString().substring(1))) // Convert absolute path to relative one.
                        .forEach(projectPaths::addAll);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return projectPaths;
    }
}