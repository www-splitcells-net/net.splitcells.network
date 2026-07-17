/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.dem.resource.host;

import net.splitcells.dem.environment.config.framework.Option;
import net.splitcells.dem.lang.tree.Tree;
import net.splitcells.dem.resource.FileSystem;
import net.splitcells.dem.resource.Paths;

import java.util.Optional;

import static net.splitcells.dem.lang.tree.TreeI.tree;
import static net.splitcells.dem.resource.PathFileSystem.pathFileSystem;

public class UserHomeFileSystem implements Option<FileSystem> {
    @Override public FileSystem defaultValue() {
        return pathFileSystem(Paths.userHome());
    }
    @Override public Optional<Tree> serialize(FileSystem currentValue) {
        return Optional.of(tree(currentValue.toString()));
    }
}
