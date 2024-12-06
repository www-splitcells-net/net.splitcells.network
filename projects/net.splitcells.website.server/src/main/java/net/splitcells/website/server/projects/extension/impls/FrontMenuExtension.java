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
import net.splitcells.dem.lang.tree.TreeI;
import net.splitcells.dem.utils.StreamUtils;
import net.splitcells.website.server.Config;
import net.splitcells.website.server.processor.BinaryMessage;
import net.splitcells.website.server.projects.ProjectsRendererI;
import net.splitcells.website.server.projects.RenderRequest;
import net.splitcells.website.server.projects.extension.ProjectsRendererExtension;

import java.nio.file.Path;
import java.util.Optional;

import static net.splitcells.dem.data.set.Sets.setOfUniques;
import static net.splitcells.dem.data.set.list.Lists.toList;
import static net.splitcells.dem.lang.namespace.NameSpaces.SEW;
import static net.splitcells.dem.lang.tree.TreeI.tree;
import static net.splitcells.dem.resource.ContentType.HTML_TEXT;
import static net.splitcells.website.server.processor.BinaryMessage.binaryMessage;

public class FrontMenuExtension implements ProjectsRendererExtension {

    private static final String EXTENSION_PATH = "/net/splitcells/website/server/front-menu.html";

    public static ProjectsRendererExtension frontMenuExtension() {
        return new FrontMenuExtension();
    }

    private FrontMenuExtension() {

    }

    @Override
    public Optional<BinaryMessage> renderFile(String path, ProjectsRendererI projectsRendererI, Config config) {
        if (EXTENSION_PATH.equals(path) && config.programConfigs().hasElements()) {
            final var pathFolder = StreamUtils.stream(path.split("/"))
                    .filter(s -> !s.isEmpty())
                    .collect(toList())
                    .withRemovedFromBehind(0);
            final var article = TreeI.tree("article", SEW);
            article.withChild(TreeI.tree("meta", SEW)
                    .withChildren(TreeI.tree("title", SEW).withText("Front Menu")
                            , TreeI.tree("description", SEW)
                                    .withText("Contains all major programs and documents of this site.")));
            final var deck = TreeI.tree("deck", SEW);
            article.withPath(TreeI.tree("content", SEW), deck);
            config.programConfigs()
                    .forEach(pc -> {
                        final var card = TreeI.tree("card", SEW);
                        card.withChild(TreeI.tree("name", SEW).withText(pc.name()));
                        if (pc.isPathUrl()) {
                            card.withChild(TreeI.tree("url", SEW).withText(pc.path()));
                        } else {
                            card.withChild(TreeI.tree("path", SEW).withText(pc.path()));
                        }
                        if (pc.description().isPresent())
                            card.withChild(TreeI.tree("description", SEW)
                                    .withText(pc.description().orElseThrow()));
                        if (pc.logoPath().isPresent())
                            card.withPath(TreeI.tree("logos", SEW)
                                    , TreeI.tree("image", SEW).withText(pc.logoPath().orElseThrow()));
                        deck.withChild(card);
                    });
            return Optional.of(binaryMessage(projectsRendererI.projectRenderers().get(0)
                            .renderRawXml(article.toXmlString(true), config)
                            .orElseThrow()
                    , HTML_TEXT.codeName()));
        }
        return Optional.empty();
    }

    @Override
    public boolean requiresAuthentication(RenderRequest request) {
        return false;
    }

    @Override
    public Set<Path> projectPaths(ProjectsRendererI projectsRendererI) {
        if (projectsRendererI.config().programConfigs().isEmpty()) return setOfUniques();
        return setOfUniques(Path.of(path(projectsRendererI.config()).substring(1)));
    }

    private String path(Config config) {
        return (config.rootPath() + EXTENSION_PATH).replace("//", "/");
    }
}
