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
import net.splitcells.dem.utils.StreamUtils;
import net.splitcells.website.server.project.LayoutConfig;
import net.splitcells.website.server.project.ProjectRenderer;
import net.splitcells.website.server.processor.BinaryMessage;
import net.splitcells.website.server.project.renderer.PageMetaData;
import net.splitcells.website.server.projects.ProjectsRenderer;

import java.nio.file.Path;
import java.util.Optional;
import java.util.stream.Stream;

import static net.splitcells.dem.data.set.list.Lists.toList;
import static net.splitcells.dem.lang.Xml.optionalDirectChildElementsByName;
import static net.splitcells.dem.lang.tree.Tree.subtree;
import static net.splitcells.dem.resource.ContentType.HTML_TEXT;
import static net.splitcells.dem.resource.Paths.removeFileSuffix;
import static net.splitcells.dem.utils.StreamUtils.emptyStream;
import static net.splitcells.website.Format.XML;
import static net.splitcells.website.server.project.LayoutConfig.layoutConfig;
import static net.splitcells.website.server.processor.BinaryMessage.binaryMessage;
import static net.splitcells.website.server.project.renderer.PageMetaData.pageMetaData;

/**
 * Projects the file tree located "src/main/xml/" of the project's folder.
 * The projected path's replaces the "xml" file suffix with "html".
 * All files need to end with ".xml".
 */
public class XmlProjectRendererExtension implements ProjectRendererExtension {

    private static final String MARKER = "834ZT09345ZHGF09H3457G90H34";

    public static XmlProjectRendererExtension xmlRenderer() {
        return new XmlProjectRendererExtension();
    }

    private XmlProjectRendererExtension() {

    }

    @Override
    public Optional<PageMetaData> metaData(String path, ProjectsRenderer projectsRenderer, ProjectRenderer projectRenderer) {
        final var xmlPath = xmlPath(path, projectsRenderer, projectRenderer);
        if (xmlPath.isPresent()) {
            final var metaData = pageMetaData(path);
            final var document = Xml.parse(projectRenderer.projectFileSystem().readString(xmlPath.get()));
            if (NameSpaces.SEW.uri().equals(document.getDocumentElement().getNamespaceURI())) {
                final var metaElement = optionalDirectChildElementsByName(document.getDocumentElement(), "meta", NameSpaces.SEW);
                if (metaElement.isPresent()) {
                    final var titleElement = optionalDirectChildElementsByName(metaElement.get(), "title", NameSpaces.SEW);
                    if (titleElement.isPresent()) {
                        metaData.withTitle(Optional.of(titleElement.get().getTextContent()));
                    }
                }
            }
            return Optional.of(metaData);
        }
        return Optional.empty();
    }

    /**
     * @param path
     * @param projectsRenderer
     * @param projectRenderer
     * @return This is the {@link ProjectRenderer#projectFileSystem()} path to the XML source code of the requested path.
     */
    private Optional<Path> xmlPath(String path, ProjectsRenderer projectsRenderer, ProjectRenderer projectRenderer) {
        if (path.endsWith(".html")) {
            final var xmlFile = Path.of("src/main/xml")
                    .resolve(path.substring(0, path.lastIndexOf(".html")) + ".xml");
            if (projectRenderer.projectFileSystem().isFile(xmlFile)) {
                return Optional.of(xmlFile);
            }
        }
        return Optional.empty();
    }

    @Override
    public Optional<BinaryMessage> renderFile(String path, ProjectsRenderer projectsRenderer, ProjectRenderer projectRenderer) {
        if (path.endsWith(".html")) {
            final var xmlPath = xmlPath(path, projectsRenderer, projectRenderer);
            final var pathFolder = StreamUtils.stream(path.split("/"))
                    .filter(s -> !s.isEmpty())
                    .collect(toList())
                    .withRemovedFromBehind(0);
            if (xmlPath.isPresent()) {
                final var layoutConfig = layoutConfig(path)
                        .withLocalPathContext(projectsRenderer.config().layoutPerspective()
                                .map(l -> subtree(l, pathFolder)));
                return renderFile(path, projectRenderer.projectFileSystem().readString(xmlPath.get())
                        , projectRenderer, projectsRenderer, layoutConfig);
            } else {
                final var sumXmlFile = Path.of("src/main/sum.xml")
                        .resolve(path.substring(0, path.lastIndexOf(".html")) + ".xml");
                if (projectRenderer.projectFileSystem().isFile(sumXmlFile)) {
                    final var layoutConfig = layoutConfig(path)
                            .withLocalPathContext(projectsRenderer.config().layoutPerspective()
                                    .map(l -> subtree(l, pathFolder)));
                    return renderFile(path, "<start xmlns=\"http://splitcells.net/den.xsd\">"
                                    + projectRenderer.projectFileSystem().readString(sumXmlFile)
                                    + "</start>"
                            , projectRenderer
                            , projectsRenderer
                            , layoutConfig);
                }
            }
        }
        return Optional.empty();
    }

