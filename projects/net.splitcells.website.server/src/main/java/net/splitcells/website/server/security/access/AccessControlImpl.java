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

import net.splitcells.website.server.security.authentication.Authentication;
import net.splitcells.website.server.security.authentication.Authenticator;
import net.splitcells.website.server.security.authentication.Login;
import net.splitcells.website.server.security.authentication.UserSession;

import java.util.function.BiConsumer;

import static net.splitcells.dem.Dem.configValue;

public class AccessControlImpl<T extends Firewall> implements AccessControl<T> {
    public static <R extends Firewall> AccessControl<R> accessControl(AccessProvider<R> accessProvider) {
        return new AccessControlImpl<>(configValue(Authentication.class), accessProvider);
    }

    public static <R extends Firewall> AccessControl<R> accessControl(Authenticator authenticator
            , AccessProvider<R> accessProvider) {
        return new AccessControlImpl<>(authenticator, accessProvider);
    }

    private final Authenticator authenticator;
    private final AccessProvider<T> accessProvider;

    private AccessControlImpl(Authenticator argAuthenticator, AccessProvider<T> argAccessProvider) {
        authenticator = argAuthenticator;
        accessProvider = argAccessProvider;
    }

    @Override public void access(BiConsumer<UserSession, T> action, Login login) {
        access(action, authenticator.userSession(login));
    }

    @Override public void access(BiConsumer<UserSession, T> action, UserSession userSession) {
        try {
            accessProvider.access(a -> action.accept(userSession, a), userSession);
        } finally {
            authenticator.endSession(userSession);
        }
    }

    @Override public void access(BiConsumer<UserSession, T> action, UserSession userSession, String lifeCycleId) {
        try {
            accessProvider.access(a -> action.accept(userSession, a), userSession, lifeCycleId);
        } finally {
            authenticator.endSession(userSession);
        }
    }
}
