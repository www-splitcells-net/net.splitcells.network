/*
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.website.server.project.renderer;

import net.splitcells.dem.environment.config.framework.Option;

import static net.splitcells.dem.Dem.configValue;
import static net.splitcells.website.server.project.renderer.DiscoveryRendererImpl.discoveryRendererImpl;

public class DiscoveryRenderer implements Option<DiscoveryRendererImpl> {
    @Override
    public DiscoveryRendererImpl defaultValue() {
        return discoveryRendererImpl();
    }

    public static void registerObject(DiscoverableRenderer object) {
        configValue(DiscoveryRenderer.class).withObject(object);
    }
}
