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

import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.testing.annotations.UnitTest;
import net.splitcells.dem.utils.StringUtils;
import net.splitcells.website.server.security.authentication.UserSession;

import static net.splitcells.dem.data.atom.Bools.require;
import static net.splitcells.dem.data.atom.Bools.requireNot;
import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.resource.FileSystemViaMemory.fileSystemViaMemory;
import static net.splitcells.website.server.security.access.AccessControlImpl.accessControl;
import static net.splitcells.website.server.security.authentication.AuthenticatorBasedOnFiles.PASSWORD_FILE;
import static net.splitcells.website.server.security.authentication.AuthenticatorBasedOnFiles.authenticatorBasedOnFiles;
import static net.splitcells.website.server.security.authentication.Login.login;
import static net.splitcells.website.server.security.authentication.UserSession.*;

public class AccessControlTest {
    @UnitTest
    public void test() {
        final var username = "username-123";
        final var password = "password-456";
        final var userData = fileSystemViaMemory();
        userData.createDirectoryPath(username);
        userData.writeToFile(username + PASSWORD_FILE, StringUtils.toBytes(password));
        final var authenticator = authenticatorBasedOnFiles(userData);
        final List<Firewall> accessCounter = list();
        final List<UserSession> userSessions = list();
        final var subjectAccessed = new Firewall() {
            boolean isValid = true;
        };
        final var testSubject = accessControl(authenticator, c -> {
            c.accept(subjectAccessed);
            subjectAccessed.isValid = false;
        });
        testSubject.access((u, a) -> {
            accessCounter.requireEmpty();
            userSessions.requireEmpty();
            require(subjectAccessed.isValid);
            if (!isValidNoLoginStandard(u)) {
                userSessions.add(u);
                accessCounter.add(a);
            }
        }, login(username, password));
        accessCounter.requireEquals(list(subjectAccessed));
        userSessions.requireSizeOf(1);
        requireNot(authenticator.isValid(userSessions.get(0)));
        requireNot(subjectAccessed.isValid);
    }
}
