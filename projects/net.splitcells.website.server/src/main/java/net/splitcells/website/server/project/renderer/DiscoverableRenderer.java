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
package net.splitcells.website.server.project.renderer;

import net.splitcells.dem.object.Discoverable;

import java.util.Optional;

/**
 * TODO IDEA Render private attributes of {@link DiscoverableRenderer} for debugging.
 * For this paths would have to be automatically generated for each private attribute.
 */
public interface DiscoverableRenderer extends Discoverable {
    /**
     *
     * @return This returns an HTML string, that can be embedded into another HTML document.
     */
    String render();

    Optional<String> title();
}
