package net.splitcells.website.server.projects.extension.status;

import net.splitcells.dem.data.set.Set;
import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.data.set.list.Lists;
import net.splitcells.dem.resource.communication.interaction.LogLevel;
import net.splitcells.dem.resource.host.HostName;
import net.splitcells.website.Formats;
import net.splitcells.website.server.Config;
import net.splitcells.website.server.project.RenderingResult;
import net.splitcells.website.server.project.validator.RenderingValidatorForHtmlLinks;
import net.splitcells.website.server.projects.ProjectsRendererI;
import net.splitcells.website.server.projects.extension.ProjectsRendererExtension;

import java.nio.file.Path;
import java.time.LocalDate;
import java.util.Optional;
import java.util.stream.Stream;

import static io.vertx.core.http.HttpHeaders.TEXT_HTML;
import static java.nio.charset.StandardCharsets.UTF_8;
import static net.splitcells.dem.Dem.config;
import static net.splitcells.dem.data.order.Comparator.ASCENDING_DOUBLES;
import static net.splitcells.dem.data.order.Comparators.naturalComparator;
import static net.splitcells.dem.data.set.Sets.setOfUniques;
import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.data.set.list.Lists.listWithValuesOf;
import static net.splitcells.dem.data.set.list.Lists.toList;
import static net.splitcells.dem.resource.Paths.userHome;
import static net.splitcells.dem.utils.NotImplementedYet.notImplementedYet;
import static net.splitcells.network.worker.Logger.logger;
import static net.splitcells.website.server.project.RenderingResult.renderingResult;
import static net.splitcells.website.server.projects.extension.status.StatusReport.statusReport;

public class NetworkStatusRenderExtension implements ProjectsRendererExtension {
    private static final String STATUS_DOCUMENT_PATH = "net/splitcells/network/status.html";
    private static final String STATUS_PATH = "net/splitcells/network/status.csv";
    private static final Path RUNTIME_FOLDER = Path.of("net/splitcells/network/logger/builder/runtime");

    public static NetworkStatusRenderExtension networkStatusRenderExtension() {
        return new NetworkStatusRenderExtension();
    }

    @Override
    public Optional<RenderingResult> renderFile(String path, ProjectsRendererI projectsRendererI, Config config) {
        // TODO Avoid code duplication in if else structure.
        if (path.equals("/" + STATUS_DOCUMENT_PATH)) {
            final var disruptedStatuses = new StringBuffer();
            final var successfulStatuses = new StringBuffer();
            projectsRendererI.projectsPaths().stream()
                    .filter(p -> p.startsWith(RUNTIME_FOLDER))
                    .filter(p -> p.toString().endsWith(".csv"))
                    .forEach(p -> {
                        final var csvContent = new String(projectsRendererI.render(config.rootPath() + "/" + p.toString()).get().getContent(), UTF_8);
                        final var lastMeasurement = Stream.of(csvContent.split("\\R"))
                                .filter(l -> !l.trim().isEmpty())
                                .collect(toList())
                                .lastValue();
                        if (lastMeasurement.isPresent()) {
                            final var localDate = LocalDate.parse(lastMeasurement.get().split(",")[0]);
                            if (LocalDate.now().minusDays(7).isAfter(localDate)) {
                                disruptedStatuses.append("<li>Build not executed successfully on "
                                        + p.getFileName().toString().substring(0, p.getFileName().toString().length() - 4)
                                        + ".</li>");
                            } else {
                                successfulStatuses.append("<li>Build executed successfully on "
                                        + p.getFileName().toString().substring(0, p.getFileName().toString().length() - 4)
                                        + ".</li>");
                            }
                        }
                    });
            final var linkValidityStatusReport = linkValidityStatus();
            if (linkValidityStatusReport.logLevel().equals(LogLevel.INFO)) {
                successfulStatuses.append(linkValidityStatusReport.report());
            } else if (linkValidityStatusReport.logLevel().equals(LogLevel.WARNING)) {
                disruptedStatuses.append(linkValidityStatusReport.report());
            } else {
                throw notImplementedYet();
            }
            final var disruptedTasks = "<h2>Disrupted Tasks</h2><p>The following executors did not execute the network worker in the last 7 days:</p><ol>"
                    + disruptedStatuses
                    + "</ol>";
            final var successfulTasks = "<h2>Successful Tasks</h2><p>The following executors did execute the network worker in the last 7 days:</p><ol>"
                    + successfulStatuses
                    + "</ol>";
            return Optional.of(renderingResult(projectsRendererI.renderHtmlBodyContent(disruptedTasks + successfulTasks
                                    , Optional.of("Network Status")
                                    , Optional.of(STATUS_DOCUMENT_PATH)
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
                        final var csvContent = new String(projectsRendererI.render(config.rootPath() + "/" + p.toString()).get().getContent(), UTF_8);
                        final var lastMeasurement = Stream.of(csvContent.split("\\R"))
                                .filter(l -> !l.trim().isEmpty())
                                .collect(toList())
                                .lastValue();
                        if (lastMeasurement.isPresent()) {
                            final var localDate = LocalDate.parse(lastMeasurement.get().split(",")[0]);
                            if (LocalDate.now().minusDays(7).isAfter(localDate)) {
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

    private StatusReport linkValidityStatus() {
        // TODO HACK This should be create via an Dem Option (aka dependency injection).
        final var logRepo = logger();
        final var invalidLinkHistory = logRepo.readExecutionResults(RenderingValidatorForHtmlLinks.reportPath("build"), config().configValue(HostName.class));
        final var invalidLinkTable = Lists.list(invalidLinkHistory.split("\n"));
        invalidLinkTable.remove(0);
        final var historyMinimum = invalidLinkTable.stream()
                .map(l -> l.split(",")[1])
                .map(Double::parseDouble)
                .min(ASCENDING_DOUBLES)
                .orElse(0d);
        final var currentInvalidLinkCount = invalidLinkTable.lastValue()
                .map(l -> l.split(",")[1])
                .map(Double::parseDouble)
                .orElse(0d);
        if (currentInvalidLinkCount.equals(historyMinimum)) {
            return statusReport(LogLevel.INFO
                    , "<li><a href=\""
                            + "/net/splitcells/website/server/project/validator/RenderingValidatorForHtmlLinks/build/"
                            + config().configValue(HostName.class)
                            + ".csv.html"
                            + "\">The number of invalid links is historically improving.</a></li>");
        }
        return statusReport(LogLevel.WARNING
                , "<li><a href=\""
                        + "/net/splitcells/website/server/project/validator/RenderingValidatorForHtmlLinks/build/"
                        + config().configValue(HostName.class)
                        + ".csv.html"
                        + "\">There are increases in the history of the invalid link count.</a></li>");
    }
}
