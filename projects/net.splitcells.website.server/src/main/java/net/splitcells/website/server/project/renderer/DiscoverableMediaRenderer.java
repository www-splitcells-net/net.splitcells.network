/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.website.server.project.renderer;

import net.splitcells.dem.object.Discoverable;
import net.splitcells.website.server.Config;
import net.splitcells.website.server.project.ProjectRenderer;
import net.splitcells.website.server.processor.BinaryMessage;

import java.util.Optional;

public interface DiscoverableMediaRenderer extends Discoverable {
    Optional<BinaryMessage> render(ProjectRenderer projectRenderer, Config config);
}
