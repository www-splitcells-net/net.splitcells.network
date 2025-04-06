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

import java.util.regex.Pattern;

/**
 * <p>TODO Create a base {@link Authenticator} class, that takes some authentication functions and
 * uses them in the recommended way.
 * Thereby, duplicated security sensitive code is omitted,
 * which can be important for things like caching.</p>
 * <p>Implementations should be thread safe, in order to minimize the number of {@link Authenticator} used at once.
 * This also avoid race conditions,
 * if a none thread safe implementation is accidentally used in multithreaded environments by accident.</p>
 */
public interface Authenticator {
    /**
     * Dots are not valid characters in usernames,
     * as dots are used in order to navigate folders relatively for instance via `../`.
     * Strictly speaking dots could be allowed, because slash (`/`) is not part of a valid username,
     * but it is better to be safe than sorry.
     */
    Pattern VALID_USERNAME_SYMBOLS = Pattern.compile("[a-zA-Z0-9 \\-]+");

    UserSession userSession(Login login);

    /**
     * <p>TODO Maybe it makes sense, to limit the number of valid {@link UserSession} at any given time,
     * in order to find bugs in the security system,
     * where problematic {@link UserSession} with infinite lifetimes are created.</p>
     * <p>Consider limiting the amount of valid {@link UserSession} at any one time as
     * a last resort to catch or notice {@link UserSession} that are valid without indefinitely.</p>
     *
     * @param userSession
     * @return Returns true, if the given {@link UserSession} is a valid authentication of a user.
     * Such {@link UserSession} are primarily ones, that were created by this {@link Authenticator} and is
     * still valid and therefore its lifetime has not ended yet.
     * There are also valid {@link UserSession}, that are not created by this,
     * like {@link UserSession#ANONYMOUS_USER_SESSION} or {@link UserSession#INSECURE_USER_SESSION}.
     * In other words, a {@link UserSession} is valid, when an {@link Login} was successfully mapped
     * to an {@link UserSession}, regardless of the correctness of the user's login data.
     * Therefore, a given {@link UserSession} is also a report regarding the validity of a corresponding {@link Login}.
     */
    boolean isValid(UserSession userSession);

    void endSession(UserSession userSession);

    /**
     * This is called username and not id,
     * as it is only intended to be valid for one {@link Authenticator}.
     *
     * @param userSession The session, for which the user's name is to be determined.
     * @return Returns the name of the user, that is backed by the {@link UserSession}.
     * Valid ids have to comply with {@link #VALID_USERNAME_SYMBOLS},
     * in order to avoid unexpected things based on usernames.
     */
    String name(UserSession userSession);
}
