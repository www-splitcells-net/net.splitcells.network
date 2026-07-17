/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.dem.resource.profiling;

import net.splitcells.dem.environment.config.framework.Option;
import net.splitcells.dem.lang.tree.Tree;
import net.splitcells.dem.resource.FileSystem;

import java.util.Optional;

import static net.splitcells.dem.lang.tree.TreeI.tree;

/**
 * Provides the address to the Pyroscope server for Java profiling.
 */
public class PyroscopeServerUrl implements Option<String> {
    @Override public String defaultValue() {
        return "http://host.docker.internal:4040";
    }
    @Override public Optional<Tree> serialize(String currentValue) {
        return Optional.of(tree(currentValue));
    }
}
