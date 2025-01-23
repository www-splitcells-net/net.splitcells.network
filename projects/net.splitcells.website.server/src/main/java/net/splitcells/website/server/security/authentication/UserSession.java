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
import net.splitcells.dem.utils.ExecutionException;
import net.splitcells.website.server.security.authorization.Authorization;

import java.util.Optional;

import static net.splitcells.dem.lang.tree.TreeI.tree;
import static net.splitcells.dem.utils.ExecutionException.execException;

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
 * <p>The goal behind this is to force any user of {@link UserSession} to get any info about it
 * only through trusted authorities.
 * This discourages usage of {@link UserSession} directly without checking its validity,
 * as the main job of {@link UserSession} is to provide a key/id for authorities,
 * that cannot be copied, faked and misused for other than intended operations.</p>
 */
public class UserSession {
    /**
     * This method exists, so that valid {@link UserSession} without a login can be checked,
     * while having a central location for such a default definition.
     *
     * @param userSession
     * @return Returns true if the given {@link UserSession} is one,
     * that is always expected to be true by default for any given {@link Authentication}.
     * Any {@link Authentication} is allowed to not discard such {@link UserSession},
     * but most will accept such.
     */
    public static boolean isValidNoLoginStandard(UserSession userSession) {
        return userSession == ANONYMOUS_USER_SESSION || userSession == INSECURE_USER_SESSION;
    }

    public static String noLoginUserId(UserSession userSession) {
        if (userSession == ANONYMOUS_USER_SESSION) {
            return ANONYMOUS_USER_NAME;
        }
        if (userSession == INSECURE_USER_SESSION) {
            return INSECURE_USER_NAME;
        }
        throw ExecutionException.execException(tree("Not a valid no login user was given")
                .withProperty("user session", userSession.toString()));
    }

    /**
     * This is the id of the user of {@link #ANONYMOUS_USER_SESSION}.
     */
    public static final String ANONYMOUS_USER_NAME = "anonymous";

    /**
     * This is the id of the user of {@link #INSECURE_USER_SESSION}.
     */
    public static final String INSECURE_USER_NAME = "insecure-user";
    /**
     * This {@link UserSession} is created without any {@link Login},
     * or a login with an invalid name.
     * As a username is no secret, this is not considered to be a security issue.
     * Therefore, this {@link UserSession} represents a user,
     * that is not registered to this program.
     * This is also known as a random internet user.
     */
    public static final UserSession ANONYMOUS_USER_SESSION = notAuthenticatedUser();
    /**
     * This {@link UserSession} is created by invalid secrets provided by {@link Login}.
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
