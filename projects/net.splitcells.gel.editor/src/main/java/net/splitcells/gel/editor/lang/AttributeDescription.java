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

import net.splitcells.dem.data.atom.Thing;
import net.splitcells.dem.lang.tree.Tree;
import net.splitcells.dem.testing.Result;
import net.splitcells.gel.editor.geal.lang.SourceUnit;

import static net.splitcells.dem.lang.tree.TreeI.tree;
import static net.splitcells.dem.testing.Result.result;
import static net.splitcells.gel.editor.SolutionEditor.AFFECTED_CONTENT;

/**
 * @deprecated Use {@link SourceUnit} instead.
 */
@Deprecated
public class AttributeDescription implements SourceCodeQuotation {
    public static Result<AttributeDescription, Tree> parseAttributeDescription(String name, String type, SourceCodeQuote sourceCodeQuote) {
        final Result<AttributeDescription, Tree> attribute = result();
        final var primitiveType = PrimitiveType.parse(type);
        if (primitiveType.isEmpty()) {
            return attribute.withErrorMessage(tree("Unknown attribute type.")
                    .withProperty("name", name)
                    .withProperty("type", type)
                    .withProperty(AFFECTED_CONTENT, sourceCodeQuote.userReference()));
        }
        return attribute.withValue(attributeDescription(name, primitiveType.get(), sourceCodeQuote));
    }

    public static AttributeDescription attributeDescription(String name, PrimitiveType primitiveType, SourceCodeQuote sourceCodeQuote) {
        return new AttributeDescription(name, primitiveType, sourceCodeQuote);
    }

    private final String name;
    private final PrimitiveType primitiveType;
    private final SourceCodeQuote sourceCodeQuote;

    private AttributeDescription(String argName, PrimitiveType argPrimitiveType, SourceCodeQuote argSourceCodeQuote) {
        name = argName;
        primitiveType = argPrimitiveType;
        sourceCodeQuote = argSourceCodeQuote;
    }

    public String name() {
        return name;
    }

    public PrimitiveType primitiveType() {
        return primitiveType;
    }


    @Override
    public boolean equals(Object arg) {
        if (arg instanceof AttributeDescription other) {
            return name.equals(other.name()) && primitiveType.equals(other.primitiveType()) && sourceCodeQuote.equals(other.getSourceCodeQuote());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Thing.hashCode(name, primitiveType, sourceCodeQuote);
    }

    @Override
    public SourceCodeQuote getSourceCodeQuote() {
        return sourceCodeQuote;
    }

    @Override
    public String toString() {
        return "name: " + name + ", primitiveType: " + primitiveType + ", sourceCodeQuote: " + sourceCodeQuote;
    }
}
