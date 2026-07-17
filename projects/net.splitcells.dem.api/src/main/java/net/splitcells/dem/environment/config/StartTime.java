/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.dem.environment.config;

import net.splitcells.dem.environment.config.framework.OptionImpl;
import net.splitcells.dem.lang.tree.Tree;

import java.time.ZonedDateTime;
import java.util.Optional;

import static net.splitcells.dem.lang.tree.TreeI.tree;

public class StartTime extends OptionImpl<ZonedDateTime> {

    public StartTime() {
        super(() -> ZonedDateTime.now());
    }

    @Override public Optional<Tree> serialize(ZonedDateTime currentValue) {
        return Optional.of(tree(currentValue.toString()));
    }
}
