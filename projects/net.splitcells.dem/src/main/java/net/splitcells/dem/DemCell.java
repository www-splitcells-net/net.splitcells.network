/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.dem;

import net.splitcells.dem.environment.Cell;
import net.splitcells.dem.environment.Environment;
import net.splitcells.dem.lang.tree.Tree;

import java.util.Optional;
import java.util.function.Consumer;

public class DemCell implements Cell {
    @Override
    public String groupId() {
        return "net.splitcells";
    }

    @Override
    public String artifactId() {
        return "dem";
    }

    @Override
    public void accept(Environment env) {
        env.config().withInitedOption(DemFileSystem.class);
    }
}
