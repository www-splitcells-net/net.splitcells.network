/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.dem.environment.config;

import net.splitcells.dem.data.atom.Bool;
import net.splitcells.dem.environment.config.framework.OptionImpl;
import net.splitcells.dem.lang.tree.Tree;

import java.util.Optional;

import static net.splitcells.dem.lang.tree.TreeI.tree;

public class IsDeterministic extends OptionImpl<Optional<Bool>> {

    /**
     * Programs are not required to deterministic by default, because performance is more important by default.
     */
    public IsDeterministic() {
        super(() -> Optional.empty());
    }

    @Override public Optional<Tree> serialize(Optional<Bool> currentValue) {
        return Optional.of(tree("" + currentValue));
    }
}
