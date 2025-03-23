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
import net.splitcells.dem.resource.ConfigFileSystem;
import net.splitcells.dem.resource.FileSystemView;
import net.splitcells.dem.utils.ExecutionException;

import java.util.function.BiFunction;

import static net.splitcells.dem.Dem.configValue;
import static net.splitcells.dem.data.set.Sets.setOfUniques;
import static net.splitcells.dem.data.set.map.Maps.map;
import static net.splitcells.dem.lang.tree.TreeI.tree;
import static net.splitcells.dem.utils.ExecutionException.execException;
import static net.splitcells.website.server.security.authentication.UserSession.*;


public class AuthenticatorImpl implements Authenticator {
    public static final String USER_FOLDER = "net/splitcells/website/server/security/users/";

    public static final String PASSWORD_FILE = "/password";

    public static Authenticator authenticator(BiFunction<Login, Authenticator, UserSession> userLogin) {
        return new AuthenticatorImpl(userLogin);
    }

    public static Authenticator authenticatorBasedOnFiles() {
        return authenticatorBasedOnFiles(configValue(ConfigFileSystem.class).subFileSystem(USER_FOLDER));
    }

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
     * <p>Only usernames matching to {@link Authenticator#VALID_USERNAME_SYMBOLS} are allowed.</p>
     * <p>TODO Speed up logins by caching these.
     * This requires one to know, when the user data changes inside {@code userData}.
     * This is best solved by having a subscription on {@code userData}.
     * Another way of doing this, is to only allow the program to change {@code userData} via a form and
     * assuming that the data is not changed by external programs.
     * Thereby, the form code can trigger the appropriate cache invalidation.
     * This would be the preferred way.
     * If the OS does not actively support this, this could also be done via a thread,
     * that checks the {@code userData} content every minute regarding the cache content,
     * but in this case some status info in the admin GUI or user data update form needs to signal the cache status</p>
     */
    public static Authenticator authenticatorBasedOnFiles(FileSystemView userData) {
        return new AuthenticatorImpl((login, argAuthenticator) -> {
            if (!userData.isFile(login.username() + AuthenticatorImpl.PASSWORD_FILE)) {
                return INSECURE_USER_SESSION;
            }
            /* TODO HACK This was a hack, because the equals method did not work otherwise.
             * Currently, it is unknown, why some text editors like nano add a new line symbol to the end,
             * that cannot be seen.
             */
            if (!VALID_USERNAME_SYMBOLS.matcher(login.username()).matches()) {
                return INSECURE_USER_SESSION;
            }
            final var storedPassword = userData.readString(login.username() + AuthenticatorImpl.PASSWORD_FILE).split("\n")[0];
            if (!login.password().equals(storedPassword)) {
                return INSECURE_USER_SESSION;
            }
            return UserSession.user(argAuthenticator);
        });
    }

    private final BiFunction<Login, Authenticator, UserSession> authenticator;
    private final Set<UserSession> validUserSessions = setOfUniques();
    private final Map<UserSession, String> usernames = map();

    private AuthenticatorImpl(BiFunction<Login, Authenticator, UserSession> argAuthenticator) {
        authenticator = argAuthenticator;
    }

    @Override
    public synchronized UserSession userSession(Login login) {
        final var userSession = authenticator.apply(login, this);
        if (UserSession.isValidNoLoginStandard(userSession)) {
            return userSession;
        }
        validUserSessions.add(userSession);
        usernames.put(userSession, login.username());
        if (!isValid(userSession)) {
            throw ExecutionException.execException(tree("Could not create a valid user session, given the login.")
                    .withProperty("login data", login.toString()));
        }
        return userSession;
    }

    @Override
    public synchronized boolean isValid(UserSession userSession) {
        if (isValidNoLoginStandard(userSession)) {
            validUserSessions.requireAbsenceOf(userSession);
            usernames.requireKeyAbsence(userSession);
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
    public synchronized void endSession(UserSession userSession) {
        requireValid(userSession);
        if (isValidNoLoginStandard(userSession)) {
            validUserSessions.requireAbsenceOf(userSession);
            usernames.requireKeyAbsence(userSession);
            return;
        }
        validUserSessions.remove(userSession);
        usernames.remove(userSession);
    }

    @Override
    public synchronized String name(UserSession userSession) {
        requireValid(userSession);
        if (isValidNoLoginStandard(userSession)) {
            usernames.requireKeyAbsence(userSession);
            return noLoginUserId(userSession);
        }
        return usernames.get(userSession);
    }

    private synchronized void requireValid(UserSession userSession) {
        if (!isValid(userSession)) {
            throw ExecutionException.execException(tree("The given user session is invalid")
                    .withProperty("user session", userSession.toString()));
        }
    }
}
