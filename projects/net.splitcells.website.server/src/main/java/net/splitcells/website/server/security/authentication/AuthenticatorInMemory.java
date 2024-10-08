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

import net.splitcells.dem.data.set.map.Map;

import java.util.function.Function;

public class AuthenticatorInMemory implements Authenticator {
    public static Authenticator authenticatorInMemory(Function<Login, User> userLogin) {
        return new AuthenticatorInMemory(userLogin);
    }

    public static Authenticator authenticatorInMemory(Map<Login, User> userLogin) {
        return new AuthenticatorInMemory(login -> userLogin.getOrDefault(login, User.INVALID_LOGIN));
    }

    private final Function<Login, User> userQuery;

    private AuthenticatorInMemory(Function<Login, User> argUserQuery) {
        userQuery = argUserQuery;
    }

    @Override
    public User userByLogin(Login login) {
        return userQuery.apply(login);
    }
}
