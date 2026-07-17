/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.website;

import net.splitcells.dem.environment.config.framework.OptionImpl;
import net.splitcells.dem.lang.tree.Tree;
import net.splitcells.dem.resource.FileSystemView;

import java.util.Optional;

import static net.splitcells.dem.lang.tree.TreeI.tree;

public class RenderUserStateRepo extends OptionImpl<Boolean> {
    public RenderUserStateRepo() {
        super(() -> false);
    }
    @Override public Optional<Tree> serialize(Boolean currentValue) {
        return Optional.of(tree("" + currentValue));
    }
}
