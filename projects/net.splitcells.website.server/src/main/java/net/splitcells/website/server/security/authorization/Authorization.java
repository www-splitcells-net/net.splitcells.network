/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.website.server.security.authorization;

import net.splitcells.dem.environment.config.framework.Option;
import net.splitcells.dem.lang.tree.Tree;
import net.splitcells.website.server.security.authentication.Authenticator;
import net.splitcells.website.server.security.authentication.UserSession;

import java.util.Optional;

import static net.splitcells.dem.Dem.configValue;
import static net.splitcells.website.server.security.authentication.Authentication.requireValid;
import static net.splitcells.website.server.security.authorization.AnonymousRole.ANONYMOUS_ROLE;
import static net.splitcells.website.server.security.authorization.AuthorizerInMemory.authorizerInMemory;

public class Authorization implements Option<Authorizer> {
    @Override public Authorizer defaultValue() {
        return authorizerInMemory((user, role) -> ANONYMOUS_ROLE.equals(role));
    }

    public static boolean hasRole(UserSession userSession, Role role) {
        return configValue(Authorization.class).hasRole(requireValid(userSession), role);
    }
    
    public static boolean missesRole(UserSession userSession, Role role) {
        return !configValue(Authorization.class).hasRole(requireValid(userSession), role);
    }

    @Override public Optional<Tree> serialize(Authorizer currentValue) {
        return Optional.empty();
    }
}
