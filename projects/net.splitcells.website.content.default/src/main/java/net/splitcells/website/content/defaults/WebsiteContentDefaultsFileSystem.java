/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.website.content.defaults;

import net.splitcells.dem.environment.config.framework.OptionImpl;
import net.splitcells.dem.lang.tree.Tree;
import net.splitcells.dem.resource.FileSystemView;

import java.util.Optional;

import static net.splitcells.dem.lang.tree.TreeI.tree;
import static net.splitcells.dem.resource.FileSystemViaClassResources.fileSystemViaClassResources;

public class WebsiteContentDefaultsFileSystem extends OptionImpl<FileSystemView> {
    public WebsiteContentDefaultsFileSystem() {
        super(() -> fileSystemViaClassResources(WebsiteContentDefaultsFileSystem.class, "net.splitcells", "website.content.default"));
    }
    @Override public Optional<Tree> serialize(FileSystemView currentValue) {
        return Optional.of(tree(currentValue.toString()));
    }
}
