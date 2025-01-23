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
