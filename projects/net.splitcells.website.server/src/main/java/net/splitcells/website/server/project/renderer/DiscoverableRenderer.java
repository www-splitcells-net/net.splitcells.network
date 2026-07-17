/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.website.server.project.renderer;

import net.splitcells.dem.object.Discoverable;

import java.util.Optional;

/**
 * TODO IDEA Render private attributes of {@link DiscoverableRenderer} for debugging.
 * For this paths would have to be automatically generated for each private attribute.
 */
public interface DiscoverableRenderer extends Discoverable {
    /**
     *
     * @return This returns an HTML string, that can be embedded into another HTML document.
     */
    String render();

    Optional<String> title();
}
