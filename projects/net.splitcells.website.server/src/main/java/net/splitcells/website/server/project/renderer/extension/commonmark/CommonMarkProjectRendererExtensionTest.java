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
package net.splitcells.website.server.project.renderer.extension.commonmark;

import net.splitcells.website.server.Config;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.util.Optional;

import static net.splitcells.dem.testing.TestTypes.INTEGRATION_TEST;
import static net.splitcells.website.server.project.ProjectRenderer.projectRenderer;
import static org.assertj.core.api.Assertions.assertThat;

public class CommonMarkProjectRendererExtensionTest {
    /**
     * TODO Test extension explicitly.
     */
    @Tag(INTEGRATION_TEST)
    @Test
    public void testCommonMarkLayout() {
        final var testSubject = projectRenderer
                ("public"
                        , Path.of("../..")
                        , Path.of("../net.splitcells.website.content.default/src/main/xsl/net/splitcells/website/den/translation/to/html/")
                        , Path.of("net.splitcells.website.content.default/src/main/resources/html")
                        , "/net/splitcells/"
                        , a -> Optional.empty()
                        , Config.create());
        assertThat(testSubject.projectPaths()).contains(Path.of("net/splitcells/network/guidelines/technology-stack.html"));
    }
}
