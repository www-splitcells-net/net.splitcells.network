/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.dem.environment.config;

import net.splitcells.dem.Dem;
import net.splitcells.dem.data.set.list.Lists;
import net.splitcells.dem.environment.config.framework.Option;
import net.splitcells.dem.lang.tree.Tree;
import net.splitcells.dem.object.Discoverable;

import java.util.Optional;

import static net.splitcells.dem.lang.tree.TreeI.tree;
import static net.splitcells.dem.object.Discoverable.discoverable;
import static net.splitcells.dem.utils.reflection.ClassesRelated.simplifiedName;

/**
 * Represents the root path of the currently running application.
 * This is used,
 * in order to create a tree containing all {@link Discoverable} objects of the currently running program and
 * thereby provide an easy data structure to observe the program's major objects.
 * This is intended to be easily integrated into the web server or filesystem.
 */
public class ProgramsDiscoveryPath implements Option<Discoverable> {
    @Override public Discoverable defaultValue() {
        return discoverable(Dem.environment().config().configValue(ProgramRepresentative.class));
    }

    @Override public Optional<Tree> serialize(Discoverable currentValue) {
        return Optional.of(tree(currentValue.path().unixPathString()));
    }
}
