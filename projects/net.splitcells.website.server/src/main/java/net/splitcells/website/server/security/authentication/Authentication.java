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

import net.splitcells.dem.Dem;
import net.splitcells.dem.environment.config.framework.Option;

import static net.splitcells.dem.Dem.configValue;
import static net.splitcells.dem.lang.tree.TreeI.tree;
import static net.splitcells.dem.utils.ExecutionException.executionException;
import static net.splitcells.website.server.security.authentication.AuthenticatorImpl.authenticator;
import static net.splitcells.website.server.security.authentication.UserSession.ANONYMOUS_USER_SESSION;

/**
 * <p>Any used {@link Authenticator}, has to be thread safe, when a multi thread {@link Dem#process(Runnable)} is used.</p>
 * <p>Keep in mind, the responsibility for the security of such a system is the responsibility of the implementation.
 * Depending on the security requirements, one has to implement features not mentioned or unrelated to this API.
 * For instance, one can ensure that {@link Authenticator#userSession(Login)} always takes the same amount of time.
 * So, keep in mind, that your security requirements should match this {@link Option} value and
 * that this is your responsibility.</p>
 */
public class Authentication implements Option<Authenticator> {

    @Override
    public Authenticator defaultValue() {
        return authenticator((login, authenticator) -> ANONYMOUS_USER_SESSION);
    }

    public static UserSession requireValid(UserSession userSession) {
        if (!configValue(Authentication.class).isValid(userSession)) {
            throw executionException(tree("Invalid user session was found")
                    .withProperty("user session", userSession.toString()));
        }
        return userSession;
    }
}
