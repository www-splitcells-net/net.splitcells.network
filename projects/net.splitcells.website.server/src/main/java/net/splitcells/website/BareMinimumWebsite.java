/*
 * Copyright (c) 2023 Contributors To The `net.splitcells.*` Projects
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
package net.splitcells.website;

import net.splitcells.dem.Dem;
import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.resource.Files;
import net.splitcells.website.server.Config;
import net.splitcells.website.server.project.ProjectRenderer;
import net.splitcells.website.server.project.validator.SourceValidator;
import net.splitcells.website.server.projects.ProjectsRendererI;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import static net.splitcells.dem.Dem.waitIndefinitely;
import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.resource.FileSystems.fileSystemOnLocalHost;
import static net.splitcells.dem.resource.Files.readFileAsString;
import static net.splitcells.dem.utils.ConstructorIllegal.constructorIllegal;
import static net.splitcells.website.server.project.ProjectRenderer.projectRenderer;
import static net.splitcells.website.server.project.validator.SourceValidator.VOID_VALIDATOR;
import static net.splitcells.website.server.projects.ProjectsRendererI.projectsRenderer;

public class BareMinimumWebsite {
    private BareMinimumWebsite() {
        throw constructorIllegal();
    }

    /**
     * It is assumed, that program is executed at the root folder of the `net.splitcells.network` repo.
     *
     * @param args
     */
    public static void main(String... args) {
        Dem.process(() -> {
            final var config = Config.create()
                    .withOpenPort(8080)
                    .withXmlSchema(Optional.empty())
                    .withIsSecured(false)
                    .withDetailedXslMenu(Optional.of(readFileAsString(Paths.get("projects/net.splitcells.website.content.default/src/main/xsl/net/splitcells/website/detailed-menu.xsl"))));
            final var xslLib = Paths.get("projects/net.splitcells.website.content.default/src/main/xsl/net/splitcells/website/den/translation/to/html/");
            final var projectRenderer = projectRenderer("public"
                    , Paths.get("projects/net.splitcells.website.content.minimal/")
                    , fileSystemOnLocalHost(xslLib)
                    , Paths.get("projects/net.splitcells.website.content.default/src/main/resources/html")
                    , "/"
                    , VOID_VALIDATOR
                    , config);
            final var targetFolder = Paths.get("target/minimal-test-website");
            Files.createDirectory(targetFolder);
            projectsRenderer("public", projectRenderer, list(), config).serveTo(targetFolder);
        });
    }
}
