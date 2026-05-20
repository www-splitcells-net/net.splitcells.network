/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.gel.ui.editor.geal;

import net.splitcells.dem.environment.config.framework.Option;
import net.splitcells.dem.lang.tree.Tree;
import net.splitcells.dem.resource.FileSystemView;
import net.splitcells.gel.editor.Editor;
import net.splitcells.website.server.security.access.AccessContainer;

import java.util.Optional;

import static net.splitcells.dem.lang.tree.TreeI.tree;
import static net.splitcells.website.server.security.access.AccessContainer.accessContainer;

public class EditorAccess implements Option<AccessContainer<Editor>> {
    @Override public AccessContainer<Editor> defaultValue() {
        return accessContainer();
    }

    @Override public Optional<Tree> serialize(AccessContainer<Editor> currentValue) {
        return Optional.empty();
    }
}
