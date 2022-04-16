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
import static net.splitcells.dem.data.set.Sets.setOfUniques;
import static net.splitcells.dem.resource.Files.isDirectory;
import static net.splitcells.dem.resource.Files.is_file;
import static net.splitcells.website.Formats.CSS;
import static net.splitcells.website.server.project.RenderingResult.renderingResult;

public class JavadocProjectRendererExtension implements ProjectRendererExtension {
    private static final String RENDERED_JAVADOC_FOLDER = "javadoc";
    private static final String SOURCE_JAVADOC_FOLDER = "target/site/apidocs";

    public static JavadocProjectRendererExtension javadocReportRenderer() {
        return new JavadocProjectRendererExtension();
    }

    private JavadocProjectRendererExtension() {

    }

    @Override
    public Optional<RenderingResult> renderFile(String path, ProjectRenderer projectRenderer, Config config) {
        if (("/" + path).startsWith(projectRenderer.resourceRootPath2().resolve(RENDERED_JAVADOC_FOLDER).toString())) {
            final var requestedFile = projectRenderer
                    .projectFolder()
                    .resolve(SOURCE_JAVADOC_FOLDER)
                    .resolve(projectRenderer.resourceRootPath2().resolve(RENDERED_JAVADOC_FOLDER).relativize(Path.of("/" + path + "/")));
            if (is_file(requestedFile)) {
                try {
                    final String format;
                    if (path.endsWith(".css")) {
                        format = CSS.toString();
                    } else {
                        format = TEXT_HTML.toString();
                    }
                    return Optional.of(renderingResult(readAllBytes(requestedFile), format));
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
                .resolve(SOURCE_JAVADOC_FOLDER);
        if (isDirectory(sourceFolder)) {
            try {
                java.nio.file.Files.walk(sourceFolder)
                        .filter(java.nio.file.Files::isRegularFile)
                        .map(file -> sourceFolder.relativize(file.getParent().resolve(file.getFileName().toString())))
                        .map(path -> projectRenderer.resourceRootPath2().resolve(RENDERED_JAVADOC_FOLDER).resolve(path))
                        .map(path -> Path.of(path.toString().substring(1))) // Convert absolute path to relative one.
                        .forEach(projectPaths::addAll);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return projectPaths;
    }

    @Override
    public Set<Path> relevantProjectPaths(ProjectRenderer projectRenderer) {
        final var projectPaths = Sets.<Path>setOfUniques();
        final var sourceFolder = projectRenderer
                .projectFolder()
                .resolve(SOURCE_JAVADOC_FOLDER);
        if (isDirectory(sourceFolder)) {
            projectPaths.add(
                    Path.of(projectRenderer
                            .resourceRootPath2()
                            .resolve(RENDERED_JAVADOC_FOLDER)
                            .resolve("index.html")
                            .toString()
                            .substring(1)));
        }
        return projectPaths;
    }
}