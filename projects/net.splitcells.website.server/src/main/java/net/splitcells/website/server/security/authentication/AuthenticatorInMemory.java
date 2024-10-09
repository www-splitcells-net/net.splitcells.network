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
package net.splitcells.website.server.security.authentication;

import net.splitcells.dem.data.set.Set;
import net.splitcells.dem.data.set.map.Map;

import java.util.function.Function;

import static net.splitcells.dem.data.set.Sets.setOfUniques;

public class AuthenticatorInMemory implements Authenticator {
    public static Authenticator authenticatorInMemory(Function<Login, UserSession> userLogin) {
        return new AuthenticatorInMemory(userLogin);
    }

    public static Authenticator authenticatorInMemory(Map<Login, UserSession> userLogin) {
        return new AuthenticatorInMemory(login -> userLogin.getOrDefault(login, UserSession.INVALID_LOGIN));
    }

    private final Function<Login, UserSession> userQuery;
    /**
     * TODO Currently, this is a memory leak,
     * but we do not expect many logins for now.
     * In the future {@link UserSession} will have to be some kind of user session object instead.
     * Meaning, that the user is only valid for a certain duration.
     * Such info could be added to {@link UserSession} itself and could be used to clear {@link #validUserSessions}.
     * In such case some kind of alternative user entity is required as a base for this new user session.
     * Maybe the root user entity is just a user session, with an infinite amount of time?
     * This way, a tree is formed, that represents the user and its actions.
     * On the other hand, it would be a lot easier, to just cache a limited number of valid users.
     */
    private final Set<UserSession> validUserSessions = setOfUniques();

    private AuthenticatorInMemory(Function<Login, UserSession> argUserQuery) {
        userQuery = argUserQuery;
    }

    @Override
    public UserSession userByLogin(Login login) {
        final var user = userQuery.apply(login);
        validUserSessions.add(user);
        return user;
    }

    @Override
    public boolean isValid(UserSession userSession) {
        if (userSession.authenticatedBy().isEmpty()) {
            return false;
        }
        if (userSession.authenticatedBy().get() != this) {
            return false;
        }
        return validUserSessions.has(userSession);
    }
}
