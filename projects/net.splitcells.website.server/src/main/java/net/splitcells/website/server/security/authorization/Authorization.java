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
package net.splitcells.website.server.security.authorization;

import net.splitcells.dem.environment.config.framework.Option;
import net.splitcells.website.server.security.authentication.UserSession;

import static net.splitcells.dem.Dem.configValue;
import static net.splitcells.website.server.security.authorization.AuthorizerInMemory.authorizerInMemory;
import static net.splitcells.website.server.security.authorization.Role.ANONYMOUS_ROLE;

public class Authorization implements Option<Authorizer> {
    @Override
    public Authorizer defaultValue() {
        return authorizerInMemory((user, role) -> ANONYMOUS_ROLE.equals(role));
    }

    public static boolean hasRole(UserSession userSession, Role role) {
        return configValue(Authorization.class).hasRole(userSession, role);
    }

    public static boolean missesRole(UserSession userSession, Role role) {
        return !configValue(Authorization.class).hasRole(userSession, role);
    }
}
