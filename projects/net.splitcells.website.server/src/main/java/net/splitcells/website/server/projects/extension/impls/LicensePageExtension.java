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
import net.splitcells.dem.resource.Trail;
import net.splitcells.website.Format;
import net.splitcells.website.server.processor.BinaryMessage;
import net.splitcells.website.server.projects.ProjectsRenderer;
import net.splitcells.website.server.projects.ProjectsRendererI;
import net.splitcells.website.server.projects.RenderRequest;
import net.splitcells.website.server.projects.RenderResponse;
import net.splitcells.website.server.projects.extension.ProjectsRendererExtension;

import java.nio.file.Path;
import java.util.Optional;

import static net.splitcells.dem.data.set.Sets.setOfUniques;
import static net.splitcells.dem.lang.namespace.NameSpaces.*;
import static net.splitcells.dem.lang.tree.TreeI.tree;
import static net.splitcells.dem.lang.tree.XmlConfig.xmlConfig;
import static net.splitcells.dem.resource.Trail.trail;
import static net.splitcells.website.server.projects.RenderResponse.renderResponse;

/**
 * Provides an overview of all licensing relevant info.
 */
public class LicensePageExtension implements ProjectsRendererExtension {

    public static final Trail PATH = trail("/net/splitcells/website/license-page.html");

    public static ProjectsRendererExtension licensePageExtension() {
        return new LicensePageExtension();
    }

    private static final String INTRO_TEXT = """
            Provides an overview of all licensing relevant documents.
            The user content stored by this software into a database, data storage or similar,
            is not considered by this licensing page.
            The licensing page only considers data, that is part of the program itself.""";

    private LicensePageExtension() {
    }

    /**
     * TODO A template should be used for the tree construction and not a building API.
     * 
     * @param request
     * @param projectsRenderer
     * @return
     */
    @Override
    public RenderResponse render(RenderRequest request, ProjectsRenderer projectsRenderer) {
        if (PATH.equals(request.trail())) {
            final var licensePage = tree("article", SEW)
                    .withProperty("meta", SEW, tree("title", SEW).withText("License Page"))
                    .withChild(tree("paragraph", SEW).withText(INTRO_TEXT));
            final var licenseChapter = tree("chapter", SEW).withParent(licensePage);
            licenseChapter.withProperty("title", SEW, "Licensing Documents");
            final var licenseList = tree("list", SEW).withParent(licenseChapter);
            projectsRenderer.config().getLicensePages().forEach(lp ->
                    licenseList.withChild(tree("link", SEW)
                            .withProperty("url", SEW, "/" + lp.getPath().unixPathString())
                            .withProperty("name", SEW, lp.getName())));
            final var licenseRendering = projectsRenderer.projectRenderers().get(0)
                    .renderRawXml(licensePage.toXmlString(xmlConfig().withPrintNameSpaceAttributeAtTop(true))
                            , projectsRenderer.config())
                    .orElseThrow();
            return renderResponse(BinaryMessage.binaryMessage(licenseRendering, Format.HTML));
        }
        return renderResponse(Optional.empty());
    }

    @Override
    public boolean requiresAuthentication(RenderRequest request) {
        return false;
    }

    @Override
    public Set<Path> projectPaths(ProjectsRendererI projectsRenderer) {
        return setOfUniques(Path.of(PATH.unixPathString()));
    }
}
