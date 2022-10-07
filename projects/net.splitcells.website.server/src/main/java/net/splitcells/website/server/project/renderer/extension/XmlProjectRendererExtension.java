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
package net.splitcells.website.server.project.renderer.extension;

import net.splitcells.dem.data.set.Set;
import net.splitcells.dem.data.set.Sets;
import net.splitcells.dem.lang.Xml;
import net.splitcells.dem.lang.namespace.NameSpaces;
import net.splitcells.dem.resource.Files;
import net.splitcells.website.server.Config;
import net.splitcells.website.server.project.ProjectRenderer;
import net.splitcells.website.server.project.RenderingResult;

import java.nio.file.Path;
import java.util.Optional;

import static net.splitcells.dem.lang.Xml.optionalDirectChildElementsByName;
import static net.splitcells.dem.resource.ContentType.HTML_TEXT;
import static net.splitcells.dem.resource.Files.is_file;
import static net.splitcells.dem.resource.Paths.readString;
import static net.splitcells.website.server.project.LayoutConfig.layoutConfig;
import static net.splitcells.website.server.project.RenderingResult.renderingResult;

/**
 * Projects the file tree located "src/main/xml/" of the project's folder.
 * The projected path's replaces the "xml" file suffix with "html".
 * All files need to end with ".xml".
 */
public class XmlProjectRendererExtension implements ProjectRendererExtension {
    public static XmlProjectRendererExtension xmlRenderer() {
        return new XmlProjectRendererExtension();
    }

    private XmlProjectRendererExtension() {

    }

    @Override
    public Optional<RenderingResult> renderFile(String path, ProjectRenderer projectRenderer, Config config) {
        if (path.endsWith(".html")) {
            final var xmlFile = projectRenderer
                    .projectFolder()
                    .resolve("src/main/xml")
                    .resolve(path.substring(0, path.lastIndexOf(".html")) + ".xml");
            if (is_file(xmlFile)) {
                final var xmlContent = readString(xmlFile);
                final var document = Xml.parse(xmlContent);
                if (NameSpaces.SEW.uri().equals(document.getDocumentElement().getNamespaceURI())) {
                    final var metaElement = optionalDirectChildElementsByName(document.getDocumentElement(), "meta", NameSpaces.SEW)
                            .orElseGet(() -> {
                                final var newMeta = document.createElementNS(NameSpaces.SEW.uri(), "meta");
                                document.getDocumentElement().appendChild(newMeta);
                                return newMeta;
                            });
                    if (optionalDirectChildElementsByName(document.getDocumentElement(), "path", NameSpaces.SEW).isEmpty()) {
                        final var pathElement = document.createElementNS(NameSpaces.SEW.uri(), "path");
                        pathElement.appendChild(document.createTextNode(path));
                        metaElement.appendChild(pathElement);
                    }
                    return Optional.of(renderingResult(projectRenderer.renderRawXml(Xml.toDocumentString(document), config).get()
                            , HTML_TEXT.codeName()));
                } else {
                    return Optional.of(renderingResult(projectRenderer.renderXml(xmlContent, layoutConfig(path), config).get(), HTML_TEXT.codeName()));
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public Set<Path> projectPaths(ProjectRenderer projectRenderer) {
        final var projectPaths = Sets.<Path>setOfUniques();
        final var sourceFolder = projectRenderer.projectFolder().resolve("src/main").resolve("xml");
        // TODO Move this code block into a function, in order to avoid
        if (Files.isDirectory(sourceFolder)) {
            Files.walk_recursively(sourceFolder)
                    .filter(Files::fileExists)
                    .map(file -> sourceFolder.relativize(
                            file.getParent()
                                    .resolve(net.splitcells.dem.resource.Paths.removeFileSuffix
                                            (file.getFileName().toString()) + ".html")))
                    .forEach(projectPaths::addAll);
        }
        return projectPaths;
    }

    @Override
    public Set<Path> relevantProjectPaths(ProjectRenderer projectRenderer) {
        return projectPaths(projectRenderer);
    }
}
