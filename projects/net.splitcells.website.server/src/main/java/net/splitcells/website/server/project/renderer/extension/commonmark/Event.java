/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.website.server.project.renderer.extension.commonmark;

import net.splitcells.dem.lang.annotations.JavaLegacy;
import org.commonmark.node.Node;

import java.time.LocalDateTime;

@JavaLegacy
public class Event {
    public static Event event(LocalDateTime dateTime, Node node) {
        return new Event(dateTime, node);
    }

    private final LocalDateTime dateTime;
    private final Node node;

    private Event(LocalDateTime dateTime, Node node) {
        this.dateTime = dateTime;
        this.node = node;
    }

    public LocalDateTime dateTime() {
        return dateTime;
    }

    public Node node() {
        return node;
    }
}
