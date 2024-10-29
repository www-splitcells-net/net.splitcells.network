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

import net.splitcells.dem.resource.ConfigFileSystem;
import net.splitcells.dem.resource.FileSystemView;
import net.splitcells.website.server.security.authentication.Authentication;
import net.splitcells.website.server.security.authentication.UserSession;

import static net.splitcells.dem.Dem.configValue;
import static net.splitcells.website.server.security.authorization.Role.requireValid;

/**
 * <p>Has a user to {@link Role} mapping in a file system in the form of `[username]/[role name]`.
 * If this file is present, a given username has the given role.
 * The file's content is not relevant.</p>
 */
public class AuthorizerBasedOnFiles implements Authorizer {
    public static Authorizer authorizerBasedOnFiles() {
        return new AuthorizerBasedOnFiles(configValue(ConfigFileSystem.class)
                .subFileSystem(ROLE_FOLDER));
    }

    public static Authorizer authorizerBasedOnFiles(FileSystemView fileSystemView) {
        return new AuthorizerBasedOnFiles(fileSystemView);
    }

    private static final String ROLE_FOLDER = "net/splitcells/website/server/security/user-roles/";

    private final FileSystemView userRoles;

    private AuthorizerBasedOnFiles(FileSystemView argUserRoles) {
        userRoles = argUserRoles;
    }

    @Override
    public boolean hasRole(UserSession userSession, Role role) {
        requireValid(role);
        return userRoles.isFile(configValue(Authentication.class).name(userSession) + "/" + role.name());
    }
}