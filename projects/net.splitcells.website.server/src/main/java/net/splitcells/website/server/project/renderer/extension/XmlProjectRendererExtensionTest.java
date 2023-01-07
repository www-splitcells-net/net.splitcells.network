/*
 * Copyright (c) 2021 Mārtiņš Avots (Martins Avots) and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0, or the MIT License,
 * which is available at https://spdx.org/licenses/MIT.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR MIT
 */
package net.splitcells.website.server.project.renderer.extension;

import net.splitcells.dem.data.atom.Bools;
import net.splitcells.website.server.Config;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.util.Optional;
import java.util.regex.Pattern;

import static net.splitcells.dem.testing.TestTypes.INTEGRATION_TEST;
import static net.splitcells.website.server.project.ProjectRenderer.projectRenderer;
import static net.splitcells.website.server.project.renderer.extension.XmlProjectRendererExtension.XML_OPENING_ELEMENT;
import static org.assertj.core.api.Assertions.assertThat;

public class XmlProjectRendererExtensionTest {
    /**
     * TODO Test extension explicitly.
     */
    @Tag(INTEGRATION_TEST)
    @Test
    public void testXmlLayout() {
        final var testSubject = projectRenderer("public"
                , Path.of("../net.splitcells.dem")
                , Path.of("../net.splitcells.website.default.content/src/main/xsl/net/splitcells/website/den/translation/to/html/")
                , Path.of("net.splitcells.website.default.content/src/main/resources/html")
                , "/net/splitcells/dem"
                , a -> Optional.empty()
                , Config.create());
        assertThat(testSubject.projectPaths()).contains(Path.of("net/splitcells/dem/history.html"));
    }
}
