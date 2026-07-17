/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.dem.resource.host;

import net.splitcells.dem.Dem;
import net.splitcells.dem.environment.config.framework.OptionImpl;
import net.splitcells.dem.lang.tree.Tree;
import net.splitcells.dem.resource.Files;

import java.nio.file.Path;
import java.util.Optional;

import static net.splitcells.dem.lang.tree.TreeI.tree;

/**
 * <p>This path points to the folder.
 * The Folder contains the files of the process created by {@link Dem#process}.</p>
 */
public class ProcessPath extends OptionImpl<Path> {
    public ProcessPath() {
        super(() -> Files.usersStateFiles());
    }
    @Override public Optional<Tree> serialize(Path currentValue) {
        return Optional.of(tree("This value is host specific."));
    }
}