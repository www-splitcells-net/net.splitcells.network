/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.dem.environment.config;

import net.splitcells.dem.environment.config.framework.OptionImpl;
import net.splitcells.dem.lang.tree.Tree;
import net.splitcells.dem.resource.FileSystemView;

import java.time.ZonedDateTime;
import java.util.Optional;

import static net.splitcells.dem.lang.tree.TreeI.tree;

public class EndTime extends OptionImpl<Optional<ZonedDateTime>> {

    public EndTime() {
        super(() -> Optional.empty());
    }
    @Override public Optional<Tree> serialize(Optional<ZonedDateTime> currentValue) {
        return currentValue.map(v -> tree(v.toString()));
    }
}
