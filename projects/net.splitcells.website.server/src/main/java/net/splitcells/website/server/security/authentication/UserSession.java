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

import net.splitcells.dem.environment.config.framework.Option;
import net.splitcells.website.server.security.authorization.Authorization;

import java.util.Optional;

/**
 * <p>An instance of this class, is the claim,
 * that a {@link UserSession} exists at {@link Authentication},
 * where {@link #authenticatedBy} is equals to the value of {@link Authentication}.</p>
 * <p>Only the {@link Option} objects {@link Authentication} or {@link Authorization}
 * can provide any trustworthy information for any given {@link UserSession}.
 * Any other kind of user processing,
 * is untrustworthy for the actual real world process of authentication and/or authorization.
 * This way, the only way to create a valid {@link UserSession} is by using {@link Authentication}.
 * No other code can therefore fake a valid {@link UserSession}.</p>
 */
public class UserSession {
    /**
     * This {@link UserSession} is created without any {@link BasicLogin},
     * or a login with an invalid name.
     * As a username is no secret, this is not considered to be a security issue.
     * Therefore, this {@link UserSession} represents a user,
     * that is not registered to this program.
     * This is also known as a random internet user.
     */
    public static final UserSession ANONYMOUS_USER_SESSION = notAuthenticatedUser();
    /**
     * This {@link UserSession} is created by invalid secrets provided by {@link BasicLogin}.
     * If no special treatment is required,
     * this user can also be handled exactly like {@link #ANONYMOUS_USER_SESSION}.
     */
    public static final UserSession INSECURE_USER_SESSION = notAuthenticatedUser();

    public static UserSession notAuthenticatedUser() {
        return new UserSession(Optional.empty());
    }

    public static UserSession user(Authenticator authenticatedBy) {
        return new UserSession(Optional.of(authenticatedBy));
    }

    private final Optional<Authenticator> authenticatedBy;

    private UserSession(Optional<Authenticator> argAuthenticatedBy) {
        authenticatedBy = argAuthenticatedBy;
    }

    /**
     * @return If the return value {@link Optional#isEmpty()}, than this {@link UserSession} is not trustworthy.
     */
    public Optional<Authenticator> authenticatedBy() {
        return authenticatedBy;
    }
}
