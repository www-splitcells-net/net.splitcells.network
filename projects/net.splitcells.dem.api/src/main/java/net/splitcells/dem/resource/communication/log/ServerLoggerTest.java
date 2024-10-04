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
package net.splitcells.dem.resource.communication.log;

import net.splitcells.dem.testing.annotations.UnitTest;

import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.lang.tree.TreeI.tree;
import static net.splitcells.dem.resource.communication.log.ServerLogger.serverLog;

public class ServerLoggerTest {
    @UnitTest
    public void test() {
        final SenderStub<String> results = SenderStub.create();
        try (final var testSubject = serverLog(results, arg -> LogLevel.TRACE.smallerThan(arg.priority()))) {
            testSubject.append(tree("1"), LogLevel.TRACE);
            testSubject.append(tree("2"), LogLevel.INFO);
            testSubject.flush();
            results.storage().requireEqualityTo(list("[\"2\"]"));
            testSubject.append(tree("object").withProperty("attribute", "value"), LogLevel.WARNING);
        }
        results.storage().requireEqualityTo(list("[\"2\"]", "{\"object\": {\"attribute\": [\"value\"]}}"));
    }
}
