/*
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.website.server.project.renderer;

import net.splitcells.dem.testing.annotations.UnitTest;

import java.nio.file.Path;

import static net.splitcells.website.server.project.renderer.ObjectsMediaRendererI.objectsMediaRenderer;

public class ObjectsMediaRendererTest {
    @UnitTest
    public void test() {
        final var testSubject = objectsMediaRenderer(Path.of(""));
    }
}
