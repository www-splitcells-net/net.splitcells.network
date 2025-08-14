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
package net.splitcells.website.server.notify;

import net.splitcells.dem.testing.annotations.UnitTest;

import java.time.ZoneId;
import java.time.ZonedDateTime;

import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.website.Format.COMMON_MARK;
import static net.splitcells.website.server.notify.Notification.notification;
import static net.splitcells.website.server.notify.NotificationQueue.notificationQueue;

public class NotificationQueueTest {
    @UnitTest
    public void testWithAdditionalNotifications() {
        final var testSubject = notificationQueue();


        final var testData1 = list(notification(ZonedDateTime.of(1992,3,11,0,0,0,1000, ZoneId.of("UTC")), COMMON_MARK, "A")
                , notification(ZonedDateTime.of(1992,3,11,0,0,0,1010, ZoneId.of("UTC")), COMMON_MARK, "B")
                , notification(ZonedDateTime.of(1992,3,11,0,0,0,1030, ZoneId.of("UTC")), COMMON_MARK, "C"));
        final var testData2 = list(notification(ZonedDateTime.of(1992,3,11,0,0,0,1000, ZoneId.of("UTC")), COMMON_MARK, "D")
                , notification(ZonedDateTime.of(1992,3,11,0,0,0,1011, ZoneId.of("UTC")), COMMON_MARK, "E")
                , notification(ZonedDateTime.of(1992,3,11,0,0,0,1029, ZoneId.of("UTC")), COMMON_MARK, "F"));
        testSubject.withAdditionalNotifications(testData1);
        testSubject.withAdditionalNotifications(testData2);
        testSubject.notifications().requireEquals(list(
                testData1.get(0)
                , testData2.get(0)
                , testData1.get(1)
                , testData2.get(1)
                , testData2.get(2)
                , testData1.get(2)));
    }
}
