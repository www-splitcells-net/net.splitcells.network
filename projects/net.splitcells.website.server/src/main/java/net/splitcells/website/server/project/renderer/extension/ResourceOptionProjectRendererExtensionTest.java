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

public class ResourceOptionProjectRendererExtensionTest {
    /**
     * TODO Test extension explicitly.
     */
    @Tag(INTEGRATION_TEST)
    @Test
    public void testResourceLayout() {
        final var testSubject = projectRenderer
                ("public"
                        , pathFileSystem(Path.of("."))
                        , pathFileSystem(Path.of("../net.splitcells.website.content.default/src/main/xsl/net/splitcells/website/den/translation/to/html/"))
                        , pathFileSystem(Path.of("../net.splitcells.website.content.default/src/main/resources/html"))
                        , "/net/splitcells/network"
                        , a -> Optional.empty()
                        , Config.create());
        testSubject.projectPaths().requirePresenceOf(Path.of("test-resource.html"));
    }
}
