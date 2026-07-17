/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.dem.environment.config;

import net.splitcells.dem.Dem;
import net.splitcells.dem.data.atom.Bool;
import net.splitcells.dem.environment.config.framework.OptionImpl;
import net.splitcells.dem.lang.tree.Tree;

import java.time.format.DateTimeFormatter;
import java.util.Optional;

import static net.splitcells.dem.lang.tree.TreeI.tree;

public class ProgramLocalIdentity extends OptionImpl<String> {

    public ProgramLocalIdentity() {
        super(() -> Dem.environment().config().configValue(ProgramName.class)
                + Dem.environment().config().configValue(StartTime.class).format(DateTimeFormatter.ofPattern("yyyy.MM.dd.HH.m.s.n"))
        );
    }

    @Override public Optional<Tree> serialize(String currentValue) {
        return Optional.of(tree(currentValue));
    }
}
