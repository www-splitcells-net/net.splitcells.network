/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.website.server.security.authorization;

import net.splitcells.dem.utils.ExecutionException;

import static net.splitcells.dem.lang.tree.TreeI.tree;
import static net.splitcells.dem.utils.ExecutionException.execException;
import static net.splitcells.website.server.security.authentication.Authenticator.VALID_USERNAME_SYMBOLS;

public class Roles implements Role {

    public static Role role(String name) {
        return new Roles(name);
    }

    private final String name;

    private Roles(String argName) {
        if (!VALID_USERNAME_SYMBOLS.matcher(argName).matches()) {
            throw ExecutionException.execException(tree("The given role name is invalid.")
                    .withProperty("role name", argName));
        }
        name = argName;
    }

    @Override
    public String name() {
        return name;
    }
}
