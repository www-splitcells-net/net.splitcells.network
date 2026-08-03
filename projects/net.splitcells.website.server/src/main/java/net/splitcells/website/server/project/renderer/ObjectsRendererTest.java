/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.website.server.project.renderer;

import lombok.val;
import net.splitcells.dem.data.set.list.ListView;
import net.splitcells.dem.testing.annotations.UnitTest;

import java.nio.file.Path;
import java.util.Optional;

import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.testing.Assertions.requireEquals;
import static net.splitcells.website.server.project.renderer.ObjectsRendererI.objectsRenderer;

public class ObjectsRendererTest {
    @UnitTest public void testSubject() {
        val objectsRenderer = objectsRenderer(Path.of("./98"));
        val subject = new Object();
        val testRenderer = new DiscoverableRenderer() {

            @Override public ListView<String> path() {
                return list("a", "0", "g");
            }

            @Override public String render() {
                return "";
            }

            @Override public Optional<String> title() {
                return Optional.empty();
            }
        };
        objectsRenderer.withObject(testRenderer, Optional.of(subject));
        requireEquals(objectsRenderer.publicLinkOfSubject(subject), "http://localhost:8443/./98/a/0/g");
    }
}
