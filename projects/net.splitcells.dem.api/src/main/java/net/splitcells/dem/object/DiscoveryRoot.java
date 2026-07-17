/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.dem.object;

import net.splitcells.dem.environment.config.framework.Option;
import net.splitcells.dem.lang.tree.Tree;

import java.util.Optional;

import static net.splitcells.dem.lang.tree.TreeI.tree;
import static net.splitcells.dem.object.Discoveries.discoveryRoot;

public class DiscoveryRoot implements Option<Discovery> {
    @Override
    public Discovery defaultValue() {
        return discoveryRoot();
    }

    @Override public Optional<Tree> serialize(Discovery currentValue) {
        return Optional.of(tree(currentValue.path().unixPathString()));
    }
}
