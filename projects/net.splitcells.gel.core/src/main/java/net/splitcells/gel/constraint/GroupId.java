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
package net.splitcells.gel.constraint;

import static net.splitcells.dem.lang.Xml.elementWithChildren;
import static net.splitcells.dem.lang.Xml.textNode;

import java.util.Optional;

import net.splitcells.dem.lang.Xml;
import org.w3c.dom.Element;
import net.splitcells.dem.lang.dom.Domable;

public class GroupId implements Domable {
    @Deprecated
    public GroupId() {
        vārds = Optional.empty();
    }

    private GroupId(String vārds) {
        this.vārds = Optional.ofNullable(vārds);
    }

    private final Optional<String> vārds;

    public static GroupId grupa() {
        return new GroupId();
    }

    public static GroupId group(String vārds) {
        return new GroupId(vārds);
    }

    @Deprecated
    public String gūtVārds() {
        return vārds.get();
    }

    public Optional<String> vārds() {
        return vārds;
    }

    public static GroupId multiply(GroupId a, GroupId b) {
        return new GroupId(a.toString() + " un " + b.toString());
    }

    @Override
    public String toString() {
        return vārds.orElse(super.toString());
    }

    @Override
    public Element toDom() {
        final var dom = Xml.elementWithChildren("group");
        if (vārds.isPresent()) {
            dom.appendChild(Xml.elementWithChildren("name", textNode(vārds.get())));
        }
        dom.appendChild(Xml.elementWithChildren("id", textNode(this.hashCode() + "")));
        return dom;
    }
}
