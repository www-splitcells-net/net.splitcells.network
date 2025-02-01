/*
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.website.server.project.renderer;

import net.splitcells.dem.environment.config.framework.OptionImpl;

import java.nio.file.Path;

import static net.splitcells.dem.Dem.configValue;
import static net.splitcells.website.server.project.renderer.ObjectsRendererI.objectsRenderer;

public class ObjectsRenderer extends OptionImpl<ObjectsRendererI> {
    public ObjectsRenderer() {
        super(() -> objectsRenderer(Path.of("/")));
    }

    public static void registerObject(DiscoverableRenderer object) {
        configValue(ObjectsRenderer.class).withObject(object);
    }

    public static void registerObject(CsvRenderer object) {
        configValue(ObjectsRenderer.class).withObject(object);
    }
}
