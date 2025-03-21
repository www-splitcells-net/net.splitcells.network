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

import net.splitcells.dem.data.order.Comparators;
import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.data.set.list.ListView;

import static net.splitcells.dem.data.set.list.Lists.list;

public class NotificationQueue {
    public static NotificationQueue notificationQueue() {
        return new NotificationQueue();
    }

    private final List<Notification> notifications = list();
    private boolean isSorted = true;

    private NotificationQueue() {

    }

    public NotificationQueue withAdditionalNotifications(List<Notification> arg) {
        notifications.addAll(arg);
        isSorted = false;
        return this;
    }

    public NotificationQueue withAdditionalNotification(Notification arg) {
        notifications.add(arg);
        isSorted = false;
        return this;
    }

    public ListView<Notification> notifications() {
        if (!isSorted) {
            notifications.sort((a, b) -> a.time().compareTo(b.time()));
        }
        return notifications;
    }
}
