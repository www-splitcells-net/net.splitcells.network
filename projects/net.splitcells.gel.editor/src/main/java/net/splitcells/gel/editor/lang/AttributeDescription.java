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

import net.splitcells.dem.lang.tree.Tree;
import net.splitcells.dem.testing.Result;
import net.splitcells.gel.data.view.attribute.Attribute;

import static net.splitcells.dem.lang.tree.TreeI.tree;
import static net.splitcells.dem.testing.Result.result;
import static net.splitcells.gel.data.view.attribute.AttributeI.*;

public class AttributeDescription {
    public static Result<AttributeDescription, Tree> parseAttributeDescription(String name, String type) {
        final Result<AttributeDescription, Tree> attribute = result();
        final var primitiveType = PrimitiveType.parse(type);
        if (primitiveType.isEmpty()) {
            return attribute.withErrorMessage(tree("Unknown attribute type.")
                    .withProperty("name", name)
                    .withProperty("type", type));
        }
        return attribute.withValue(attributeDescription(name, primitiveType.get()));
    }

    public static AttributeDescription attributeDescription(String name, PrimitiveType primitiveType) {
        return new AttributeDescription(name, primitiveType);
    }

    private final String name;
    private final PrimitiveType primitiveType;

    private AttributeDescription(String argName, PrimitiveType argPrimitiveType) {
        name = argName;
        primitiveType = argPrimitiveType;
    }

    public String name() {
        return name;
    }

    public PrimitiveType primitiveType() {
        return primitiveType;
    }
}
