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

import java.util.Optional;

/**
 * An instance of this class, is the claim,
 * that a {@link UserSession} exists at {@link Authorization} and
 * where {@link #authenticatedBy} is equals to the value of {@link Authorization}.
 * Only {@link Option} objects like {@link Authorization} or {@link net.splitcells.website.server.security.authorization.Authorization}
 * can provide any trustworthy information for any given {@link UserSession}.
 * Any other kind of user processing,
 * is untrustworthy for the actual real world process of authentication and/or authorization.
 */
public class UserSession {
    public static final UserSession ANONYMOUS_USER_SESSION = notAuthenticatedUser();
    public static final UserSession INVALID_LOGIN = notAuthenticatedUser();

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
