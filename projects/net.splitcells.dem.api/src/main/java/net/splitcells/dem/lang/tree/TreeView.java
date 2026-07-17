/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.dem.lang.tree;

import net.splitcells.dem.data.set.list.ListView;
import net.splitcells.dem.lang.dom.Domable;
import net.splitcells.dem.lang.namespace.NameSpace;

import java.util.Optional;

import static net.splitcells.dem.data.set.list.Lists.listWithValuesOf;

public interface TreeView extends Domable {

    NameSpace nameSpace();

    String name();

    default String xmlName() {
        return name()
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&apos;")
                .replace("~", "&Tilde;");
    }

    default Optional<Tree> value() {
        if (children().size() == 1) {
            return Optional.of(children().get(0));
        }
        return Optional.empty();
    }

    default String valueName() {
        return children().get(0).name();
    }

    ListView<Tree> children();

    default boolean nameIs(String value, NameSpace nameSpace) {
        return nameSpace().equals(nameSpace) && name().equals(value);
    }
}
