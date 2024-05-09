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
package net.splitcells.website.server.vertx;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.User;
import net.splitcells.dem.lang.annotations.JavaLegacyArtifact;
import net.splitcells.dem.testing.Assertions;
import net.splitcells.dem.testing.annotations.UnitTest;
import net.splitcells.dem.utils.StringUtils;

import static net.splitcells.dem.data.atom.Bools.require;
import static net.splitcells.dem.resource.FileSystemViaMemory.fileSystemViaMemory;
import static net.splitcells.website.server.vertx.FileBasedAuthenticationProvider.fileBasedAuthenticationProvider;

@JavaLegacyArtifact
public class FileBasedAuthenticationProviderTest {
    @UnitTest
    public void test() {
        final var username = "username-123";
        final var password = "password-456";
        final var userData = fileSystemViaMemory();
        userData.writeToFile(username, StringUtils.toBytes(password));
        final var testSubject = fileBasedAuthenticationProvider(userData);
        final var validRequest = new JsonObject("{\"username\":\"" + username + "\",\"password\":\"" + password + "\"}");
        testSubject.authenticate(validRequest, new Handler<>() {

            @Override
            public void handle(AsyncResult<User> event) {
                Assertions.requireEquals(event.result().subject(), username);
            }
        });
        final var invalidRequest = new JsonObject("{\"username\":\"username\",\"password\":\"password\"}");
        testSubject.authenticate(invalidRequest, new Handler<>() {

            @Override
            public void handle(AsyncResult<User> event) {
                require(event.failed());
            }
        });
    }
}
