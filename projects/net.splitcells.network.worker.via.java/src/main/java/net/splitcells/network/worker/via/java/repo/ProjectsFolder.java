/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.network.worker.via.java.repo;

import net.splitcells.dem.environment.config.framework.OptionImpl;
import net.splitcells.dem.lang.tree.Tree;
import net.splitcells.dem.resource.FileSystemView;

import java.nio.file.Path;
import java.util.Optional;

import static net.splitcells.dem.lang.tree.TreeI.tree;
import static net.splitcells.dem.resource.Paths.userHome;

public class ProjectsFolder extends OptionImpl<Path> {
    public ProjectsFolder() {
        super(() -> userHome("Documents/projects/net.splitcells.martins.avots.support.system/public/"));
    }
    @Override public Optional<Tree> serialize(Path currentValue) {
        return Optional.of(tree(currentValue.toString()));
    }
}
