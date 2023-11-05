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
package net.splitcells.website.server.project.renderer.extension;

import net.splitcells.dem.data.atom.Bools;
import net.splitcells.website.server.Config;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.util.Optional;
import java.util.regex.Pattern;

import static net.splitcells.dem.resource.FileSystems.fileSystemOnLocalHost;
import static net.splitcells.dem.testing.TestTypes.INTEGRATION_TEST;
import static net.splitcells.website.server.project.ProjectRenderer.projectRenderer;
import static org.assertj.core.api.Assertions.assertThat;

public class XmlProjectRendererExtensionTest {
    /**
     * TODO Test extension explicitly.
     */
    @Tag(INTEGRATION_TEST)
    @Test
    public void testXmlLayout() {
        final var testSubject = projectRenderer("public"
                , fileSystemOnLocalHost(Path.of("../net.splitcells.dem"))
                , fileSystemOnLocalHost(Path.of("../net.splitcells.website.content.default/src/main/xsl/net/splitcells/website/den/translation/to/html/"))
                , fileSystemOnLocalHost(Path.of("../net.splitcells.website.content.default/src/main/resources/html"))
                , "/net/splitcells/dem"
                , a -> Optional.empty()
                , Config.create());
        assertThat(testSubject.projectPaths()).contains(Path.of("net/splitcells/dem/history.html"));
    }
}
