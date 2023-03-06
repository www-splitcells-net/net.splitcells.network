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
package net.splitcells.gel.constraint;

import static net.splitcells.dem.data.set.map.typed.TypedMapI.typedMap;
import static net.splitcells.dem.lang.Xml.elementWithChildren;
import static net.splitcells.dem.lang.Xml.textNode;

import java.util.Optional;

import net.splitcells.dem.data.set.map.typed.TypedMapView;
import net.splitcells.dem.lang.Xml;
import net.splitcells.gel.data.table.Line;
import org.w3c.dom.Element;
import net.splitcells.dem.lang.dom.Domable;

/**
 * <p>TODO By default it should not be possible to create a {@link GroupId} without a given {@link GroupId}.
 * This makes it a lot easier, to avoid errors, when a {@link Line} is present in a {@link Constraint},
 * with multiple different {@link Constraint#INCOMING_CONSTRAINT_GROUP}.
 * Thereby, it can be easier to avoid the use of the same {@link Constraint#RESULTING_CONSTRAINT_GROUP}
 * for multiple occurrences of one {@link Line}.
 * Such a functionality, could also be used in order to optionally trace the creation of groups.</p>
 * <p>IDEA Implement GroupId#name as constraint path like in reasoning system.</p>
 * <p>IDEA Create value based grouping.</p>
 */
public class GroupId implements Domable {

    private static final String NOT_DESCRIBED_PARENT_GROUP = "not described parent group";

    public static GroupId rootGroup() {
        return new GroupId();
    }

    public static GroupId group(GroupId group) {
        return new GroupId(group.name().orElse(NOT_DESCRIBED_PARENT_GROUP));
    }

    public static GroupId rootGroup(String name) {
        return new GroupId(name);
    }

    public static GroupId group(GroupId parentGroup, String name) {
        return new GroupId(parentGroup.name().orElse(NOT_DESCRIBED_PARENT_GROUP) + " " + name);
    }

    public static GroupId group(GroupId parentGroup, String name, TypedMapView metaData) {
        return new GroupId(parentGroup.name().orElse(NOT_DESCRIBED_PARENT_GROUP) + " " + name, metaData);
    }

    @Deprecated
    public GroupId() {
        name = Optional.empty();
        metaData = typedMap();
    }

    private GroupId(String name) {
        this.name = Optional.ofNullable(name);
        metaData = typedMap();
    }

    private GroupId(String name, TypedMapView metaData) {
        this.name = Optional.ofNullable(name);
        this.metaData = metaData;
    }

    /**
     * TODO Move name inside {@link #metaData}
     */
    private final Optional<String> name;
    private final TypedMapView metaData;

    @Deprecated
    public String getName() {
        return name.get();
    }

    public Optional<String> name() {
        return name;
    }

    public static GroupId multiply(GroupId a, GroupId b) {
        return new GroupId(a.toString() + " and " + b.toString());
    }

    @Override
    public String toString() {
        return name.orElse(super.toString());
    }

    @Override
    public Element toDom() {
        final var dom = Xml.elementWithChildren("group");
        if (name.isPresent()) {
            dom.appendChild(Xml.elementWithChildren("name", textNode(name.get())));
        }
        dom.appendChild(Xml.elementWithChildren("id", textNode(this.hashCode() + "")));
        return dom;
    }

    public TypedMapView metaData() {
        return metaData;
    }
}
