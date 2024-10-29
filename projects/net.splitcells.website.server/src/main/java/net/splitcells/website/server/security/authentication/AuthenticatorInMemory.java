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
import static net.splitcells.dem.data.set.map.Maps.map;
import static net.splitcells.website.server.security.authentication.UserSession.isValidNoLoginStandard;
import static net.splitcells.website.server.security.authentication.UserSession.noLoginUserId;

public class AuthenticatorInMemory implements Authenticator {
    public static Authenticator authenticatorInMemory(Function<Login, UserSession> userLogin) {
        return new AuthenticatorInMemory(userLogin);
    }

    public static Authenticator authenticatorInMemory(Map<Login, UserSession> userLogin) {
        return new AuthenticatorInMemory(basicLogin -> userLogin.getOrDefault(basicLogin, UserSession.INSECURE_USER_SESSION));
    }

    private final Function<Login, UserSession> userQuery;
    private final Set<UserSession> validUserSessions = setOfUniques();
    private final Map<UserSession, String> usernames = map();

    private AuthenticatorInMemory(Function<Login, UserSession> argUserQuery) {
        userQuery = argUserQuery;
    }

    @Override
    public UserSession userSession(Login login) {
        final var userSession = userQuery.apply(login);
        validUserSessions.add(userSession);
        usernames.put(userSession, login.username());
        return userSession;
    }

    @Override
    public boolean isValid(UserSession userSession) {
        if (isValidNoLoginStandard(userSession)) {
            return true;
        }
        if (userSession.authenticatedBy().isEmpty()) {
            return false;
        }
        if (userSession.authenticatedBy().get() != this) {
            return false;
        }
        return validUserSessions.has(userSession);
    }

    @Override
    public void endSession(UserSession userSession) {
        if (isValidNoLoginStandard(userSession)) {
            return;
        }
        validUserSessions.remove(userSession);
        usernames.remove(userSession);
    }

    @Override
    public String name(UserSession userSession) {
        if (isValidNoLoginStandard(userSession)) {
            usernames.requireKeyAbsence(userSession);
            return noLoginUserId(userSession);
        }
        return usernames.get(userSession);
    }
}
