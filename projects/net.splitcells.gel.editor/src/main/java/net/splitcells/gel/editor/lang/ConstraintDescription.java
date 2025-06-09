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
import net.splitcells.dem.data.set.list.List;

import static net.splitcells.dem.data.set.list.Lists.list;

public class ConstraintDescription implements SourceCodeQuotation {
    public static ConstraintDescription constraintDescription(FunctionCallDescription definition, SourceCodeQuote sourceCodeQuote) {
        return new ConstraintDescription(definition, list(), sourceCodeQuote);
    }

    public static ConstraintDescription constraintDescription(FunctionCallDescription definition, List<ConstraintDescription> children, SourceCodeQuote sourceCodeQuote) {
        return new ConstraintDescription(definition, children, sourceCodeQuote);
    }

    private final FunctionCallDescription definition;
    private final List<ConstraintDescription> children;
    private final SourceCodeQuote sourceCodeQuote;

    private ConstraintDescription(FunctionCallDescription argDefinition, List<ConstraintDescription> argChildren, SourceCodeQuote argSourceCodeQuote) {
        children = argChildren;
        definition = argDefinition;
        sourceCodeQuote = argSourceCodeQuote;
    }

    public List<ConstraintDescription> children() {
        return children;
    }

    public ConstraintDescription withChild(ConstraintDescription child) {
        children.add(child);
        return this;
    }

    public FunctionCallDescription definition() {
        return definition;
    }

    @Override
    public String toString() {
        return "definition: " + definition.toString() + ", children: " + children + ", sourceCodeQuote: " + sourceCodeQuote;
    }

    @Override
    public SourceCodeQuote getSourceCodeQuote() {
        return sourceCodeQuote;
    }

    @Override
    public boolean equals(Object arg) {
        if (arg instanceof ConstraintDescription other) {
            return definition.equals(other.definition()) && children.equals(other.children()) && sourceCodeQuote.equals(other.getSourceCodeQuote());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Thing.hashCode(definition, children, sourceCodeQuote);
    }
}
