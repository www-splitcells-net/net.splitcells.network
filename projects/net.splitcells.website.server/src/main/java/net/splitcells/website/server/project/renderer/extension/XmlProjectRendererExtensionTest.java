/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.website.server.project.renderer.extension;

import net.splitcells.website.server.Config;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.util.Optional;

import static net.splitcells.dem.resource.PathFileSystem.pathFileSystem;
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
                , pathFileSystem(Path.of("../net.splitcells.dem"))
                , pathFileSystem(Path.of("../net.splitcells.website.content.default/src/main/xsl/net/splitcells/website/den/translation/to/html/"))
                , pathFileSystem(Path.of("../net.splitcells.website.content.default/src/main/resources/html"))
                , "/net/splitcells/dem"
                , a -> Optional.empty()
                , Config.create());
        assertThat(testSubject.projectPaths()).contains(Path.of("net/splitcells/dem/history.html"));
    }
}
