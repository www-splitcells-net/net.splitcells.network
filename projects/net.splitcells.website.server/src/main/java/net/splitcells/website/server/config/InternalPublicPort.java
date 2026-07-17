/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.website.server.config;

import net.splitcells.dem.environment.config.framework.Option;
import net.splitcells.dem.lang.tree.Tree;

import java.util.Optional;

import static net.splitcells.dem.lang.tree.TreeI.tree;

public class InternalPublicPort implements Option<Optional<Integer>> {
    @Override public Optional<Integer> defaultValue() {
        return Optional.empty();
    }

    @Override public Optional<Tree> serialize(Optional<Integer> currentValue) {
        return Optional.of(tree("" + currentValue));
    }
}
