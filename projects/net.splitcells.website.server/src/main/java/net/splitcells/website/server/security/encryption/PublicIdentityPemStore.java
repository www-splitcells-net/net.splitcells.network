/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.website.server.security.encryption;

import net.splitcells.dem.environment.config.framework.Option;
import net.splitcells.dem.lang.tree.Tree;
import net.splitcells.dem.utils.StringUtils;

import java.util.Optional;

import static net.splitcells.dem.lang.tree.TreeI.tree;

public class PublicIdentityPemStore implements Option<Optional<byte[]>> {
    @Override public Optional<byte[]> defaultValue() {
        return Optional.empty();
    }
    @Override public Optional<Tree> serialize(Optional<byte[]> currentValue) {
        return Optional.of(tree("This value is host specific."));
    }
}
