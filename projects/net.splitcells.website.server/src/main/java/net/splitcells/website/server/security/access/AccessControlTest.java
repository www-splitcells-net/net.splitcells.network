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

import lombok.val;
import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.testing.annotations.UnitTest;
import net.splitcells.dem.utils.StringUtils;
import net.splitcells.website.server.security.authentication.UserSession;

import java.util.function.Consumer;
import java.util.function.Function;

import static net.splitcells.dem.data.atom.Bools.require;
import static net.splitcells.dem.data.atom.Bools.requireNot;
import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.resource.FileSystemViaMemory.fileSystemViaMemory;
import static net.splitcells.website.server.security.access.StaticAccessControl.staticAccessControl;
import static net.splitcells.website.server.security.authentication.AuthenticatorImpl.PASSWORD_FILE;
import static net.splitcells.website.server.security.authentication.AuthenticatorImpl.authenticatorBasedOnFiles;
import static net.splitcells.website.server.security.authentication.Login.login;

public class AccessControlTest {
    @UnitTest
    public void test() {
        val username = "username-123";
        val password = "password-456";
        val userData = fileSystemViaMemory();
        userData.createDirectoryPath(username);
        userData.writeToFile(username + PASSWORD_FILE, StringUtils.toBytes(password));
        val authenticator = authenticatorBasedOnFiles(userData);
        final List<Object> accessRecorder = list();
        final List<UserSession> userSessions = list();
        val accessCounter = list(0);
        final var testSubject = staticAccessControl(authenticator, accessCounter);
        testSubject.access((u, a) -> {
            accessRecorder.requireEmpty();
            userSessions.requireEmpty();
            a.set(0, 1 + a.get(0));
            if (authenticator.isActivelyAuthenticated(u)) {
                userSessions.add(u);
                accessRecorder.add(a);
            }
        }, login(username, password));
        accessRecorder.requireEquals(list(accessCounter));
        userSessions.requireSizeOf(1);
        requireNot(authenticator.isValid(userSessions.get(0)));
        accessCounter.require(0, 1);
    }
}
