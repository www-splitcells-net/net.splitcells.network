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
package net.splitcells.website.server.project;

import net.splitcells.website.server.project.validator.SourceValidator;
import net.splitcells.website.server.Config;

import java.nio.file.Path;
import java.util.Optional;

import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.data.set.map.Maps.map;
import static net.splitcells.dem.lang.perspective.PerspectiveI.perspective;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

/**
 * Renders a folder, that is interpreted as a folder containing a project.
 * This can also be a build system, where a source code project is compiled to deployable artifact.
 * <p>
 * This is often used to provide a virtual path system, where file paths relative to the project's root folder
 * are mapped to a set of path's.
 * These paths can be queried or projected to another filesystem.
 */
public interface ProjectRenderer extends Renderer {

    static ProjectRenderer projectRenderer(String renderer, Path projectFolder, Path xslLibs, Path resources
            , String resourceRootPath
            , boolean typedFolder
            , boolean flatRepository
            , SourceValidator sourceValidator) {
        return new ProjectRendererI(renderer, projectFolder.resolve("src/main"), xslLibs, resources, resourceRootPath
                , true
                , false
                , sourceValidator
                , projectFolder);
    }

    static ProjectRenderer projectRenderer(String renderer, Path projectFolder, Path xslLibs, Path resources
            , String resourceRootPath
            , SourceValidator sourceValidator) {
        return new ProjectRendererI(renderer, projectFolder.resolve("src/main"), xslLibs, resources, resourceRootPath
                , true
                , false
                , sourceValidator
                , projectFolder);
    }

    Path projectFolder();

    @Deprecated
    Optional<byte[]> renderString(String arg);

    Optional<byte[]> renderHtmlBodyContent(String bodyContent
            , Optional<String> title
            , Optional<String> path
            , Config config);
}
