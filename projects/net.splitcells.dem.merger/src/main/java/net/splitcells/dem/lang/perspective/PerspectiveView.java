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
package net.splitcells.dem.lang.perspective;

import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.data.set.list.ListView;
import net.splitcells.dem.lang.dom.Domable;
import net.splitcells.dem.lang.namespace.NameSpace;

import java.util.Optional;

public interface PerspectiveView extends Domable {

    NameSpace nameSpace();

    String name();

    default String xmlName() {
        return name().replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&apos;")
                .replace("&", "&amp;");
    }

    default Optional<Perspective> value() {
        if (children().size() == 1) {
            return Optional.of(children().get(0));
        }
        return Optional.empty();
    }

    ListView<Perspective> children();

    default boolean nameIs(String value, NameSpace nameSpace) {
        return nameSpace().equals(nameSpace) && name().equals(value);
    }
}
