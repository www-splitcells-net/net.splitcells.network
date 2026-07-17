/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.website.server;

import net.splitcells.dem.environment.config.framework.Option;
import net.splitcells.dem.lang.tree.Tree;
import net.splitcells.dem.object.Discoverable;

import java.util.Optional;

import static net.splitcells.dem.lang.tree.TreeI.tree;

public class ServerConfig implements Option<Config> {
    @Override public Config defaultValue() {
        return Config.create();
    }

    @Override public Optional<Tree> serialize(Config currentValue) {
        return Optional.of(currentValue.serialize());
    }
}
