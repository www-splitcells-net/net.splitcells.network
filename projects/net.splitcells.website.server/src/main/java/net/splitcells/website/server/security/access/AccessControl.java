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
package net.splitcells.website.server.security.access;

import net.splitcells.dem.Dem;
import net.splitcells.website.server.security.authentication.Authenticator;
import net.splitcells.website.server.security.authentication.Login;
import net.splitcells.website.server.security.authentication.UserSession;

import java.util.function.BiConsumer;

/**
 * TODO Consider using the {@link UserSession} that is stored at a new {@link Dem#config()}.
 * In other words, for every user access a child {@link Dem} would have to be created,
 * where {@link Dem#config()} is the same as the parent one,
 * but contains the current {@link UserSession}.
 * The advantage of this, is the fact, that a {@link UserSession} always exists and
 * is harder to accidentally swap with other {@link UserSession},
 * which causes access rights escalations.
 *
 * @param <T> This is the type of object, whose access is controlled via {@link UserSession}.
 *            The instance of these themselves are responsible to only allow things,
 *            that the {@link UserSession} is allowed to do.
 */
public interface AccessControl<T extends Firewall> {
    /**
     * Provides a {@link T}, that is encapsulated in this, to the given running action.
     * During the run the {@link UserSession} is {@link Authenticator#isValid(UserSession)}
     * as long as the given action is running.
     * The provided {@link T} should only be valid while running the given action and
     * after that {@link AutoCloseable#close()} has to be executed.
     *
     * @param action This is an action, that requires a {@link UserSession} for authorization.
     * @param login  This is the {@link Login} used, in order to create a {@link UserSession} for authorization.
     */
    void access(BiConsumer<UserSession, T> action, Login login);

    /**
     * This is the helper method, in case the creation time of the {@link UserSession} cannot full be controlled,
     * by this {@link AccessControl}.
     * Keep in mind, that after the access, the user session will be invalidated.
     *
     * @param action      This is an action, that requires a {@link UserSession} for authorization.
     * @param userSession This object authorizes the access for the action.
     */
    void access(BiConsumer<UserSession, T> action, UserSession userSession);
}
