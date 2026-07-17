/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
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
