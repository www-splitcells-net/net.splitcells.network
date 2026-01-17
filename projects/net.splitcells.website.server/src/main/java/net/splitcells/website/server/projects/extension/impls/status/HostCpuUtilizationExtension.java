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

import net.splitcells.dem.Dem;
import net.splitcells.dem.data.set.Set;
import net.splitcells.dem.environment.resource.HostUtilizationRecordService;
import net.splitcells.dem.resource.Trail;
import net.splitcells.website.server.projects.ProjectsRenderer;
import net.splitcells.website.server.projects.ProjectsRendererI;
import net.splitcells.website.server.projects.RenderRequest;
import net.splitcells.website.server.projects.RenderResponse;
import net.splitcells.website.server.projects.extension.impls.ProjectPathsRequest;
import net.splitcells.website.server.projects.extension.ProjectsRendererExtension;

import java.nio.file.Path;
import java.util.Optional;

import static net.splitcells.dem.data.set.Sets.setOfUniques;
import static net.splitcells.dem.lang.namespace.NameSpaces.SEW;
import static net.splitcells.dem.lang.tree.TreeI.tree;
import static net.splitcells.dem.resource.ContentType.HTML_TEXT;
import static net.splitcells.dem.resource.Trail.trail;
import static net.splitcells.dem.utils.StringUtils.toBytes;
import static net.splitcells.website.server.processor.BinaryMessage.binaryMessage;
import static net.splitcells.website.server.projects.RenderResponse.renderResponse;
import static net.splitcells.website.server.security.authorization.AdminRole.ADMIN_ROLE;
import static net.splitcells.website.server.security.authorization.Authorization.hasRole;
import static net.splitcells.website.server.security.authorization.Authorization.missesRole;

public class HostCpuUtilizationExtension implements ProjectsRendererExtension {

    private static final Trail REPORT_PATH = trail("net/splitcells/host/resource/cpu/utilization.csv.html");
    private static final Trail CSV_PATH = trail("net/splitcells/host/resource/cpu/utilization.csv");

    public static HostCpuUtilizationExtension hostCpuUtilizationExtension() {
        return new HostCpuUtilizationExtension();
    }

    private HostCpuUtilizationExtension() {

    }

    @Override
    public boolean requiresAuthentication(RenderRequest request) {
        return request.trail().equalContents(REPORT_PATH) || request.trail().equalContents(CSV_PATH);
    }

    @Override
    public RenderResponse render(RenderRequest request, ProjectsRenderer projectsRenderer) {
        if (!request.trail().equalContents(REPORT_PATH) && !request.trail().equalContents(CSV_PATH)) {
            return renderResponse();
        }
        if (missesRole(request.user(), ADMIN_ROLE)) {
            return projectsRenderer.renderMissingAccessRights(request);
        }
        if (request.trail().equalContents(REPORT_PATH)) {
            final var page = tree("article", SEW);
            final var meta = tree("meta", SEW);
            meta.withChild(tree("path", SEW).withText(request.trail().unixPathString()));
            meta.withChild(tree("title", SEW).withText("Host CPU Utilization"));
            meta.withChild(tree("full-screen-by-default", SEW).withText("true"));
            page.withChild(meta);
            page.withProperty("content", SEW, tree("csv-chart-lines", SEW)
                    .withProperty("path", SEW, "/" + CSV_PATH));
            return renderResponse(Optional.of(binaryMessage(projectsRenderer.projectRenderers().get(0)
                            .renderRawXml(page.toXmlStringWithAllNameSpaceDeclarationsAtTop(), projectsRenderer.config())
                            .orElseThrow()
                    , HTML_TEXT.codeName())));
        } else if (request.trail().equalContents(CSV_PATH)) {
            return renderResponse(Optional.of(binaryMessage(
                    toBytes(Dem.configValue(HostUtilizationRecordService.class).cpuUtilizationReportAsCsv())
                    , HTML_TEXT.codeName())));
        }
        return renderResponse(Optional.empty());
    }

    @Override
    public Set<Path> projectPaths(ProjectsRendererI projectsRendererI) {
        return setOfUniques();
    }

    @Override
    public Set<Path> projectPaths(ProjectPathsRequest request) {
        if (hasRole(request.user(), ADMIN_ROLE)) {
            return setOfUniques(Path.of(REPORT_PATH.unixPathString()));
        }
        return setOfUniques();
    }
}
