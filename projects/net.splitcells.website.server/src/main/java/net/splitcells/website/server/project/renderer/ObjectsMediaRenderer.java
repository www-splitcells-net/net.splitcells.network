/*
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.website.server.project.renderer;

import net.splitcells.dem.environment.config.framework.OptionImpl;

import java.nio.file.Path;

import static net.splitcells.dem.Dem.configValue;
import static net.splitcells.website.server.project.renderer.ObjectsMediaRendererI.objectsMediaRenderer;

public class ObjectsMediaRenderer extends OptionImpl<ObjectsMediaRendererI> {
    public ObjectsMediaRenderer() {
        super(() -> objectsMediaRenderer(Path.of("/")));
    }

    public static void registerMediaObject(DiscoverableMediaRenderer object) {
        configValue(ObjectsMediaRenderer.class).withMediaObject(object);
    }
}
