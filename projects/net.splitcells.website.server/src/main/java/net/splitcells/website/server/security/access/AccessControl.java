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

import net.splitcells.website.server.security.authentication.Authenticator;
import net.splitcells.website.server.security.authentication.BasicLogin;
import net.splitcells.website.server.security.authentication.UserSession;

import java.util.function.BiConsumer;

public interface AccessControl<T extends AutoCloseable> {
    /**
     * Provides to a {@link T}, that is encapsulated in this, while running the given action.
     * During the run the {@link UserSession} is {@link Authenticator#isValid(UserSession)}
     * as long as the given action is running.
     * The provided {@link T} should only be valid while running the given action and
     * after that {@link AutoCloseable#close()} has to be executed.
     *
     * @param action
     * @param login
     */
    void access(BiConsumer<UserSession, T> action, BasicLogin login);
}
