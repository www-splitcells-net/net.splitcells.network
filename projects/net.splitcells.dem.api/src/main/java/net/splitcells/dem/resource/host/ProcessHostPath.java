/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.dem.resource.host;

import net.splitcells.dem.environment.config.framework.OptionImpl;
import net.splitcells.dem.lang.tree.Tree;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import static net.splitcells.dem.lang.tree.TreeI.tree;

public class ProcessHostPath extends OptionImpl<Path> {
    public ProcessHostPath() {
        super(() -> {
            if ("true".equals(System.getProperty("net.splitcells.mode.build"))) {
                /**
                 * This prevents from files being created at the project's root folder,
                 * when tests are executed via maven.
                 * Thereby, no test files are committed by accident.
                 */
                return Paths.get("target");
            } else {
                return Paths.get(".");
            }
        });
    }

    @Override public Optional<Tree> serialize(Path currentValue) {
        return Optional.of(tree(currentValue.toString()));
    }
}
