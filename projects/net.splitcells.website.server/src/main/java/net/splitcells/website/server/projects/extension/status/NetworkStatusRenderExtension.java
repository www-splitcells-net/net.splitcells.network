/*
 * Copyright (c) 2021 Mārtiņš Avots (Martins Avots) and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0, or the MIT License,
 * which is available at https://spdx.org/licenses/MIT.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR MIT
 */
package net.splitcells.website.server.projects.extension.status;

import net.splitcells.dem.data.set.Set;
import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.data.set.list.Lists;
import net.splitcells.dem.resource.ContentType;
import net.splitcells.dem.resource.communication.interaction.LogLevel;
import net.splitcells.dem.resource.host.HostName;
import net.splitcells.dem.utils.CommonFunctions;
import net.splitcells.website.Formats;
import net.splitcells.website.server.Config;
import net.splitcells.website.server.project.RenderingResult;
import net.splitcells.website.server.project.validator.RenderingValidatorForHtmlLinks;
import net.splitcells.website.server.projects.ProjectsRendererI;
import net.splitcells.website.server.projects.extension.ProjectsRendererExtension;

import java.nio.file.Path;
import java.time.LocalDate;
import java.util.Optional;

import static net.splitcells.dem.Dem.config;
import static net.splitcells.dem.data.order.Comparator.ASCENDING_DOUBLES;
import static net.splitcells.dem.data.order.Comparators.naturalComparator;
import static net.splitcells.dem.data.set.Sets.setOfUniques;
import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.data.set.list.Lists.listWithValuesOf;
import static net.splitcells.dem.data.set.list.Lists.toList;
import static net.splitcells.dem.utils.CommonFunctions.asString;
import static net.splitcells.dem.utils.CommonFunctions.getBytes;
import static net.splitcells.dem.utils.NotImplementedYet.notImplementedYet;
import static net.splitcells.dem.utils.StreamUtils.stream;
import static net.splitcells.network.worker.Logger.logger;
import static net.splitcells.website.server.project.RenderingResult.renderingResult;
import static net.splitcells.website.server.projects.extension.status.StatusReport.statusReport;

public class NetworkStatusRenderExtension implements ProjectsRendererExtension {
    private static final String STATUS_DOCUMENT_PATH = "net/splitcells/network/status.html";
    private static final String STATUS_PATH = "net/splitcells/network/status.csv";
    private static final Path RUNTIME_FOLDER = Path.of("net/splitcells/network/logger/builder/runtime");
    
    private static final int DAY_WARNING_THRESHOLD = 30;

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
                        final var csvContent = asString(projectsRendererI.render(config.rootPath() + "/" + p.toString())
                                .get()
                                .getContent(), ContentType.UTF_8);
                        final var lastMeasurement = stream(csvContent.split("\\R"))
                                .filter(l -> !l.trim().isEmpty())
                                .collect(toList())
                                .lastValue();
                        if (lastMeasurement.isPresent()) {
                            final var localDate = LocalDate.parse(lastMeasurement.get().split(",")[0]);
                            if (LocalDate.now().minusDays(DAY_WARNING_THRESHOLD).isAfter(localDate)) {
                                disruptedStatuses.append("<li>The build was not executed successfully on <q>"
                                        + p.getFileName().toString().substring(0, p.getFileName().toString().length() - 4)
                                        + "</q> in the last " + DAY_WARNING_THRESHOLD + " days.</li>");
                            } else {
                                successfulStatuses.append("<li>The build was executed successfully on <q>"
                                        + p.getFileName().toString().substring(0, p.getFileName().toString().length() - 4)
                                        + "</q> in the last " + DAY_WARNING_THRESHOLD + " days.</li>");
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
            final var disruptedTasks = "<h2>Disrupted Tasks</h2><ol>"
                    + disruptedStatuses
                    + "</ol>";
            final var successfulTasks = "<h2>Successful Tasks</h2><ol>"
                    + successfulStatuses
                    + "</ol>";
            return Optional.of(renderingResult(projectsRendererI.renderHtmlBodyContent(disruptedTasks + successfulTasks
                                    , Optional.of("Network Status")
                                    , Optional.of(STATUS_DOCUMENT_PATH)
                                    , config)
                            .get()
                    , ContentType.HTML_TEXT.codeName()));
        }
        if (path.equals("/" + STATUS_PATH)) {
            final List<LogLevel> logLevels = list();
            projectsRendererI.projectsPaths().stream()
                    .filter(p -> p.startsWith(RUNTIME_FOLDER))
                    .filter(p -> p.toString().endsWith(".csv"))
                    .forEach(p -> {
                        final var csvContent = asString(projectsRendererI.render(config.rootPath() + "/" + p.toString())
                                .get()
                                .getContent(), ContentType.UTF_8);
                        final var lastMeasurement = stream(csvContent.split("\\R"))
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
            return Optional.of(renderingResult(getBytes(statusLevel.name(), ContentType.UTF_8), Formats.TEXT_PLAIN.mimeTypes()));
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
