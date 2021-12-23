package net.splitcells.website.server.project.renderer;

import net.splitcells.dem.data.set.Set;
import net.splitcells.dem.data.set.Sets;
import net.splitcells.dem.lang.Xml;
import net.splitcells.dem.lang.namespace.NameSpaces;
import net.splitcells.dem.resource.Paths;
import net.splitcells.website.server.project.FileStructureTransformer;
import net.splitcells.website.server.project.ProjectRenderer;
import net.splitcells.website.server.project.RenderingResult;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;

import static io.vertx.core.http.HttpHeaders.TEXT_HTML;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.nio.file.Files.readAllBytes;
import static net.splitcells.dem.resource.Files.isDirectory;
import static net.splitcells.dem.resource.Files.is_file;
import static net.splitcells.dem.resource.Paths.readString;
import static net.splitcells.website.server.project.RenderingResult.renderingResult;

public class CsvChartRenderer implements Renderer {
    public static CsvChartRenderer csvChartRenderer(FileStructureTransformer renderer) {
        return new CsvChartRenderer(renderer);
    }

    private final FileStructureTransformer renderer;

    private CsvChartRenderer(FileStructureTransformer renderer) {
        this.renderer = renderer;
    }

    @Override
    public Optional<RenderingResult> renderFile(String path, ProjectRenderer projectRenderer) {
        if (path.endsWith("csv.html")) {
            final var csvPath = path.substring(0, path.lastIndexOf(".csv.html")) + ".csv";
            final var requestedFile = projectRenderer
                    .projectFolder()
                    .resolve("src/main/csv/")
                    .resolve(csvPath);
            if (is_file(requestedFile)) {
                final var content = Xml.rElement(NameSpaces.SEW, "csv-chart");
                final var contentsPath = Xml.elementWithChildren(NameSpaces.SEW, "path");
                contentsPath.appendChild(Xml.textNode("/" + csvPath));
                content.appendChild(contentsPath);
                content.appendChild(Xml.textNode(Paths.readString(requestedFile)));
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
        final var sourceFolder = projectRenderer
                .projectFolder()
                .resolve("src/main/csv/");
        if (isDirectory(sourceFolder)) {
            try {
                java.nio.file.Files.walk(sourceFolder)
                        .filter(java.nio.file.Files::isRegularFile)
                        .map(file -> sourceFolder.relativize(
                                file.getParent()
                                        .resolve(net.splitcells.dem.resource.Paths.removeFileSuffix
                                                (file.getFileName().toString()) + ".csv.html")))
                        .forEach(projectPaths::addAll);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return projectPaths;
    }
}
