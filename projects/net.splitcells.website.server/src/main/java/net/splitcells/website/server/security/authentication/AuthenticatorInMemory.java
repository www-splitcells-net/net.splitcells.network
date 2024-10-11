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
    public static Authenticator authenticatorInMemory(Function<BasicLogin, UserSession> userLogin) {
        return new AuthenticatorInMemory(userLogin);
    }

    public static Authenticator authenticatorInMemory(Map<BasicLogin, UserSession> userLogin) {
        return new AuthenticatorInMemory(basicLogin -> userLogin.getOrDefault(basicLogin, UserSession.INSECURE_USER_SESSION));
    }

    private final Function<BasicLogin, UserSession> userQuery;
    private final Set<UserSession> validUserSessions = setOfUniques();

    private AuthenticatorInMemory(Function<BasicLogin, UserSession> argUserQuery) {
        userQuery = argUserQuery;
    }

    @Override
    public UserSession userSession(BasicLogin basicLogin) {
        final var user = userQuery.apply(basicLogin);
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
