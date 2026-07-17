/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.website.server.config;

import net.splitcells.dem.environment.config.framework.Option;
import net.splitcells.dem.lang.tree.Tree;

import java.util.Optional;

import static net.splitcells.dem.lang.tree.TreeI.tree;

/**
 * Defines the default port for HTTP.
 * Using 80 is not desirable,
 * as port 80 sometimes requires additional rights as it is in the Linux's privileged port range 1-1023.
 * If this is the case, no real error message is created sometimes and can eat up a lot of time.
 */
public class HttpPort implements Option<Integer> {
    @Override public Integer defaultValue() {
        return 8080;
    }
    @Override public Optional<Tree> serialize(Integer currentValue) {
        return Optional.of(tree("" + currentValue));
    }
}
