/*
 * Copyright (c) 2021 Contributors To The `net.splitcells.*` Projects
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the Eclipse
 * Public License, v. 2.0 are satisfied: GNU General Public License v2.0 or later
 * which is available at https://www.gnu.org/licenses/old-licenses/gpl-2.0-standalone.html
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.website.server.projects.extension.impls.status;

import net.splitcells.dem.data.set.Set;
import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.data.set.list.Lists;
import net.splitcells.dem.resource.ContentType;
import net.splitcells.dem.resource.communication.log.LogLevel;
import net.splitcells.dem.resource.host.HostName;
import net.splitcells.network.worker.via.java.Tester;
import net.splitcells.website.Format;
import net.splitcells.website.server.Config;
import net.splitcells.website.server.processor.BinaryMessage;
import net.splitcells.website.server.project.validator.RenderingValidatorForHtmlLinks;
import net.splitcells.website.server.projects.ProjectsRendererI;
import net.splitcells.website.server.projects.RenderRequest;
import net.splitcells.website.server.projects.extension.ProjectsRendererExtension;

import java.nio.file.Path;
import java.time.LocalDate;
import java.util.Optional;

import static net.splitcells.dem.Dem.config;
import static net.splitcells.dem.data.order.Comparators.ASCENDING_DOUBLES;
import static net.splitcells.dem.data.order.Comparators.naturalComparator;
import static net.splitcells.dem.data.set.Sets.setOfUniques;
import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.data.set.list.Lists.listWithValuesOf;
import static net.splitcells.dem.data.set.list.Lists.toList;
import static net.splitcells.dem.resource.communication.log.LogLevel.INFO;
import static net.splitcells.dem.resource.communication.log.LogLevel.WARNING;
import static net.splitcells.dem.utils.CommonFunctions.asString;
import static net.splitcells.dem.utils.CommonFunctions.getBytes;
import static net.splitcells.dem.utils.NotImplementedYet.notImplementedYet;
import static net.splitcells.dem.utils.StreamUtils.stream;
import static net.splitcells.dem.utils.StringUtils.stringBuilder;
import static net.splitcells.network.worker.via.java.Logger.logger;
import static net.splitcells.website.server.processor.BinaryMessage.binaryMessage;
import static net.splitcells.website.server.projects.extension.impls.status.StatusReport.statusReport;

/**
 * TODO This probably belongs to the network worker project.
 */
public class NetworkStatusRenderExtension implements ProjectsRendererExtension {
    private static final String STATUS_DOCUMENT_PATH = "net/splitcells/network/status.html";
    private static final String STATUS_PATH = "net/splitcells/network/status.csv";

    private static final int DAY_WARNING_THRESHOLD = 30;

    public static NetworkStatusRenderExtension networkStatusRenderExtension() {
        return new NetworkStatusRenderExtension();
    }

    @Override
    public Optional<BinaryMessage> renderFile(String path, ProjectsRendererI projectsRendererI, Config config) {
        // TODO Avoid code duplication in if else structure.
        if (path.equals("/" + STATUS_DOCUMENT_PATH) || path.equals("/" + STATUS_PATH)) {
            final var disruptedStatuses = stringBuilder();
            final var successfulStatuses = stringBuilder();
            projectsRendererI.projectsPaths().stream()
                    .filter(p -> p.startsWith(Tester.REPORT_PATH))
                    .filter(p -> p.toString().endsWith(".csv"))
                    .forEach(p -> {
                        final var csvContent = asString(projectsRendererI.render(config.rootPath() + "/" + p.toString())
                                .orElseThrow()
                                .getContent(), ContentType.UTF_8);
                        final var lastMeasurement = stream(csvContent.split("\\R"))
                                .filter(l -> !l.trim().isEmpty())
                                .collect(toList())
                                .lastValue();
                        if (1f == Float.parseFloat(lastMeasurement.orElse("0").split(",")[1].trim())) {
                            successfulStatuses.append("<li>The last Tester run was executed successfully on <q>");
                            successfulStatuses.append(p.getFileName().toString().substring(0, p.getFileName().toString().length() - 4));
                            successfulStatuses.append("</li>");
                        } else {

                            disruptedStatuses.append("<li>The last Tester run was not executed successfully on <q>");
                            disruptedStatuses.append(p.getFileName().toString().substring(0, p.getFileName().toString().length() - 4));
                            disruptedStatuses.append("</li>");
                        }
                    });
            final var disruptedTasks = "<h2>Disrupted Tasks</h2><ol>"
                    + disruptedStatuses
                    + "</ol>";
            final var successfulTasks = "<h2>Successful Tasks</h2><ol>"
                    + successfulStatuses
                    + "</ol>";
            if (path.equals("/" + STATUS_DOCUMENT_PATH)) {
                return Optional.of(binaryMessage(projectsRendererI.renderHtmlBodyContent(disruptedTasks + successfulTasks
                                        , Optional.of("Network Status")
                                        , Optional.of(STATUS_DOCUMENT_PATH)
                                        , config)
                                .orElseThrow()
                        , ContentType.HTML_TEXT.codeName()));
            } else {
                if (disruptedStatuses.isEmpty()) {
                    return Optional.of(binaryMessage(getBytes(INFO.name(), ContentType.UTF_8), Format.TEXT_PLAIN.mimeTypes()));
                }
                return Optional.of(binaryMessage(getBytes(WARNING.name(), ContentType.UTF_8), Format.TEXT_PLAIN.mimeTypes()));
            }
        }
        return Optional.empty();
    }

    @Override
    public boolean requiresAuthentication(RenderRequest request) {
        return false;
    }

    @Override
    public Set<Path> projectPaths(ProjectsRendererI projectsRendererI) {
        return setOfUniques(Path.of(STATUS_DOCUMENT_PATH), Path.of(STATUS_PATH));
    }
}
