/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.dem.resource;

import net.splitcells.dem.environment.config.framework.OptionImpl;
import net.splitcells.dem.lang.tree.Tree;

import java.util.Optional;

import static net.splitcells.dem.Dem.configValue;
import static net.splitcells.dem.lang.tree.TreeI.tree;
import static net.splitcells.dem.resource.FileSystemViaClassResourcesFactoryImpl._fileSystemViaClassResourcesFactoryImpl;

public class FileSystemViaClassResources extends OptionImpl<FileSystemViaClassResourcesFactoryApi> {
    public FileSystemViaClassResources() {
        super(() -> _fileSystemViaClassResourcesFactoryImpl());
    }

    public static FileSystemView fileSystemViaClassResources(Class<?> clazz, String groupId, String artifactId) {
        return configValue(FileSystemViaClassResources.class).fileSystemViaClassResources(clazz, groupId, artifactId);
    }
    @Override public Optional<Tree> serialize(FileSystemViaClassResourcesFactoryApi currentValue) {
        return Optional.empty();
    }
}
