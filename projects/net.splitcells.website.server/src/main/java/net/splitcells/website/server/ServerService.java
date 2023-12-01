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
package net.splitcells.website.server;

import net.splitcells.dem.Dem;
import net.splitcells.dem.environment.resource.ResourceOptionI;
import net.splitcells.dem.environment.resource.Service;
import net.splitcells.website.server.project.renderer.ObjectsMediaRenderer;
import net.splitcells.website.server.project.renderer.ObjectsRenderer;

import java.util.Optional;

import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.resource.FileSystems.fileSystemOnLocalHost;
import static net.splitcells.dem.resource.Files.readFileAsString;
import static net.splitcells.dem.resource.Paths.userHome;
import static net.splitcells.dem.utils.NotImplementedYet.notImplementedYet;
import static net.splitcells.website.Projects.projectsRenderer;
import static net.splitcells.website.server.project.ProjectRenderer.projectRenderer;
import static net.splitcells.website.server.project.validator.SourceValidatorViaSchema.validatorViaSchema;

public class ServerService extends ResourceOptionI<Service> {
    public ServerService() {
        super(() -> {
            // TODO This is a hack, because the webserver still depends on private documents, in order to render the website.
            final var publicProjectRepository = userHome(
                    "Documents/projects/net.splitcells.martins.avots.support.system/public/net.splitcells.network/projects");
            final var privateProjectRepository = userHome("Documents/projects/net.splitcells.martins.avots.support.system/private");
            final var xslLib = publicProjectRepository.resolve(
                    "net.splitcells.website.content.default/src/main/xsl/net/splitcells/website/den/translation/to/html/");
            final var validator = validatorViaSchema(
                    publicProjectRepository.resolve("net.splitcells.website.server").resolve("src/main/xsd/den.xsd"));
            final var config = Config.create()
                    .withOpenPort(8448)
                    .withIsSecured(false)
                    .withSiteFolder(Optional.of(userHome("Documents/projects/net.splitcells.martins.avots.support.system/public/net.splitcells.network/projects/").toString()))
                    .withDetailedXslMenu(Optional.of(readFileAsString(userHome("Documents/projects/net.splitcells.martins.avots.support.system/private/net.splitcells.martins.avots.website/src/main/resources/detailed-menu.xsl"))))
                    .withCssFiles(list("net/splitcells/website/css/theme.white.variables.css"
                            , "net/splitcells/website/css/basic.themed.css"
                            , "net/splitcells/website/css/basic.css"
                            , "net/splitcells/website/css/den.css"
                            , "net/splitcells/website/css/layout.default.css"
                            , "net/splitcells/martins/avots/website/css/theme.css"));
            /* TODO The config should only be read during service start,
             * in order to be sure, that the config is set up.
             */
            return projectsRenderer(publicProjectRepository, "public"
                    , projectRenderer(
                            "public", fileSystemOnLocalHost(privateProjectRepository.resolve("net.splitcells.martins.avots.website/"))
                            , fileSystemOnLocalHost(xslLib)
                            , fileSystemOnLocalHost(privateProjectRepository
                                    .resolve("net.splitcells.martins.avots.website/src/main/resources/html"))
                            , "/"
                            , validator
                            , config)
                    , list(projectRenderer("public"
                                    , fileSystemOnLocalHost(privateProjectRepository.resolve("net.splitcells.martins.avots.website/"))
                                    , fileSystemOnLocalHost(xslLib)
                                    , fileSystemOnLocalHost(privateProjectRepository
                                            .resolve("net.splitcells.martins.avots.website/src/main/resources/html"))
                                    , "/"
                                    , validator
                                    , config)
                            , projectRenderer("public"
                                    , fileSystemOnLocalHost(privateProjectRepository.resolve(
                                            "/home/splitcells/Documents/projects/net.splitcells.martins.avots.support.system/private/net.splitcells.martins.avots.website/"))
                                    , fileSystemOnLocalHost(xslLib)
                                    , fileSystemOnLocalHost(privateProjectRepository
                                            .resolve("net.splitcells.martins.avots.website/src/main/resources/html"))
                                    , "/"
                                    , validator
                                    , config)
                            , Dem.configValue(ObjectsRenderer.class)
                            , Dem.configValue(ObjectsMediaRenderer.class))
                    , validator
                    , config)
                    .httpServer();
        });
    }
}
