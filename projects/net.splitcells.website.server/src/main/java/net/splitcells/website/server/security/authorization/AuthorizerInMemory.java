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

import net.splitcells.dem.data.set.Set;
import net.splitcells.dem.data.set.map.Map;
import net.splitcells.website.server.security.authentication.UserSession;

import java.util.function.BiFunction;

public class AuthorizerInMemory implements Authorizer {
    public static Authorizer authorizerInMemory(Map<UserSession, Set<Role>> userRoleMapping) {
        return new AuthorizerInMemory((user, role) -> userRoleMapping.getOptionally(user)
                .map(roles -> roles.has(role))
                .orElse(false));
    }

    public static Authorizer authorizerInMemory(BiFunction<UserSession, Role, Boolean> userRoleMapping) {
        return new AuthorizerInMemory(userRoleMapping);
    }

    private final BiFunction<UserSession, Role, Boolean> userRoleMapping;

    private AuthorizerInMemory(BiFunction<UserSession, Role, Boolean> userRoleMappingArg) {
        userRoleMapping = userRoleMappingArg;
    }

    @Override
    public synchronized boolean hasRole(UserSession userSession, Role role) {
        return userRoleMapping.apply(userSession, role);
    }
}
