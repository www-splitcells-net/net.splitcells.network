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
import net.splitcells.dem.data.set.list.Lists;
import net.splitcells.dem.lang.Xml;
import net.splitcells.dem.lang.namespace.NameSpaces;
import net.splitcells.dem.resource.Files;
import net.splitcells.dem.utils.StreamUtils;
import net.splitcells.website.server.Config;
import net.splitcells.website.server.project.ProjectRenderer;
import net.splitcells.website.server.project.RenderingResult;

import java.nio.file.Path;
import java.util.Optional;
import java.util.regex.Pattern;

import static net.splitcells.dem.data.set.list.Lists.toList;
import static net.splitcells.dem.environment.resource.Console.CONSOLE_FILE_NAME;
import static net.splitcells.dem.lang.Xml.optionalDirectChildElementsByName;
import static net.splitcells.dem.lang.perspective.Den.subtree;
import static net.splitcells.dem.lang.perspective.PerspectiveI.perspective;
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

    private static final String MARKER = "834ZT09345ZHGF09H3457G90H34";
    public static final Pattern XML_OPENING_ELEMENT = Pattern.compile("^([\\n\\r\\s]*)(<[a-zA-Z][a-zA-Z0-9-])(\\s*[\\sa-zA-Z0-9-=\\\":/[.\\n\\r]]*)(>).*");
    private static final Pattern XML_CLOSING_ELEMENT = Pattern.compile("(\\n\\r.*<)([a-zA-Z][a-zA-Z0-9-]*)([\\n\\r\\s]*[a-zA-Z0-9-=\\\":/\\.\\n\\r]*)(>[\\n\\r\\s]*)$");

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
            final var pathFolder = StreamUtils.stream(path.split("/"))
                    .filter(s -> !s.isEmpty())
                    .collect(toList())
                    .withRemovedFromBehind(0);
            final var layoutConfig = layoutConfig(path)
                    .withLocalPathContext(config.layoutPerspective()
                            .map(l -> subtree(l, pathFolder)));
            if (is_file(xmlFile)) {
                final var xmlContent = readString(xmlFile);
                // TODO This is probably a hack.
                if (CONSOLE_FILE_NAME.equals(xmlFile.getFileName().toString())) {
                    final var openingMatch = XML_OPENING_ELEMENT.matcher(xmlContent);
                    final var closingMatch = XML_CLOSING_ELEMENT.matcher(xmlContent);
                    if (openingMatch.matches()) {
                        if (!closingMatch.matches()) {
                            System.out.println("Incomplete xml found: " + path);
                        }
                    }
                }
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
                    metaElement.appendChild(document.createTextNode(MARKER));
                    final String documentString;
                    if (layoutConfig.localPathContext().isPresent()) {
                        documentString = Xml.toDocumentString(document)
                                .replace(MARKER, perspective("path.context", NameSpaces.NATURAL)
                                        .withChild(layoutConfig.localPathContext().get())
                                        .toHtmlString());
                    } else {
                        documentString = Xml.toDocumentString(document);
                    }
                    return Optional.of(renderingResult(projectRenderer.renderRawXml(documentString, config).get()
                            , HTML_TEXT.codeName()));
                } else {
                    return Optional.of(renderingResult(projectRenderer.renderXml(xmlContent, layoutConfig, config).get()
                            , HTML_TEXT.codeName()));
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
