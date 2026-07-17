/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.website.server.config;

import net.splitcells.dem.environment.config.framework.Option;
import net.splitcells.dem.lang.tree.Tree;

import java.util.Optional;

import static net.splitcells.dem.lang.tree.TreeI.tree;

/**
 * This server is publicly available by this DNS domain of this server.
 */
public class PublicDomain implements Option<Optional<String>> {
    @Override public Optional<String> defaultValue() {
        return Optional.empty();
    }
    @Override public Optional<Tree> serialize(Optional<String> currentValue) {
        return Optional.of(tree("" + currentValue));
    }
}
