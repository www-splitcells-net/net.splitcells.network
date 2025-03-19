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
package net.splitcells.website.server.projects;

import net.splitcells.dem.data.set.Set;
import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.environment.resource.Service;
import net.splitcells.website.server.Config;
import net.splitcells.website.server.notify.NotificationQueue;
import net.splitcells.website.server.project.LayoutConfig;
import net.splitcells.website.server.project.ProjectRenderer;
import net.splitcells.website.server.processor.BinaryMessage;
import net.splitcells.website.server.project.renderer.PageMetaData;
import net.splitcells.website.server.projects.extension.impls.ProjectPathsRequest;
import net.splitcells.website.server.security.access.Firewall;

import java.nio.file.Path;
import java.util.Optional;

import static net.splitcells.dem.data.set.Sets.setOfUniques;
import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.data.set.list.Lists.listWithValuesOf;
import static net.splitcells.dem.lang.namespace.NameSpaces.*;
import static net.splitcells.dem.lang.namespace.NameSpaces.SEW;
import static net.splitcells.dem.lang.tree.TreeI.tree;
import static net.splitcells.dem.resource.ContentType.HTML_TEXT;
import static net.splitcells.dem.utils.NotImplementedYet.notImplementedYet;
import static net.splitcells.website.server.processor.BinaryMessage.binaryMessage;
import static net.splitcells.website.server.project.LayoutConfig.layoutConfig;
import static net.splitcells.website.server.projects.RenderResponse.renderResponse;

/**
 * <p>Provides a file system like API to project files,
 * that are rendered in a presentable manner.</p>
 * <p>TODO Support any rendering target formats.
 * Currently, the files are only rendered as HTML and other web compatible formats.</p>
 * <p>TODO Note, that this API has a lot of historic burdens and
 * therefor does not currently represent the goals, that are achieved with it.
 * When this project was started, it was not known how documentation should be rendered and
 * what requirements were for real world usage etc.,
 * as appropriate development expertise was not available.
 * Therefore, the webserver implementation started with a domain driven paradigm,
 * where the focus was on rendering the contents of project folders as one website.
 * Later it was figured out,
 * that hard coding the project driven rendering into the program code created convoluted code,
 * that was hard to refactor and extend.</p>
 */
public interface ProjectsRenderer extends Firewall {
    /**
     * <p>
     * Creates all meta info maybe required for rendering.
     * This method is called by all rendering methods, that render all files at once, by default,
     * except, if otherwise explicitly stated in the arguments.</p>
     * <p>
     * So normally, this method does not have to be called explicitly.
     * This is not the case for rendering methods, that only render single paths
     * like {@link #render(String)}.
     * </p>
     */
    void build();

    /**
     * TODO Create flag in order to trigger incremental,
     * where only changed files are build.
     * Find out changed files via modification time or git history.
     * <p>
     * TODO This belongs to a dedicated class.
     *
     * @param target Folder where the rendered files are written to.
     */
    @Deprecated
    void serveTo(Path target);

    /**
     * <p>TODO This belongs to a dedicated class.</p>
     * <p>`ProjectsRenderer#httpServer()` should not be used anymore. Use `Server#serveToHttpAt()` instead, because multi threading is not supported for `ProjectsRenderer#httpServer()`.</p>
     */
    @Deprecated
    Service httpServer();

    /**
     * TODO This belongs to a dedicated class.
     */
    @Deprecated
    Service authenticatedHttpsServer();

    /**
     * Renders the file of the given {@param path}.
     * This file has to be present in {@link #projectsPaths}.
     *
     * @param path This is the path of the file being rendered.
     * @return The rendered file.
     * @deprecated This is deprecated, as for every request an authorization should be done.
     * Also, this method is not future proof, as it is hard to add new rendering parameters.
     */
    @Deprecated
    Optional<BinaryMessage> render(String path);


    /**
     * <p>Renders the file of the given {@param request}.
     * This file has to be present in {@link #projectsPaths}.</p>
     * <p>This method and {@link #render(String)} should never return a non empty result for the same path.
     * This method has an empty default implementation,
     * because this method was created after {@link #render(String)}.
     * This method will completely replace {@link #render(String)} in the future.</p>
     *
     * @param request
     * @return
     */
    default RenderResponse render(RenderRequest request) {
        return renderResponse(Optional.empty());
    }

    /**
     * Retrieves the source code, for the given path.
     * This is helpful, if the content of one file, depends on the content of another file.
     *
     * @param path This is the path for which the source code is requested.
     * @return Returns the content, that is used the given path.
     */
    Optional<BinaryMessage> sourceCode(String path);

    /**
     * @return All Paths In Relative Form
     */
    Set<Path> projectsPaths();

    /**
     * @return All Relevant Paths In Relative Form
     */
    Set<Path> relevantProjectsPaths();

    Config config();

    @Deprecated
    List<ProjectRenderer> projectRenderers();

    Optional<PageMetaData> metaData(String path);

    @Deprecated
    Optional<byte[]> renderHtmlBodyContent(String bodyContent
            , Optional<String> title
            , Optional<String> path
            , Config config);

