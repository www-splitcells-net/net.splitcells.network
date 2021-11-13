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
package net.splitcells.website.server.renderer;

import net.splitcells.dem.data.set.Set;
import net.splitcells.dem.data.set.list.Lists;
import net.splitcells.dem.lang.namespace.NameSpaces;
import net.splitcells.dem.lang.perspective.Perspective;
import net.splitcells.website.Validator;

import java.nio.file.Path;
import java.util.Optional;

import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.data.set.list.Lists.toList;
import static net.splitcells.dem.data.set.map.Maps.map;
import static net.splitcells.dem.lang.namespace.NameSpaces.STRING;
import static net.splitcells.dem.lang.perspective.PerspectiveI.perspective;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public interface ProjectRenderer extends Renderer {

    static ProjectRenderer projectRenderer(String renderer, Path projectFolder, Path xslLibs, Path resources
            , String resourceRootPath
            , boolean typedFolder
            , boolean flatRepository
            , Validator validator) {
        return new ProjectRendererI(renderer, projectFolder.resolve("src/main"), xslLibs, resources, resourceRootPath
                , true
                , false
                , validator
                , projectFolder);
    }

    static ProjectRenderer projectRenderer(String renderer, Path projectFolder, Path xslLibs, Path resources
            , String resourceRootPath
            , Validator validator) {
        return new ProjectRendererI(renderer, projectFolder.resolve("src/main"), xslLibs, resources, resourceRootPath
                , true
                , false
                , validator
                , projectFolder);
    }

    @Deprecated
    Path projectFolder();

    @Deprecated
    Optional<byte[]> renderString(String arg);

    Optional<byte[]> renderHtmlBodyContent(String bodyContent, Optional<String> title, Optional<String> path);
}
