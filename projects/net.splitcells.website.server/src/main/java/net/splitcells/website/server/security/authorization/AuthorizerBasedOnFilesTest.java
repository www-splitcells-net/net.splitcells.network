/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.website.server.security.authorization;

import net.splitcells.dem.testing.annotations.UnitTest;
import net.splitcells.dem.utils.StringUtils;

import static net.splitcells.dem.data.atom.Bools.require;
import static net.splitcells.dem.data.atom.Bools.requireNot;
import static net.splitcells.dem.resource.FileSystemViaMemory.fileSystemViaMemory;
import static net.splitcells.website.server.security.authentication.Authentication.anonymous;
import static net.splitcells.website.server.security.authentication.UserSession.*;
import static net.splitcells.website.server.security.authorization.AuthorizerBasedOnFiles.ROLE_FOLDER;
import static net.splitcells.website.server.security.authorization.AuthorizerBasedOnFiles.authorizerBasedOnFiles;
import static net.splitcells.website.server.security.authorization.Roles.role;

public class AuthorizerBasedOnFilesTest {
    @UnitTest
    public void test() {
        final var username = ANONYMOUS_USER_NAME;
        final var roleName = "role-456";
        final var userData = fileSystemViaMemory();
        userData.createDirectoryPath(username + ROLE_FOLDER);
        userData.writeToFile(username + ROLE_FOLDER + roleName, StringUtils.toBytes(""));
        final var testSubject = authorizerBasedOnFiles(userData);
        require(testSubject.hasRole(anonymous(), role(roleName)));
    }
}
