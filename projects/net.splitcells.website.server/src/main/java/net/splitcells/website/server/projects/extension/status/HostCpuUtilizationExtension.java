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
package net.splitcells.website.server.projects.extension.status;

import net.splitcells.dem.Dem;
import net.splitcells.dem.data.set.Set;
import net.splitcells.dem.environment.resource.HostUtilizationRecordService;
import net.splitcells.website.server.Config;
import net.splitcells.website.server.processor.BinaryMessage;
import net.splitcells.website.server.projects.ProjectsRendererI;
import net.splitcells.website.server.projects.extension.ProjectsRendererExtension;

import java.nio.file.Path;
import java.util.Optional;

import static net.splitcells.dem.data.set.Sets.setOfUniques;
import static net.splitcells.dem.lang.namespace.NameSpaces.SEW;
import static net.splitcells.dem.lang.perspective.TreeI.perspective;
import static net.splitcells.dem.resource.ContentType.HTML_TEXT;
import static net.splitcells.dem.utils.StringUtils.toBytes;
import static net.splitcells.website.server.processor.BinaryMessage.binaryMessage;

public class HostCpuUtilizationExtension implements ProjectsRendererExtension {

    private static final String REPORT_PATH = "net/splitcells/host/resource/cpu/utilization.csv.html";
    private static final String CSV_PATH = "net/splitcells/host/resource/cpu/utilization.csv";

    public static HostCpuUtilizationExtension hostCpuUtilizationExtension() {
        return new HostCpuUtilizationExtension();
    }

    private HostCpuUtilizationExtension() {

    }

    @Override
    public Optional<BinaryMessage> renderFile(String path, ProjectsRendererI projectsRenderer, Config config) {
        if (path.equals("/" + REPORT_PATH)) {
            final var page = perspective("article", SEW);
            final var meta = perspective("meta", SEW);
            meta.withChild(perspective("path", SEW).withText(path));
            meta.withChild(perspective("title", SEW).withText("Host CPU Utilization"));
            meta.withChild(perspective("full-screen-by-default", SEW).withText("true"));
            page.withChild(meta);
            page.withProperty("content", SEW, perspective("csv-chart-lines", SEW)
                    .withProperty("path", SEW, "/" + CSV_PATH));
            return Optional.of(binaryMessage(projectsRenderer.projectRenderers().get(0)
                            .renderRawXml(page.toXmlStringWithAllNameSpaceDeclarationsAtTop(), config)
                            .orElseThrow()
                    , HTML_TEXT.codeName()));
        } else if (path.equals("/" + CSV_PATH)) {
            return Optional.of(binaryMessage(
                    toBytes(Dem.configValue(HostUtilizationRecordService.class).cpuUtilizationReportAsCsv())
                    , HTML_TEXT.codeName()));
        }
        return Optional.empty();
    }

    @Override
    public Set<Path> projectPaths(ProjectsRendererI projectsRendererI) {
        return setOfUniques(Path.of(REPORT_PATH));
    }
}
