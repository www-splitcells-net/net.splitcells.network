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
package net.splitcells.gel.editor.lang;

import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.data.set.map.Map;
import net.splitcells.gel.data.view.attribute.Attribute;

import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.data.set.map.Maps.map;

public class SolutionDescription {
    public static SolutionDescription solutionDescription(String name
            , List<AttributeDescription> attributes
            , TableDescription demands
            , TableDescription supplies
            , List<ConstraintDescription> constraints) {
        return new SolutionDescription(name, attributes, demands, supplies, constraints);
    }

    private final String name;
    /**
     * TODO The {@link Attribute} lookup should be provided by the editor.
     */
    private final Map<String, AttributeDescription> attributes = map();
    private final TableDescription demands;
    private final TableDescription supplies;
    private final List<ConstraintDescription> constraints;
    private List<ReferenceDescription<AttributeDescription>> columnAttributesForOutputFormat = list();
    private List<ReferenceDescription<AttributeDescription>> rowAttributesForOutputFormat = list();

    private SolutionDescription(String argName
            , List<AttributeDescription> argAttributes
            , TableDescription argDemands
            , TableDescription argSupplies
            , List<ConstraintDescription> argConstraints) {
        name = argName;
        argAttributes.forEach(a -> attributes.put(a.name(), a));
        demands = argDemands;
        supplies = argSupplies;
        constraints = argConstraints;
    }

    /**
     * TODO The returned value, is not allowed to be changed. Create a ready-only interface for this.
     *
     * @return
     */
    public Map<String, AttributeDescription> attributes() {
        return attributes;
    }

    public TableDescription demands() {
        return demands;
    }

    public TableDescription supplies() {
        return supplies;
    }

    public String name() {
        return name;
    }

    public List<ConstraintDescription> constraints() {
        return constraints;
    }

    public List<ReferenceDescription<AttributeDescription>> columnAttributesForOutputFormat() {
        return columnAttributesForOutputFormat;
    }
    public List<ReferenceDescription<AttributeDescription>> rowAttributesForOutputFormat() {
        return rowAttributesForOutputFormat;
    }
}
