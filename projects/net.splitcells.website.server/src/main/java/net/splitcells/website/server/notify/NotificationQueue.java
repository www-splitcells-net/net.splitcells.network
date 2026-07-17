/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.website.server.notify;

import net.splitcells.dem.data.order.Comparators;
import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.data.set.list.ListView;
import net.splitcells.dem.data.set.list.Lists;

import static net.splitcells.dem.data.set.list.Lists.list;

public class NotificationQueue {
    public static NotificationQueue notificationQueue() {
        return new NotificationQueue();
    }

    private List<Notification> notifications = list();
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

    public List<Notification> notifications() {
        if (!isSorted) {
            notifications.sort((a, b) -> a.time().compareTo(b.time()));
        }
        return notifications;
    }

    private boolean hasAtLeastOneTag(Notification notification, String... tags) {
        for (int i = 0; i < tags.length; ++i) {
            if (notification.tags().contains(tags[i])) {
                return true;
            }
        }
        return false;
    }

    public NotificationQueue withSelectedTags(String... tags) {
        notifications = notifications.stream().filter(n -> hasAtLeastOneTag(n, tags)).toList();
        return this;
    }

    public NotificationQueue withoutTags(String... tags) {
        notifications = notifications.stream().filter(n -> !hasAtLeastOneTag(n, tags)).toList();
        return this;
    }

    public String toHtml() {
        return "<ol xmlns=\"http://www.w3.org/1999/xhtml\">"
                + notifications().reversed().stream()
                .map(n -> "<li>" + n.toHtml() + "</li>")
                .reduce((a, b) -> a + "\n" + b)
                .orElse("")
                + "</ol>";
    }
}
