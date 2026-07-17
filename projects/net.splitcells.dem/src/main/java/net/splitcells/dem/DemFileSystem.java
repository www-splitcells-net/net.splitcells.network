/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.dem;

import net.splitcells.dem.environment.config.framework.OptionImpl;
import net.splitcells.dem.lang.tree.Tree;
import net.splitcells.dem.resource.FileSystemView;

import java.util.Optional;

import static net.splitcells.dem.lang.tree.TreeI.tree;
import static net.splitcells.dem.resource.FileSystemViaClassResources.fileSystemViaClassResources;

public class DemFileSystem extends OptionImpl<FileSystemView> {
    public DemFileSystem() {
        super(() -> fileSystemViaClassResources(DemFileSystem.class, "net.splitcells", "dem"));
    }
    @Override public Optional<Tree> serialize(FileSystemView currentValue) {
        return Optional.of(tree(currentValue.toString()));
    }
}
