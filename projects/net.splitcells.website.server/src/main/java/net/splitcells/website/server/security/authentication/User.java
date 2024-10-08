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
 * An instance of this class, is the claim,
 * that a {@link User} exists at {@link Users} and
 * where {@link #authenticatedBy} is equals to the value of {@link Users}.
 * Only {@link Option} objects like {@link Users} or {@link Authorization}
 * can provide any trustworthy information for any given {@link User}.
 * Any other kind of user processing,
 * is invalid for the actual real world process of authentication and/or authorization.
 */
public class User {
    public static final User ANONYMOUS_USER = notAuthenticatedUser(Users.class.getName() + ".anonymous");
    public static final User INVALID_LOGIN = notAuthenticatedUser(Users.class.getName() + ".invalid.login");

    public static User notAuthenticatedUser(String name) {
        return new User(name, Optional.empty());
    }

    public static User user(String name, Authenticator authenticatedBy) {
        return new User(name, Optional.of(authenticatedBy));
    }

    private final String name;
    private final Optional<Authenticator> authenticatedBy;

    private User(String argName, Optional<Authenticator> argAuthenticatedBy) {
        authenticatedBy = argAuthenticatedBy;
        name = argName;
    }

    /**
     * @return If the return value {@link Optional#isEmpty()}, than this {@link User} is not trustworthy.
     */
    public Optional<Authenticator> authenticatedBy() {
        return authenticatedBy;
    }

    public String name() {
        return name;
    }
}
