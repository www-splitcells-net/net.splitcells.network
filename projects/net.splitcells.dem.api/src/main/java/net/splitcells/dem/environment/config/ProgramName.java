/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.dem.environment.config;

import net.splitcells.dem.Dem;
import net.splitcells.dem.environment.config.framework.Option;
import net.splitcells.dem.environment.config.framework.OptionImpl;
import net.splitcells.dem.lang.tree.Tree;

import java.util.Optional;

import static net.splitcells.dem.lang.tree.TreeI.tree;
import static net.splitcells.dem.utils.reflection.ClassesRelated.simplifiedName;

public class ProgramName implements Option<String> {

    @Override public String defaultValue() {
        return simplifiedName(Dem.environment().config().configValue(ProgramRepresentative.class));
    }

    @Override public Optional<Tree> serialize(String currentValue) {
        return Optional.of(tree(currentValue));
    }
}
