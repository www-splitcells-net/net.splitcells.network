package net.splitcells.website.server.project.renderer.extension;

import net.splitcells.dem.data.set.Set;
import net.splitcells.dem.data.set.Sets;
import net.splitcells.dem.lang.Xml;
import net.splitcells.dem.lang.namespace.NameSpaces;
import net.splitcells.dem.resource.Paths;
import net.splitcells.website.server.Config;
import net.splitcells.website.server.project.ProjectRenderer;
import net.splitcells.website.server.project.RenderingResult;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;

import static io.vertx.core.http.HttpHeaders.TEXT_HTML;
import static java.nio.charset.StandardCharsets.UTF_8;
import static net.splitcells.dem.lang.Xml.optionalDirectChildElementsByName;
import static net.splitcells.dem.resource.Files.isDirectory;
import static net.splitcells.dem.resource.Files.is_file;
import static net.splitcells.website.server.project.LayoutConfig.layoutConfig;
import static net.splitcells.website.server.project.RenderingResult.renderingResult;

public class CsvChartProjectRendererExtension implements ProjectRendererExtension {
    public static CsvChartProjectRendererExtension csvChartRenderer() {
        return new CsvChartProjectRendererExtension();
    }

    private CsvChartProjectRendererExtension() {
    }

    @Override
    public Optional<RenderingResult> renderFile(String path, ProjectRenderer projectRenderer, Config config) {
        if (path.endsWith("csv.html")) {
            final var csvPath = path.substring(0, path.lastIndexOf(".csv.html")) + ".csv";
            final var requestedFile = projectRenderer
                    .projectFolder()
                    .resolve("src/main/csv/")
                    .resolve(csvPath);
            if (is_file(requestedFile)) {
                final var content = Xml.rElement(NameSpaces.SEW, "csv-chart-lines");
                final var contentsPath = Xml.elementWithChildren(NameSpaces.SEW, "path");
                contentsPath.appendChild(Xml.textNode("/" + csvPath));
                content.appendChild(contentsPath);
                content.appendChild(Xml.textNode(Paths.readString(requestedFile)));

                final var page = Xml.rElement(NameSpaces.SEW, "article");
                final var metaElement = Xml.rElement(NameSpaces.SEW, "meta");
                final var pathElement = Xml.rElement(NameSpaces.SEW, "path");
                pathElement.appendChild(Xml.textNode(path));
                metaElement.appendChild(pathElement);
                page.appendChild(metaElement);
                page.appendChild(content);
                return Optional.of(renderingResult(projectRenderer.renderRawXml(Xml.toPrettyString(page), config).get()
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