    private Optional<BinaryMessage> renderFile(String path
            , String xmlContent
            , ProjectRenderer projectRenderer
            , ProjectsRenderer projectsRenderer
            , LayoutConfig layoutConfig) {
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
            final var localPathContext = layoutConfig.localPathContext();
            var pathContext = "";
            if (localPathContext.isPresent()) {
                pathContext += localPathContext.get().toXmlStringWithPrefixes();
            }
            var metaData = "";
            final var queriedMetaData = projectsRenderer.relevantParentPage(path);
            if (queriedMetaData.hasElements()) {
                final var relevantParentPages = optionalDirectChildElementsByName(metaElement, "relevant-parent-pages", NameSpaces.SEW)
                        .orElseGet(() -> document.createElementNS(NameSpaces.SEW.uri(), "relevant-parent-pages"));
                queriedMetaData.forEach(m -> {
                    final var parent = document.createElementNS(NameSpaces.SEW.uri(), "parent");
                    parent.setAttribute("path", m.path());
                    //parent.setAttribute("title", m.title().orElse(path));
                    //parent.setAttribute("indexed-folder-name", m.indexedFolderName().orElse(projectsRenderer.config().sitesName()));
                    parent.setAttribute("title", m.title()
                            .or(() -> {
                                if (m.path().contains("/")) {
                                    return Optional.of(m.path());
                                } else {
                                    return Optional.of(projectsRenderer.config().sitesName());
                                }
                            })
                            .orElse(path));
                    relevantParentPages.appendChild(parent);
                });
                metaData += Xml.toPrettyWithoutHeaderString(relevantParentPages);
            }
            documentString = Xml.toDocumentString(document)
                    .replace(MARKER, "<path.context "
                            + "xmlns:d=\"http://splitcells.net/den.xsd\""
                            + ">"
                            + pathContext
                            + "</path.context>"
                            + metaData);
            return Optional.of(binaryMessage
                    (projectRenderer.renderRawXml(documentString, projectsRenderer.config()).orElseThrow()
                            , HTML_TEXT.codeName()));
        } else {
            return Optional.of(binaryMessage
                    (projectRenderer.renderXml(xmlContent, layoutConfig, projectsRenderer.config()).orElseThrow()
                            , HTML_TEXT.codeName()));
        }
    }

    @Override
    public Set<Path> projectPaths(ProjectRenderer projectRenderer) {
        final var projectPaths = Sets.<Path>setOfUniques();
        final var sourceFolder = Path.of("src/main/xml");
        // TODO Move this code block into a function, in order to avoid
        if (projectRenderer.projectFileSystem().isDirectory(sourceFolder)) {
            projectRenderer.projectFileSystem().walkRecursively(sourceFolder)
                    .filter(p -> projectRenderer.projectFileSystem().isFile(p))
                    .map(file -> sourceFolder.relativize(file.getParent()
                            .resolve(removeFileSuffix(file.getFileName().toString()) + ".html")))
                    .forEach(projectPaths::addAll);
        }
        projectSumXlPaths(projectRenderer).forEach(projectPaths::addAll);
        return projectPaths;
    }

    private Stream<Path> projectSumXlPaths(ProjectRenderer projectRenderer) {
        final var sumXmlFolder = Path.of("src/main/sum.xml");
        if (projectRenderer.projectFileSystem().isDirectory(sumXmlFolder)) {
            return projectRenderer.projectFileSystem().walkRecursively(sumXmlFolder)
                    .filter(projectRenderer.projectFileSystem()::isFile)
                    .map(file -> sumXmlFolder.relativize(file.getParent()
                            .resolve(removeFileSuffix(file.getFileName().toString()) + ".html")));
        }
        return emptyStream();
    }

    @Override
    public Optional<BinaryMessage> sourceCode(String path, ProjectsRenderer projectsRenderer, ProjectRenderer projectRenderer) {
        final String normalizedPath;
        if (path.endsWith(".html")) {
            normalizedPath = path.substring(0, path.lastIndexOf(".html")) + ".xml";
        } else {
            normalizedPath = path;
        }
        final var xmlFile = Path.of("src/main/xml").resolve(normalizedPath);
        if (projectRenderer.projectFileSystem().isFile(xmlFile)) {
            return Optional.of(binaryMessage(projectRenderer.projectFileSystem().readFileAsBytes(xmlFile), XML));
        }
        return Optional.empty();
    }

    @Override
    public Set<Path> relevantProjectPaths(ProjectRenderer projectRenderer) {
        return projectPaths(projectRenderer);
    }
}
