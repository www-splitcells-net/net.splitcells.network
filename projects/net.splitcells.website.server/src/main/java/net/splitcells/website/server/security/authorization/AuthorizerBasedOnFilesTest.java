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

import net.splitcells.dem.data.atom.Bools;
import net.splitcells.dem.testing.annotations.UnitTest;
import net.splitcells.dem.utils.StringUtils;

import static net.splitcells.dem.data.atom.Bools.require;
import static net.splitcells.dem.data.atom.Bools.requireNot;
import static net.splitcells.dem.resource.FileSystemViaMemory.fileSystemViaMemory;
import static net.splitcells.website.server.security.authentication.AuthenticatorImpl.PASSWORD_FILE;
import static net.splitcells.website.server.security.authentication.UserSession.*;
import static net.splitcells.website.server.security.authorization.AuthorizerBasedOnFiles.ROLE_FOLDER;
import static net.splitcells.website.server.security.authorization.AuthorizerBasedOnFiles.authorizerBasedOnFiles;
import static net.splitcells.website.server.security.authorization.Role.role;

public class AuthorizerBasedOnFilesTest {
    @UnitTest
    public void test() {
        final var username = ANONYMOUS_USER_NAME;
        final var roleName = "role-456";
        final var userData = fileSystemViaMemory();
        userData.createDirectoryPath(username + ROLE_FOLDER);
        userData.writeToFile(username + ROLE_FOLDER + roleName, StringUtils.toBytes(""));
        final var testSubject = authorizerBasedOnFiles(userData);
        require(testSubject.hasRole(ANONYMOUS_USER_SESSION, role(roleName)));
        requireNot(testSubject.hasRole(INSECURE_USER_SESSION, role(roleName)));
    }
}
