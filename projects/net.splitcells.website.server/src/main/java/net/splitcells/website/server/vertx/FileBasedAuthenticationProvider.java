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

/**
 * Authenticates users via their names and passwords by looking up a file for each user,
 * that contains the user's password.
 * These files are located at {@link ConfigFileSystem} + {@link #USER_PASSWORD_FOLDER} + `/<username>`.
 * The password is the first line of the file.
 * The reason for the cut at the first line ending is the fact,
 * that on Linux sometimes a line ending symbol is added to a file
 * via a text editor without the line ending being visible in the editor (source Mārtiņš Avots).
 * Furthermore, the line ending symbol can be hard to enter for a user,
 * because of the UI of the user's computer.
 */
@JavaLegacyArtifact
public class FileBasedAuthenticationProvider implements AuthenticationProvider {
    private static final String USER_PASSWORD_FOLDER = "net/splitcells/website/server/security/users/";

    public static FileBasedAuthenticationProvider fileBasedAuthenticationProvider() {
        return new FileBasedAuthenticationProvider(configValue(ConfigFileSystem.class)
                .subFileSystem(USER_PASSWORD_FOLDER));
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
        if (!userData.isFile(username)) {
            resultHandler.handle(Future.failedFuture("The username `"
                    + username
                    + "` is unknown."));
            return;
        }
        final var storedPassword = userData.readString(username).split("\n")[0];
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
