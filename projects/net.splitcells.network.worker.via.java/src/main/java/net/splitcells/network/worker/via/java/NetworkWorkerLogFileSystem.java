/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.network.worker.via.java;

import net.splitcells.dem.environment.config.framework.Option;
import net.splitcells.dem.lang.tree.Tree;
import net.splitcells.dem.resource.FileSystem;
import net.splitcells.dem.resource.FileSystemView;

import java.util.Optional;

import static net.splitcells.dem.lang.tree.TreeI.tree;
import static net.splitcells.dem.resource.FileSystemVoid.fileSystemVoid;

public class NetworkWorkerLogFileSystem implements Option<FileSystem> {
    @Override public FileSystem defaultValue() {
        return fileSystemVoid();
    }

    @Override public Optional<Tree> serialize(FileSystem currentValue) {
        return Optional.of(tree(currentValue.toString()));
    }
}
