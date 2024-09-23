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
package net.splitcells.gel.data.table;

import net.splitcells.dem.lang.Xml;
import net.splitcells.dem.lang.dom.Domable;
import net.splitcells.dem.lang.perspective.Perspective;

import java.util.Optional;

import static net.splitcells.dem.lang.Xml.elementWithChildren;
import static net.splitcells.dem.lang.Xml.textNode;
import static net.splitcells.dem.lang.perspective.PerspectiveI.perspective;

public interface LinePointer extends Domable {
    Table context();

    int index();

    default Optional<Line> interpret() {
        return interpret(context());
    }

    @Deprecated
    Optional<Line> interpret(Table context);

    @Override
    default Perspective toPerspective() {
        final var dom = perspective(LinePointer.class.getSimpleName());
        final var line = interpret();
        if (line.isPresent()) {
            dom.withChild(line.get().toPerspective());
        } else {
            dom.withProperty("index", index() + "");
        }
        return dom;
    }
}
