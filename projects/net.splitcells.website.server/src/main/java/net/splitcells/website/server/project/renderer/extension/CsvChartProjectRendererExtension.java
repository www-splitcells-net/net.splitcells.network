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
package net.splitcells.website.server.project.renderer.extension;

import net.splitcells.dem.data.set.Set;
import net.splitcells.dem.data.set.Sets;
import net.splitcells.dem.lang.Xml;
import net.splitcells.dem.lang.namespace.NameSpaces;
import net.splitcells.website.server.Config;
import net.splitcells.website.server.project.ProjectRenderer;
import net.splitcells.website.server.project.RenderingResult;

import java.nio.file.Path;
import java.util.Optional;

import static net.splitcells.dem.resource.ContentType.HTML_TEXT;
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
            final var requestedFile = Path.of("src/main/csv/").resolve(csvPath);

            if (projectRenderer.projectFileSystem().isFile(requestedFile)) {
                final var content = Xml.rElement(NameSpaces.SEW, "csv-chart-lines");
                final var contentsPath = Xml.elementWithChildren(NameSpaces.SEW, "path");
                contentsPath.appendChild(Xml.textNode("/" + csvPath));
                content.appendChild(contentsPath);
                content.appendChild(Xml.textNode(projectRenderer.projectFileSystem().readString(requestedFile)));

                final var page = Xml.rElement(NameSpaces.SEW, "article");
                final var metaElement = Xml.rElement(NameSpaces.SEW, "meta");
                final var pathElement = Xml.rElement(NameSpaces.SEW, "path");
                pathElement.appendChild(Xml.textNode(path));
                metaElement.appendChild(pathElement);
                page.appendChild(metaElement);
                page.appendChild(content);
                return Optional.of(renderingResult(projectRenderer.renderRawXml(Xml.toPrettyString(page), config).orElseThrow()
                        , HTML_TEXT.codeName()));
            }
        }
        return Optional.empty();
    }

    @Override
    public Set<Path> projectPaths(ProjectRenderer projectRenderer) {
        final var projectPaths = Sets.<Path>setOfUniques();
        final var sourceFolder = Path.of("src/main/csv/");
        if (projectRenderer.projectFileSystem().isDirectory(sourceFolder)) {
            projectRenderer.projectFileSystem().walkRecursively(sourceFolder)
                    .filter(projectRenderer.projectFileSystem()::isFile)
                    .map(file -> sourceFolder.relativize(
                            file.getParent()
                                    .resolve(net.splitcells.dem.resource.Paths.removeFileSuffix
                                            (file.getFileName().toString()) + ".csv.html")))
                    .forEach(projectPaths::addAll);
        }
        return projectPaths;
    }
}
