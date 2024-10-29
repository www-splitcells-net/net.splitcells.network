/*
 * Copyright (c) 2021 Contributors To The `net.splitcells.*` Projects
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the Eclipse
 * Public License, v. 2.0 are satisfied: GNU General Public License v2.0 or later
 * which is available at https://www.gnu.org/licenses/old-licenses/gpl-2.0-standalone.html
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.website.server.security.authorization;

import java.util.regex.Pattern;

import static net.splitcells.dem.lang.tree.TreeI.tree;
import static net.splitcells.dem.utils.ExecutionException.executionException;
import static net.splitcells.website.server.security.authentication.Authenticator.VALID_USERNAME_SYMBOLS;

public class Role {
    public static final String ANONYMOUS_ROLE_NAME = "anonymous";
    public static final Role ANONYMOUS_ROLE = role(ANONYMOUS_ROLE_NAME);
    public static final String ADMIN_ROLE_NAME = "admin-role";
    public static final Role ADMIN_ROLE = role(ADMIN_ROLE_NAME);

    public static Role role(String name) {
        return new Role(name);
    }

    public static void requireValid(Role role) {
        if (!VALID_USERNAME_SYMBOLS.matcher(role.name()).matches()) {
            throw executionException(tree("The given role name is invalid.")
                    .withProperty("role name", role.name()));
        }
    }

    private final String name;

    private Role(String argName) {
        if (!VALID_USERNAME_SYMBOLS.matcher(argName).matches()) {
            throw executionException(tree("The given role name is invalid.")
                    .withProperty("role name", argName));
        }
        name = argName;
    }

    public String name() {
        return name;
    }
}
