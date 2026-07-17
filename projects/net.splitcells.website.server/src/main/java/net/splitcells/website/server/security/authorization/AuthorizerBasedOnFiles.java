/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.website.server.security.authorization;

import net.splitcells.dem.resource.ConfigFileSystem;
import net.splitcells.dem.resource.FileSystemView;
import net.splitcells.website.server.security.authentication.Authentication;
import net.splitcells.website.server.security.authentication.UserSession;

import static net.splitcells.dem.Dem.configValue;
import static net.splitcells.website.server.security.authentication.AuthenticatorImpl.USER_FOLDER;
import static net.splitcells.website.server.security.authorization.Role.requireValid;

/**
 * <p>Has a user to {@link Role} mapping in a file system in the form of `[username]/[role name]`.
 * If this file is present, a given username has the given role.
 * The file's content is not relevant.</p>
 */
public class AuthorizerBasedOnFiles implements Authorizer {
    public static Authorizer authorizerBasedOnFiles() {
        return new AuthorizerBasedOnFiles(configValue(ConfigFileSystem.class)
                .subFileSystem(USER_FOLDER));
    }

    public static Authorizer authorizerBasedOnFiles(FileSystemView fileSystemView) {
        return new AuthorizerBasedOnFiles(fileSystemView);
    }

    public static final String ROLE_FOLDER = "/roles/";

    private final FileSystemView userRoles;

    private AuthorizerBasedOnFiles(FileSystemView argUserRoles) {
        userRoles = argUserRoles;
    }

    @Override
    public synchronized boolean hasRole(UserSession userSession, Role role) {
        requireValid(role);
        return userRoles.isFile(configValue(Authentication.class).userId(userSession) + ROLE_FOLDER + role.name());
    }
}