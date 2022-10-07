/*
 * Copyright (c) 2021 Mārtiņš Avots (Martins Avots) and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0, or the MIT License,
 * which is available at https://spdx.org/licenses/MIT.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR MIT
 */
package net.splitcells.website.server.project.renderer.extension.commonmark;

import net.splitcells.dem.lang.annotations.JavaLegacyArtifact;
import org.commonmark.node.Node;

import java.time.LocalDateTime;

@JavaLegacyArtifact
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
