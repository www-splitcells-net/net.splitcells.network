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
package net.splitcells.website.server.project;

import net.splitcells.dem.resource.FileSystemView;
import net.splitcells.website.server.processor.BinaryMessage;
import net.splitcells.website.server.project.renderer.PageMetaData;
import net.splitcells.website.server.project.validator.SourceValidator;
import net.splitcells.website.server.Config;
import net.splitcells.website.server.projects.ProjectsRenderer;

import java.nio.file.Path;
import java.util.Optional;

import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.data.set.map.Maps.map;
import static net.splitcells.dem.lang.perspective.TreeI.perspective;

/**
 * Renders a folder, that is interpreted as a folder containing a project.
 * This can also be a build system, where a source code project is compiled to deployable artifact.
 * <p>
 * This is often used to provide a virtual path system, where file paths relative to the project's root folder
 * are mapped to a set of path's.
 * These paths can be queried or projected to another filesystem.
 */
public interface ProjectRenderer extends Renderer {

    static ProjectRenderer projectRenderer(String renderer, FileSystemView projectFolder, FileSystemView xslLibs
            , FileSystemView resources
            , String resourceRootPath
            , boolean typedFolder
            , boolean flatRepository
            , SourceValidator sourceValidator
            , Config config) {
        return new ProjectRendererI(renderer
                , projectFolder.subFileSystemView("src/main")
                , xslLibs
                , resources
                , resourceRootPath
                , true
                , false
                , sourceValidator
                , projectFolder
                , config
                , Optional.empty());
    }

    static ProjectRenderer projectRenderer(String renderer, FileSystemView projectFolder, FileSystemView xslLibs
            , FileSystemView resources
            , String resourceRootPath
            , boolean typedFolder
            , boolean flatRepository
            , SourceValidator sourceValidator
            , Config config
            , ProjectsRenderer projectsRenderer) {
        return new ProjectRendererI(renderer
                , projectFolder.subFileSystemView("src/main")
                , xslLibs
                , resources
                , resourceRootPath
                , true
                , false
                , sourceValidator
                , projectFolder
                , config
                , Optional.of(projectsRenderer));
    }

    static ProjectRenderer projectRenderer(String renderer, FileSystemView projectFolder, FileSystemView xslLibs
            , FileSystemView resources
            , String resourceRootPath
            , SourceValidator sourceValidator
            , Config config) {
        return new ProjectRendererI(renderer
                , projectFolder
                , xslLibs
                , resources
                , resourceRootPath
                , true
                , false
                , sourceValidator
                , projectFolder
                , config
                , Optional.empty());
    }

    static ProjectRenderer projectRenderer(String renderer, FileSystemView projectFolder, FileSystemView xslLibs
            , FileSystemView resources
            , String resourceRootPath
            , SourceValidator sourceValidator
            , Config config
            , ProjectsRenderer projectsRenderer) {
        return new ProjectRendererI(renderer
                , projectFolder
                , xslLibs
                , resources
                , resourceRootPath
                , true
                , false
                , sourceValidator
                , projectFolder
                , config
                , Optional.of(projectsRenderer));
    }

    FileSystemView projectFileSystem();

    default Optional<PageMetaData> metaData(String path, ProjectsRenderer projectsRenderer) {
        return Optional.empty();
    }

    @Deprecated
    Optional<byte[]> renderString(String arg);

    default Optional<BinaryMessage> sourceCode(String path, ProjectsRenderer projectsRenderer) {
        return Optional.empty();
    }

    /**
     * <p>TODO This method should be based on {@link #renderXml(String, LayoutConfig, Config)}.</p>
     * <p>TODO Add links to parent pages to the render result for better navigation.</p>
     *
     * @param path Relative path of document to be rendered.
     */
    Optional<byte[]> renderHtmlBodyContent(String bodyContent
            , Optional<String> title
            , Optional<String> path
            , @Deprecated Config config
            , ProjectsRenderer projectsRenderer);

    /**
     * This is the main rendering method for documents of a project.
     *
     * @param xml          This is the document's content being rendered.
     * @param layoutConfig This is the document's metadata.
     * @param config       This is the general webserver config.
     * @return The rendered document. Currently, the output format is HTML.
     */
    Optional<byte[]> renderXml(String xml, LayoutConfig layoutConfig, @Deprecated Config config);

    /**
     * TODO Use {@link Path} objects instead of {@link String}s.
     *
     * @param path            Path To Be Rendered
     * @param config          This is the general webserver config.
     * @param projectRenderer Provides basic rendering utilities.
     * @return This is the rendering result, if the path is supported.
     */
    default Optional<BinaryMessage> render(String path, Config config, @Deprecated ProjectRenderer projectRenderer) {
        return Optional.empty();
    }

    /**
     * TODO Use {@link Path} objects instead of {@link String}s.
     *
     * @param path             Absolute Path To Be Rendered
     * @param projectsRenderer This is the complete renderer infrastructure.
     *                         For instance, here the {@link Config} of that infrastructure can be retrieved.
     * @return This is the rendering result, if the path is supported.
     */
    default Optional<BinaryMessage> render(String path, ProjectsRenderer projectsRenderer) {
        return render(path);
    }

    /**
     * TODO This method was created in order to remove direct usage of {@link FileStructureTransformer}.
     * This method will be later removed as well, as this is basically the same,
     * but without direct access.
     * Is this method maybe a good future idea, or should it be really deleted?
     * It should be deleted, because this makes it possible to render data without a path.
     *
     * @param xml    This is the document's content being rendered.
     * @param config This is the general webserver config.
     * @return The rendered document. Currently, the output format is HTML.
     */
    @Deprecated
    Optional<byte[]> renderRawXml(String xml, @Deprecated Config config);
}
