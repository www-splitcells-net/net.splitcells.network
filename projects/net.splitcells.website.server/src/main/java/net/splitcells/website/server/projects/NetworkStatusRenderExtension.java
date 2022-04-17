package net.splitcells.website.server.projects;

import net.splitcells.dem.data.order.Comparators;
import net.splitcells.dem.data.set.Set;
import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.data.set.list.Lists;
import net.splitcells.dem.resource.communication.interaction.LogLevel;
import net.splitcells.website.Formats;
import net.splitcells.website.server.Config;
import net.splitcells.website.server.project.RenderingResult;

import java.nio.file.Path;
import java.time.LocalDate;
import java.util.Optional;
import java.util.stream.Stream;

import static io.vertx.core.http.HttpHeaders.TEXT_HTML;
import static java.nio.charset.StandardCharsets.UTF_8;
import static net.splitcells.dem.data.order.Comparators.naturalComparator;
import static net.splitcells.dem.data.set.Sets.setOfUniques;
import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.data.set.list.Lists.listWithValuesOf;
import static net.splitcells.dem.data.set.list.Lists.toList;
import static net.splitcells.website.server.project.RenderingResult.renderingResult;

public class NetworkStatusRenderExtension implements ProjectsRendererExtension {
    private static final String STATUS_DOCUMENT_PATH = "net/splitcells/network/status.html";
    private static final String STATUS_PATH = "net/splitcells/network/status.csv";
    private static final Path RUNTIME_FOLDER = Path.of("net/splitcells/network/logger/builder/runtime");

    public static NetworkStatusRenderExtension networkStatusRenderExtension() {
        return new NetworkStatusRenderExtension();
    }

    @Override
    public Optional<RenderingResult> renderFile(String path, ProjectsRendererI projectsRendererI, Config config) {
        if (path.equals("/" + STATUS_DOCUMENT_PATH)) {
            final var status = new StringBuffer();
            projectsRendererI.projectsPaths().stream()
                    .filter(p -> p.startsWith(RUNTIME_FOLDER))
                    .filter(p -> p.toString().endsWith(".csv"))
                    .forEach(p -> {
                        final var csvContent = new String(projectsRendererI.render("/" + p.toString()).get().getContent(), UTF_8);
                        final var lastMeasurement = Stream.of(csvContent.split("\\R"))
                                .filter(l -> !l.trim().isEmpty())
                                .collect(toList())
                                .lastValue();
                        if (lastMeasurement.isPresent()) {
                            final var localDate = LocalDate.parse(lastMeasurement.get().split(",")[0]);
                            if (LocalDate.now().minusDays(-7).isAfter(localDate)) {
                                status.append("<li>"
                                        + p.getFileName().toString().substring(0, p.getFileName().toString().length() - 4)
                                        + "</li>");
                            }
                        }
                    });
            return Optional.of(renderingResult(projectsRendererI.renderHtmlBodyContent("<h2>Disrupted Tasks</h2><p>The following executors did not execute the network worker in the last 7 days.</p><ol>"
                                            + status
                                            + "</ol>"
                                    , Optional.of("Network Status")
                                    , Optional.of(path)
                                    , config)
                            .get()
                    , TEXT_HTML.toString()));
        }
        if (path.equals("/" + STATUS_PATH)) {
            final List<LogLevel> logLevels = list();
            projectsRendererI.projectsPaths().stream()
                    .filter(p -> p.startsWith(RUNTIME_FOLDER))
                    .filter(p -> p.toString().endsWith(".csv"))
                    .forEach(p -> {
                        final var csvContent = new String(projectsRendererI.render("/" + p.toString()).get().getContent(), UTF_8);
                        final var lastMeasurement = Stream.of(csvContent.split("\\R"))
                                .filter(l -> !l.trim().isEmpty())
                                .collect(toList())
                                .lastValue();
                        if (lastMeasurement.isPresent()) {
                            final var localDate = LocalDate.parse(lastMeasurement.get().split(",")[0]);
                            if (LocalDate.now().minusDays(-7).isAfter(localDate)) {
                                logLevels.withAppended(LogLevel.WARNING);
                            } else {
                                logLevels.withAppended(LogLevel.INFO);
                            }
                        }
                    });
            logLevels.sort(naturalComparator());
            final var statusLevel = logLevels.get(0);
            return Optional.of(renderingResult(statusLevel.name().getBytes(UTF_8), Formats.TEXT_PLAIN.mimeTypes()));
        }
        return Optional.empty();
    }

    @Override
    public Set<Path> projectPaths(ProjectsRendererI projectsRendererI) {
        return setOfUniques(Path.of(STATUS_DOCUMENT_PATH), Path.of(STATUS_PATH));
    }
}
