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
import net.splitcells.dem.resource.Files;
import net.splitcells.website.server.Config;

import java.nio.file.Paths;
import java.util.Optional;

import static net.splitcells.dem.resource.Files.readFileAsString;
import static net.splitcells.dem.utils.ConstructorIllegal.constructorIllegal;
import static net.splitcells.website.Projects.projectsRenderer;

public class BareMinimumRendering {
    private BareMinimumRendering() {
        throw constructorIllegal();
    }

    /**
     * It is assumed, that program is executed at the root folder of the `net.splitcells.network` repo.
     *
     * @param args
     */
    public static void main(String... args) {
        Dem.process(() -> {
            projectsRenderer(Config.create()
                    .withRootPath("/net/splitcells/network/")
                    .withMainProjectRepositoryPath(Paths.get("projects"))
                    .withOpenPort(8080)
                    .withXmlSchema(Optional.empty())
                    .withIsSecured(false)
                    .withDetailedXslMenu(Optional.of(readFileAsString(Paths.get("projects/net.splitcells.website.content.default/src/main/xsl/net/splitcells/website/detailed-menu.xsl")))))
                    .serveToHttpAt();
        });
    }
}
