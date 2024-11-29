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
package net.splitcells.website.server.projects.extension;

import net.splitcells.dem.data.set.Set;
import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.lang.namespace.NameSpace;
import net.splitcells.dem.lang.namespace.NameSpaces;
import net.splitcells.dem.lang.tree.Tree;
import net.splitcells.dem.resource.Trail;
import net.splitcells.dem.utils.StringUtils;
import net.splitcells.website.Formats;
import net.splitcells.website.server.Config;
import net.splitcells.website.server.processor.BinaryMessage;
import net.splitcells.website.server.project.LayoutConfig;
import net.splitcells.website.server.projects.ProjectsRenderer;
import net.splitcells.website.server.projects.ProjectsRendererI;
import net.splitcells.website.server.projects.RenderRequest;
import net.splitcells.website.server.projects.RenderResponse;

import java.nio.file.Path;
import java.util.Optional;

import static net.splitcells.dem.data.set.Sets.setOfUniques;
import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.lang.namespace.NameSpaces.JSON;
import static net.splitcells.dem.lang.tree.Tree.*;
import static net.splitcells.dem.lang.tree.TreeI.tree;
import static net.splitcells.dem.resource.Trail.trail;
import static net.splitcells.dem.utils.StringUtils.stringBuilder;
import static net.splitcells.website.server.processor.BinaryMessage.binaryMessage;
import static net.splitcells.website.server.projects.RenderResponse.renderResponse;

/**
 * <p>Provides a JSON document formated for Fancytree,
 * that shows all documents of the web server.</p>
 * <p>TODO This is an experiment.</p>
 */
public class LayoutFancyTreeExtension implements ProjectsRendererExtension {
    public static LayoutFancyTreeExtension layoutFancyTreeExtension() {
        return new LayoutFancyTreeExtension();
    }

    public static final Trail PATH = trail("/net/splitcells/website/layout/tree-fancytree.json");

    private LayoutFancyTreeExtension() {

    }

    @Override
    public RenderResponse render(RenderRequest request, ProjectsRenderer projectsRenderer) {
        if (PATH.equals(request.trail()) && projectsRenderer.config().layout().isPresent()) {
            final var layout = tree("layout");
            projectsRenderer.projectsPaths().forEach(p -> layout.extendWith(list(p.toString().split("/"))));
            return renderResponse(Optional.of(binaryMessage(StringUtils.toBytes(asFancyTreeJson(layout)), Formats.JSON)));
        }
        return renderResponse(Optional.empty());
    }

    /**
     * <p>Assumes, that every {@link Tree} node represents a file or folder.
     * The {@link NameSpace} of each node is ignored.</p>
     * <p>TODO This method demonstrates,
     * that rendering JSONs via {@link Tree#toJsonString()} is not always the best in its current state.</p>
     *
     * @return Returns a JSON document,
     * that represents a tree formatted for the framework Javascript based HTML framework Fancytree.
     */
    private String asFancyTreeJson(Tree layout) {
        return "["
                + layout.children().stream()
                .map(c -> asFancyTreeJsonRecursion(c, list()).toJsonString())
                .reduce((a, b) -> a + "," + b)
                .orElse("")
                + "]";
    }

    private Tree asFancyTreeJsonRecursion(Tree layout, List<String> parentPath) {
        final String title;
        if (layout.name().contains(".")) {
            title = layout.name()
                    + " <a href=\""
                    + parentPath.stream().reduce("", (a, b) -> a + "/" + b)
                    + "/" + layout.name()
                    + "\"> (link)</a>";
        } else {
            title = layout.name();
        }
        final var result = tree(JSON_OBJECT, JSON).withProperty("title", title);
        if (layout.children().hasElements()) {
            result.withProperty("folder", tree(JSON_TRUE, JSON))
                    .withProperty("children"
                            , tree(JSON_ARRAY, JSON).withChildren
                                    (layout.children().stream().map(c ->
                                                    asFancyTreeJsonRecursion(c
                                                            , parentPath.shallowCopy().withAppended(layout.name())))
                                            .toList()));
        }
        return result;
    }

    /**
     * For now, the concept of a hidden path does not exist in the layout.
     *
     * @param request
     * @return
     */
    @Override
    public boolean requiresAuthentication(RenderRequest request) {
        return false;
    }

    @Override
    public Set<Path> projectPaths(ProjectPathsRequest request) {
        if (request.projectsRenderer().config().layout().isPresent()) {
            return setOfUniques(Path.of(PATH.unixPathString().substring(1)));
        }
        return setOfUniques();
    }
}
