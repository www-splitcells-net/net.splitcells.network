/*
 * SPDX-License-Identifier: EPL-2.0 OR MIT
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.website.server.project.renderer;

import net.splitcells.dem.environment.config.framework.OptionI;

import java.nio.file.Path;
import java.util.function.Supplier;

import static net.splitcells.dem.Dem.configValue;
import static net.splitcells.website.server.project.renderer.ObjectsRendererI.objectsRenderer;

public class ObjectsRenderer extends OptionI<ObjectsRendererI> {
    public ObjectsRenderer() {
        super(() -> objectsRenderer(Path.of("/net/splitcells/run/")));
    }

    public static void registerObject(DiscoverableRenderer object) {
        configValue(ObjectsRenderer.class).withObject(object);
    }
}
