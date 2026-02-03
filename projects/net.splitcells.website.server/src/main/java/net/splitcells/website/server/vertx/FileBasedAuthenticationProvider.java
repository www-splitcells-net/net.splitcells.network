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
package net.splitcells.website.server.vertx;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.User;
import io.vertx.ext.auth.authentication.AuthenticationProvider;
import net.splitcells.dem.lang.annotations.JavaLegacy;
import net.splitcells.website.server.security.authentication.Authentication;
import net.splitcells.website.server.security.authentication.Authenticator;
import net.splitcells.website.server.security.authentication.Login;

import static net.splitcells.dem.Dem.configValue;
import static net.splitcells.website.server.security.authentication.UserSession.ANONYMOUS_USER_SESSION;
import static net.splitcells.website.server.security.authentication.UserSession.INSECURE_USER_SESSION;

@JavaLegacy
public class FileBasedAuthenticationProvider implements AuthenticationProvider {
    public static final String LOGIN_KEY = FileBasedAuthenticationProvider.class.getName() + ".login.key";

    public static FileBasedAuthenticationProvider fileBasedAuthenticationProvider() {
        return new FileBasedAuthenticationProvider();
    }

    private final Authenticator authenticator = configValue(Authentication.class);

    private FileBasedAuthenticationProvider() {
    }

    /**
     * This method will never log the password entered by the user or the actual password,
     * in order to avoid security problems.
     *
     * @param credentials   The credentials
     * @param resultHandler The result handler
     */
    @Override
    public void authenticate(JsonObject credentials, Handler<AsyncResult<User>> resultHandler) {
        final var username = credentials.getString("username");
        final var inputtedPassword = credentials.getString("password");
        final var userSession = authenticator.userSession(Login.login(username, inputtedPassword));
        if (INSECURE_USER_SESSION.equals(userSession)) {
            resultHandler.handle(Future.failedFuture("The password for `"
                    + username
                    + "` is unknown."));
            return;
        } else if (ANONYMOUS_USER_SESSION.equals(userSession)) {
            resultHandler.handle(Future.failedFuture("The username `"
                    + username
                    + "` is unknown."));
            return;
        }
        final var user = User.fromName(username);
        user.attributes().put(LOGIN_KEY, userSession);
        resultHandler.handle(Future.succeededFuture(user));
    }
}
