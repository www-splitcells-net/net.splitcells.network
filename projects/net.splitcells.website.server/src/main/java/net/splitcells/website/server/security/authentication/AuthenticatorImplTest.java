/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.website.server.security.authentication;

import net.splitcells.dem.testing.annotations.UnitTest;
import net.splitcells.dem.utils.StringUtils;

import static net.splitcells.dem.resource.FileSystemViaMemory.fileSystemViaMemory;
import static net.splitcells.dem.testing.Assertions.requireDistinct;
import static net.splitcells.dem.testing.Assertions.requireEquals;
import static net.splitcells.website.server.security.authentication.AuthenticatorImpl.PASSWORD_FILE;
import static net.splitcells.website.server.security.authentication.AuthenticatorImpl.authenticatorBasedOnFiles;

public class AuthenticatorImplTest {
    @UnitTest
    public void test() {
        final var username = "username-123";
        final var password = "password-456";
        final var userData = fileSystemViaMemory();
        userData.createDirectoryPath(username);
        userData.writeToFile(username + PASSWORD_FILE, StringUtils.toBytes(password));
        final var testSubject = authenticatorBasedOnFiles(userData);
        final var validLogin = testSubject.userSession(Login.login(username, password));
        requireDistinct(validLogin, testSubject.anonymous());
        requireEquals(testSubject.userId(testSubject.userSession(Login.login(username, "not-password")))
                , testSubject.userId(testSubject.anonymous()));
        requireEquals(testSubject.userId(testSubject.userSession(Login.login("not-user", "not-password")))
                , testSubject.userId(testSubject.anonymous()));
    }
}
