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
package net.splitcells.website.server.projects.extension.impls;

import net.splitcells.dem.data.set.Set;
import net.splitcells.website.server.Config;
import net.splitcells.website.server.project.LayoutConfig;
import net.splitcells.website.server.processor.BinaryMessage;
import net.splitcells.website.server.projects.ProjectsRendererI;
import net.splitcells.website.server.projects.RenderRequest;
import net.splitcells.website.server.projects.extension.ProjectsRendererExtension;

import java.nio.file.Path;
import java.util.Optional;

import static net.splitcells.dem.data.set.Sets.setOfUniques;
import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.lang.tree.TreeI.tree;

/**
 * TODO The rendering is currently not useful,
 * because {@link LayoutExtension} currently create a lot better results from a design perspective.
 */
public class LayoutTreeExtension implements ProjectsRendererExtension {
    public static LayoutTreeExtension layoutTreeExtension() {
        return new LayoutTreeExtension();
    }

    private static final String PATH = "/net/splitcells/website/layout/tree.html";

    private LayoutTreeExtension() {

    }

    /**
     *
     * @param path
     * @param projectsRendererI
     * @param config
     * @return The rendered Tree contains meta data, so that Javascript can rerender the {@link Config#layout()}
     * with an interactive tree representation.
     */
    @Override
    public Optional<BinaryMessage> renderFile(String path, ProjectsRendererI projectsRendererI, Config config) {
        if (PATH.equals(path) && config.layout().isPresent()) {
            final var layout = tree("layout");
            projectsRendererI.projectsPaths().forEach(p -> layout.extendWith(list(p.toString().split("/"))));
            return projectsRendererI.renderContent
                    ("<div class=\"net-splitcells-website-tree-interactive\" source-path=\"/"
                                    + LayoutFancyTreeExtension.PATH.unixPathString()
                                    + "\" xmlns=\"http://www.w3.org/1999/xhtml\"><ol>"
                                    + layout.asXhtmlList(false)
                                    + "</ol></div>"
                            , LayoutConfig.layoutConfig(PATH));
        }
        return Optional.empty();
    }

    @Override
    public boolean requiresAuthentication(RenderRequest request) {
        return false;
    }

    @Override
    public Set<Path> projectPaths(ProjectsRendererI projectsRendererI) {
        if (projectsRendererI.config().layout().isPresent()) {
            return setOfUniques(Path.of(PATH.substring(1)));
        }
        return setOfUniques();
    }
}
