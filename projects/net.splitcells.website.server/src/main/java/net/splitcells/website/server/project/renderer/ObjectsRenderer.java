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
