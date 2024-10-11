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

import net.splitcells.dem.testing.annotations.UnitTest;
import net.splitcells.dem.utils.StringUtils;

import static net.splitcells.dem.resource.FileSystemViaMemory.fileSystemViaMemory;
import static net.splitcells.dem.testing.Assertions.requireDistinct;
import static net.splitcells.dem.testing.Assertions.requireEquals;
import static net.splitcells.website.server.security.authentication.AuthenticatorBasedOnFiles.PASSWORD_FILE;
import static net.splitcells.website.server.security.authentication.AuthenticatorBasedOnFiles.authenticatorBasedOnFiles;
import static net.splitcells.website.server.security.authentication.UserSession.ANONYMOUS_USER_SESSION;
import static net.splitcells.website.server.security.authentication.UserSession.INSECURE_USER_SESSION;

public class AuthenticatorBasedOnFilesTest {
    @UnitTest
    public void test() {
        final var username = "username-123";
        final var password = "password-456";
        final var userData = fileSystemViaMemory();
        userData.createDirectoryPath(username);
        userData.writeToFile(username + PASSWORD_FILE, StringUtils.toBytes(password));
        final var testSubject = authenticatorBasedOnFiles(userData);
        final var validLogin = testSubject.userSession(BasicLogin.login(username, password));
        requireDistinct(validLogin, ANONYMOUS_USER_SESSION);
        requireDistinct(validLogin, INSECURE_USER_SESSION);
        requireEquals(testSubject.userSession(BasicLogin.login(username, "not-password"))
                , INSECURE_USER_SESSION);
        requireEquals(testSubject.userSession(BasicLogin.login("not-user", "not-password"))
                , ANONYMOUS_USER_SESSION);
    }
}
