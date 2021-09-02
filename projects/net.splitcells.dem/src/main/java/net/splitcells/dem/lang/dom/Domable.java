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
package net.splitcells.dem.lang.dom;

import net.splitcells.dem.lang.perspective.Perspective;
import org.w3c.dom.Node;

import static net.splitcells.dem.lang.perspective.PerspectiveI.perspective;
import static net.splitcells.dem.utils.NotImplementedYet.notImplementedYet;

/**
 * XML descriptions for instances of this interfaces can be created.
 * This can be seen as an alternative to String.
 */
public interface Domable {
    @Deprecated
    Node toDom();

    default Perspective toPerspective() {
        return perspective("TODO-Not-implemented-yet");
    }
}
