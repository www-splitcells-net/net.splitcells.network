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
package net.splitcells.gel.editor.solution;

import net.splitcells.dem.data.set.map.Map;
import net.splitcells.gel.data.view.attribute.Attribute;
import net.splitcells.gel.editor.lang.SolutionDescription;

import static net.splitcells.dem.data.set.map.Maps.map;
import static net.splitcells.dem.utils.ExecutionException.execException;
import static net.splitcells.gel.data.view.attribute.AttributeI.integerAttribute;
import static net.splitcells.gel.data.view.attribute.AttributeI.stringAttribute;
import static net.splitcells.gel.editor.lang.PrimitiveType.INTEGER;
import static net.splitcells.gel.editor.lang.PrimitiveType.STRING;

public class SolutionEditor {
    public static SolutionEditor solutionEditor(String name) {
        return new SolutionEditor(name);
    }

    private final String name;
    private final Map<String, Attribute<? extends Object>> attributes = map();

    private SolutionEditor(String argName) {
        name = argName;
    }

    public SolutionEditor parse(SolutionDescription solutionDescription) {
        solutionDescription.attributeDescriptions().entrySet().forEach(ad -> {
            final var attributeDesc = ad.getValue();
            final Attribute<? extends Object> attribute;
            if (INTEGER.equals(attributeDesc.primitiveType())) {
                attribute = integerAttribute(attributeDesc.name());
            } else if (STRING.equals(attributeDesc.primitiveType())) {
                attribute = stringAttribute(attributeDesc.name());
            } else {
                throw execException();
            }
            attributes.put(attributeDesc.name(), attribute);
        });
        return this;
    }

    public Map<String, Attribute<? extends Object>> attributes() {
        return attributes;
    }

    public String name() {
        return name;
    }
}
