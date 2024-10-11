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
import net.splitcells.dem.lang.annotations.JavaLegacyArtifact;
import net.splitcells.dem.resource.ConfigFileSystem;
import net.splitcells.dem.resource.FileSystemView;

import static net.splitcells.dem.Dem.configValue;

@JavaLegacyArtifact
public class FileBasedAuthenticationProvider implements AuthenticationProvider {
    private static final String USER_FOLDER = "net/splitcells/website/server/security/users/";
    public static final String PASSWORD_FILE = "/password";

    public static FileBasedAuthenticationProvider fileBasedAuthenticationProvider() {
        return new FileBasedAuthenticationProvider(configValue(ConfigFileSystem.class)
                .subFileSystem(USER_FOLDER));
    }

    public static FileBasedAuthenticationProvider fileBasedAuthenticationProvider(FileSystemView userData) {
        return new FileBasedAuthenticationProvider(userData);
    }

    private final FileSystemView userData;

    private FileBasedAuthenticationProvider(FileSystemView userDataArg) {
        userData = userDataArg;
    }

    @Override
    public void authenticate(JsonObject credentials, Handler<AsyncResult<User>> resultHandler) {
        final var username = credentials.getString("username");
        final var inputtedPassword = credentials.getString("password");
        if (!userData.isFile(username + PASSWORD_FILE)) {
            resultHandler.handle(Future.failedFuture("The username `"
                    + username
                    + "` is unknown."));
            return;
        }
        final var storedPassword = userData.readString(username + PASSWORD_FILE).split("\n")[0];
        if (!inputtedPassword.equals(storedPassword)) {
            resultHandler.handle(Future.failedFuture("False input password `"
                    + inputtedPassword
                    + "` for username `"
                    + username
                    + "`. Expecting the password `"
                    + storedPassword
                    + "`."));
        }
        resultHandler.handle(Future.succeededFuture(User.fromName(username)));
    }
}
