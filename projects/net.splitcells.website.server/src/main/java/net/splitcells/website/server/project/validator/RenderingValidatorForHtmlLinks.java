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
package net.splitcells.website.server.project.validator;

import net.splitcells.dem.resource.communication.log.LogLevel;
import net.splitcells.dem.resource.host.HostName;
import net.splitcells.dem.utils.CommonFunctions;
import net.splitcells.website.Format;
import net.splitcells.website.server.projects.ProjectsRendererI;
import net.splitcells.website.server.processor.BinaryMessage;


import java.nio.file.Path;
import java.time.LocalDate;
import java.util.Optional;
import java.util.regex.Pattern;

import static net.splitcells.dem.Dem.config;
import static net.splitcells.dem.data.set.list.Lists.toList;
import static net.splitcells.dem.resource.communication.log.Logs.logs;
import static net.splitcells.network.worker.via.java.Logger.logger;

/**
 * Checks whether rendered HTML documents relative links can be rendered
 * by the given {@link ProjectsRendererI}.
 * Validation is done via Regex heuristics,
 * in order to avoid additional external dependencies.
 */
public class RenderingValidatorForHtmlLinks implements RenderingValidator {
    private static final Pattern HTML_HREF = Pattern.compile("(href=\\\")([^\\\"]*)(\\\")");
    /**
     * According to Sonarcloud's java:S5998 the possessive `++` avoids backtracking and
     * therefore a potential stackoverflow of Java's REGEX engine.
     */
    public static final Pattern PATH = Pattern.compile("(\\.)?(\\/)?([a-zA-Z0-9\\.\\-]++\\/)*[a-zA-Z0-9\\.\\-]+");

    public static RenderingValidatorForHtmlLinks renderingValidatorForHtmlLinks() {
        return new RenderingValidatorForHtmlLinks();
    }

    private int invalidLinkCount = 0;
    private String reportName = "default";

    private RenderingValidatorForHtmlLinks() {
    }

    @Override
    public boolean validate(Optional<BinaryMessage> content, ProjectsRendererI projectsRendererI, Path requestedPath) {
        if (content.isEmpty()) {
            return true;
        }
        if (!Format.HTML.mimeTypes().equals(content.get().getFormat())) {
            return true;
        }
        final var paths = projectsRendererI.projectsPaths();
        final var invalid = CommonFunctions.selectMatchesByRegex(
                        CommonFunctions.bytesToString(content.get().getContent())
                        , HTML_HREF
                        , 2)
                .filter(link -> !link.startsWith("http://") && !link.startsWith("https://"))
                .filter(link -> {
                    /**
                     * TODO Move path checking to dedicated method at {@link ProjectsRendererI}.
                     */
                    final var resolvedLinkString = requestedPath
                            .getParent()
                            .resolve(Path.of(link.replace("//", "/")))
                            .normalize()
                            .toString();
                    final Path resolvedLink;
                    if (resolvedLinkString.startsWith("/")) {
                        resolvedLink = Path.of(resolvedLinkString.substring(1));
                    } else {
                        resolvedLink = Path.of(resolvedLinkString);
                    }
                    /**
                     * If this is not done, links containing Javascript or
                     * consisting only of id,
                     * would be checked as well.
                     */
                    if (PATH.matcher(resolvedLink.toString()).matches()) {
                        final var isValid = paths.contains(resolvedLink);
                        // TODO HACK
                        if (!isValid) {
                            ++invalidLinkCount;
                            logs().append("Invalid link `"
                                            + link + "` from `"
                                            + requestedPath + "` resolved to `"
                                            + resolvedLink + "`."
                                    , LogLevel.ERROR);
                        }
                        return !isValid;
                    }
                    return true;
                }).collect(toList());
        return invalid.isEmpty();
    }

    @Override
    public void startReport(String name) {
        reportName = name;
        invalidLinkCount = 0;
    }

    @Override
    public void endReport() {
    }

    public static String reportPath(String reportName) {
        return RenderingValidatorForHtmlLinks.class.getName().replace('.', '/') + "/" + reportName;
    }

    public int invalidLinkCount() {
        return invalidLinkCount;
    }
}
