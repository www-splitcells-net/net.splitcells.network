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
package net.splitcells.gel.ui.no.code.editor;

import net.splitcells.dem.data.set.Set;
import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.data.set.list.Lists;
import net.splitcells.dem.resource.FileSystemView;
import net.splitcells.dem.resource.communication.log.LogLevel;
import net.splitcells.dem.utils.StringUtils;
import net.splitcells.gel.constraint.Query;
import net.splitcells.website.Formats;
import net.splitcells.website.server.Config;
import net.splitcells.website.server.processor.BinaryMessage;
import net.splitcells.website.server.project.LayoutConfig;
import net.splitcells.website.server.project.ProjectRenderer;
import net.splitcells.website.server.projects.ProjectsRenderer;
import net.splitcells.website.server.projects.ProjectsRendererI;
import net.splitcells.website.server.projects.extension.ProjectsRendererExtension;

import java.nio.file.Path;
import java.util.Optional;

import static io.vertx.core.http.HttpHeaders.TEXT_HTML;
import static net.splitcells.dem.data.set.Sets.setOfUniques;
import static net.splitcells.dem.utils.StringUtils.toBytes;
import static net.splitcells.website.server.processor.BinaryMessage.binaryMessage;

public class TopLevelFunctions implements ProjectsRendererExtension {

    private static final String PATH = "/net/splitcells/gel/ui/no/code/editor/top-level-functions.json";

    public static TopLevelFunctions topLevelFunctions() {
        return new TopLevelFunctions();
    }

    private TopLevelFunctions() {

    }

    /**
     * TODO HACK This data should be queried from the {@link Query} interface,
     * in order to avoid duplicating this information.
     * Currently, the {@link Query} interface does not provide this information.
     *
     * @param path
     * @param projectsRenderer
     * @param config
     * @return
     */
    @Override
    public Optional<BinaryMessage> renderFile(String path, ProjectsRendererI projectsRenderer, Config config) {
        if (PATH.equals(path)) {
            return Optional.of(binaryMessage(toBytes("["
                    + "\"attribute\""
                    + ",\"database\""
                    + ",\"solution\""
                    + ",\"forEach\""
                    + "]"), Formats.JSON));
        }
        return Optional.empty();
    }

    @Override
    public Set<Path> projectPaths(ProjectsRendererI projectsRenderer) {
        // Avoid first slash.
        return setOfUniques(Path.of(PATH.substring(1)));
    }
}
