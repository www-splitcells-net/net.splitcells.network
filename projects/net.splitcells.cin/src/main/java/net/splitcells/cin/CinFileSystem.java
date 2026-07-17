/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.cin;

import net.splitcells.dem.environment.config.framework.OptionImpl;
import net.splitcells.dem.lang.tree.Tree;
import net.splitcells.dem.resource.FileSystemView;

import java.util.Optional;

import static net.splitcells.dem.lang.tree.TreeI.tree;
import static net.splitcells.dem.resource.FileSystemViaClassResources.fileSystemViaClassResources;

public class CinFileSystem extends OptionImpl<FileSystemView> {
    public CinFileSystem() {
        super(() -> fileSystemViaClassResources(CinFileSystem.class, "net.splitcells", "cin"));
    }
    @Override public Optional<Tree> serialize(FileSystemView currentValue) {
        return Optional.of(tree(currentValue.toString()));
    }
}