    /**
     * Renders a given String to the main output format.
     *
     * @param content     This is the String to be rendered.
     * @param metaContent Additional information about {@param content}.
     * @return This is the rendered String.
     */
    Optional<BinaryMessage> renderContent(String content, LayoutConfig metaContent);

    boolean requiresAuthentication(RenderRequest request);

    default RenderResponse renderMissingAccessRights(RenderRequest request) {
        return renderResponse(renderContent("<p xmlns=\"http://www.w3.org/1999/xhtml\">You do not have the rights to access this page.</p>"
                , layoutConfig(request.trail().unixPathString())));
    }

    default RenderResponse renderMissingLogin(RenderRequest request) {
        return renderResponse(renderContent("<p xmlns=\"http://www.w3.org/1999/xhtml\">Please login, in order to see this page.</p>"
                , layoutConfig(request.trail().unixPathString())));
    }

    default BinaryMessage renderCsvGraph(String path, String dataPath, String title) {
        final var page = tree("article", SEW)
                .withChild(tree("meta", SEW)
                        .withValues(
                                tree("path", SEW).withText(path)
                                , tree("title", SEW).withText(title)
                                , tree("full-screen-by-default", SEW).withText("true")))
                .withProperty("content", SEW, tree("csv-chart-lines", SEW)
                        .withProperty("path", SEW, dataPath));
        return binaryMessage(projectRenderers().get(0)
                        .renderRawXml(page.toXmlStringWithAllNameSpaceDeclarationsAtTop(), config())
                        .orElseThrow()
                , HTML_TEXT.codeName());
    }

    /**
     * Returns a list of relevant parent documents,
     * that can be used to navigate the website backwards in the topic hierarchy.
     * This list is offered to the user, in order to help the user to travers the website.
     *
     * @param path
     * @return Returns a list of relevant parent pages for the given part,
     * that itself represents a path to the current documents excluding the current document.
     * So at max only one element per parent folder is returned.
     * The list is sorted ascending order to the parent pages folder length.
     * This makes this list consistent to the given path.
     */
    default List<PageMetaData> relevantParentPage(String path) {
        final List<PageMetaData> relevantParentPages = list();
        final var pathElements = listWithValuesOf(path.split("/"));
        while (pathElements.hasElements()) {
            pathElements.removeAt(pathElements.size() - 1);
            relevantParentPage(path, pathElements, "index.html", this)
                    .or(() -> relevantParentPage(path, pathElements, "README.html", this))
                    .or(() -> relevantParentPage(path, pathElements, this))
                    .ifPresent(relevantParentPages::add);
        }
        return relevantParentPages.reverse();
    }

    private static Optional<PageMetaData> relevantParentPage(String path
            , List<String> pathElements
            , String parentFileName
            , ProjectsRenderer projectsRenderer) {
        final var potentialPath = pathElements.shallowCopy()
                .withAppended(parentFileName)
                .stream()
                .reduce("", (a, b) -> a + "/" + b)
                .substring(1);
        final var potentialPage = projectsRenderer.config().layoutPerspective().orElseThrow().pathOfDenValueTree(potentialPath);
        if (potentialPage.isPresent()) {
            final var parentPage = potentialPage.orElseThrow().stream()
                    .map(e -> e.propertyInstance(NAME, DEN).orElseThrow().valueName())
                    .reduce("", (a, b) -> a + "/" + b)
                    .substring(1);
            if (!parentPage.equals(path)) {
                return projectsRenderer.metaData(parentPage);
            }
        }
        return Optional.empty();
    }

    /**
     * @param path
     * @param pathElements
     * @param projectsRenderer
     * @return Returns the {@link PageMetaData} of the document, that has the same parent as the parent's parent of
     * the given path and whose file name is the parent's name of the given path plus ".html".
     */
    private static Optional<PageMetaData> relevantParentPage(String path
            , List<String> pathElements
            , ProjectsRenderer projectsRenderer) {
        pathElements = pathElements.shallowCopy();
        if (pathElements.isEmpty()) {
            return Optional.empty();
        }
        final var folderName = pathElements.removeLast();
        final var potentialPath = pathElements
                .withAppended(folderName + ".html")
                .stream()
                .reduce("", (a, b) -> a + "/" + b)
                .substring(1);
        final var potentialPage = projectsRenderer.config().layoutPerspective().orElseThrow().pathOfDenValueTree(potentialPath);
        if (potentialPage.isPresent()) {
            final var parentPage = potentialPage.orElseThrow().stream()
                    .map(e -> e.propertyInstance(NAME, DEN).orElseThrow().valueName())
                    .reduce("", (a, b) -> a + "/" + b)
                    .substring(1);
            if (!parentPage.equals(path)) {
                final var relevantParentPage = projectsRenderer.metaData(parentPage);
                final var meta = relevantParentPage.orElseThrow();
                if (pathElements.get(pathElements.size() - 2).equals(meta.title().orElse(folderName))) {
                    meta.withAlternativeNameOfIndexedFolder(Optional.of(folderName));
                }
                return relevantParentPage;
            }
        }
        return Optional.empty();
    }

    Set<Path> projectPaths(ProjectPathsRequest request);
}
