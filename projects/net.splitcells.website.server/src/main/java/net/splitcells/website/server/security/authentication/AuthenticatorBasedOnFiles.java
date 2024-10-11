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
import net.splitcells.dem.resource.ConfigFileSystem;
import net.splitcells.dem.resource.FileSystemView;
import net.splitcells.dem.testing.Assertions;

import java.util.regex.Pattern;

import static net.splitcells.dem.Dem.configValue;
import static net.splitcells.dem.data.set.Sets.setOfUniques;
import static net.splitcells.dem.lang.tree.TreeI.tree;
import static net.splitcells.dem.utils.ExecutionException.executionException;
import static net.splitcells.website.server.security.authentication.UserSession.ANONYMOUS_USER_SESSION;
import static net.splitcells.website.server.security.authentication.UserSession.INSECURE_USER_SESSION;

/**
 * <p>Authenticates users via their names and passwords by looking up a file for each user,
 * that contains the user's password.
 * These files are located at {@link ConfigFileSystem} + {@link #USER_FOLDER} + `/[username]`.
 * The password is the first line of the file.
 * The reason for the cut at the first line ending is the fact,
 * that on Linux sometimes a line ending symbol is added to a file
 * via a text editor without the line ending being visible in the editor (source Mārtiņš Avots).
 * Furthermore, the line ending symbol can be hard to enter for a user,
 * because of the UI of the user's computer.</p>
 * <p>Only usernames matching to {@link #VALID_USERNAME_SYMBOLS} are allowed.</p>
 */
public class AuthenticatorBasedOnFiles implements Authenticator {
    private static final String USER_FOLDER = "net/splitcells/website/server/security/users/";
    public static final String PASSWORD_FILE = "/password";
    private static final Pattern VALID_USERNAME_SYMBOLS = Pattern.compile("[a-zA-Z0-9 \\-]+");

    public static Authenticator authenticatorBasedOnFiles() {
        return new AuthenticatorBasedOnFiles(configValue(ConfigFileSystem.class)
                .subFileSystem(USER_FOLDER));
    }

    public static Authenticator authenticatorBasedOnFiles(FileSystemView userData) {
        return new AuthenticatorBasedOnFiles(userData);
    }

    private final FileSystemView userData;
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

    private AuthenticatorBasedOnFiles(FileSystemView argUserData) {
        userData = argUserData;
    }

    @Override
    public synchronized UserSession userSession(BasicLogin basicLogin) {
        if (!userData.isFile(basicLogin.username() + PASSWORD_FILE)) {
            return ANONYMOUS_USER_SESSION;
        }
        /* TODO HACK This was a hack, because the equals method did not work otherwise.
         * Currently, it is unknown, why some text editors like nano add a new line symbol to the end,
         * that cannot be seen.
         */
        if (!VALID_USERNAME_SYMBOLS.matcher(basicLogin.username()).matches()) {
            return INSECURE_USER_SESSION;
        }
        final var storedPassword = userData.readString(basicLogin.username() + PASSWORD_FILE).split("\n")[0];
        if (!basicLogin.password().equals(storedPassword)) {
            return INSECURE_USER_SESSION;
        }
        final var userSession = UserSession.user(this);
        validUserSessions.add(userSession);
        if (!isValid(userSession)) {
            throw executionException(tree("Could not create a valid user session, given the login.")
                    .withProperty("login data", basicLogin.toString()));
        }
        return userSession;
    }

    @Override
    public synchronized boolean isValid(UserSession userSession) {
        if (ANONYMOUS_USER_SESSION.equals(userSession) || INSECURE_USER_SESSION.equals(userSession)) {
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
}
