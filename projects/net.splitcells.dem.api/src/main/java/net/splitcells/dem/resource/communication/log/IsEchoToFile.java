/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.dem.resource.communication.log;

import net.splitcells.dem.environment.config.framework.OptionImpl;
import net.splitcells.dem.lang.tree.Tree;

import java.util.Optional;

import static net.splitcells.dem.lang.tree.TreeI.tree;

/**
 * TODO RENAME This {@link net.splitcells.dem.environment.config.framework.Option}
 * main function is to enable shell persistence.
 * The kind of persistence is not important here.
 */
public class IsEchoToFile extends OptionImpl<Boolean> {

	public IsEchoToFile() {
		super(() -> false);
	}

    @Override public Optional<Tree> serialize(Boolean currentValue) {
        return Optional.of(tree("" + currentValue));
    }
}
