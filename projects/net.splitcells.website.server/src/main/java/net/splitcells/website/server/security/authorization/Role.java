/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.website.server.security.authorization;

import net.splitcells.dem.utils.ExecutionException;

import static net.splitcells.dem.lang.tree.TreeI.tree;
import static net.splitcells.dem.utils.ExecutionException.execException;
import static net.splitcells.website.server.security.authentication.Authenticator.VALID_USERNAME_SYMBOLS;

/**
 * Prefer defining new instances via {@link StaticRole},
 * in order to avoid ambiguous {@link Role}.
 */
public interface Role {

    static void requireValid(Role role) {
        if (!VALID_USERNAME_SYMBOLS.matcher(role.name()).matches()) {
            throw ExecutionException.execException(tree("The given role name is invalid.")
                    .withProperty("role name", role.name()));
        }
    }

    String name();
}
