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
package net.splitcells.network.system;

import net.splitcells.dem.Dem;
import net.splitcells.dem.resource.Trail;
import net.splitcells.dem.testing.annotations.IntegrationTest;
import net.splitcells.dem.testing.annotations.UnitTest;
import net.splitcells.website.server.projects.RenderRequest;

import java.nio.file.Paths;
import java.util.Optional;

import static net.splitcells.dem.Dem.process;
import static net.splitcells.website.server.security.authentication.UserSession.ANONYMOUS_USER_SESSION;

public class SystemCellTest {

    @IntegrationTest
    public void testServingWebsiteToFolder() {
        process(() -> SystemCell.projectsRenderer(SystemCell.config()).serveTo(Paths.get("target/test"))
                , SystemCell.class);
    }

    @UnitTest
    public void testInvalidPath() {
        process(() -> SystemCell.projectsRenderer(SystemCell.config()).render(RenderRequest.renderRequest(
                        Trail.trail("invalid-path")
                        , Optional.empty()
                        , ANONYMOUS_USER_SESSION))
                , SystemCell.class);
    }
}
