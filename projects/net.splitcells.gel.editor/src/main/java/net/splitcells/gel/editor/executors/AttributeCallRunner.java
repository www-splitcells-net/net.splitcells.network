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
package net.splitcells.gel.editor.executors;

import lombok.Getter;
import lombok.Setter;
import net.splitcells.gel.editor.Editor;
import net.splitcells.gel.editor.lang.geal.FunctionCallDesc;
import net.splitcells.gel.editor.lang.geal.NameDesc;

import java.util.Optional;

import static net.splitcells.dem.data.atom.Bools.require;
import static net.splitcells.dem.lang.tree.TreeI.tree;
import static net.splitcells.dem.utils.ExecutionException.execException;
import static net.splitcells.dem.utils.NotImplementedYet.notImplementedYet;
import static net.splitcells.gel.data.view.attribute.AttributeI.integerAttribute;
import static net.splitcells.gel.data.view.attribute.AttributeI.stringAttribute;
import static net.splitcells.gel.editor.EditorParser.*;

public class AttributeCallRunner implements FunctionCallRunner {
    public static AttributeCallRunner attributeExecutor() {
        return new AttributeCallRunner();
    }

    private @Setter Optional<Editor> context = Optional.empty();
    private @Setter Optional<Object> subject = Optional.empty();
    private @Getter Optional<Object> result = Optional.empty();

    private AttributeCallRunner() {

    }

    @Override
    public boolean supports(FunctionCallDesc functionCall) {
        return functionCall.getName().getValue().equals(ATTRIBUTE_FUNCTION);
    }

    @Override
    public FunctionCallRunner execute(FunctionCallDesc functionCall) {
        require(supports(functionCall));
        if (functionCall.getArguments().size() != 2) {
            throw execException("The attribute function requires exactly 2 arguments, but " + functionCall.getArguments().size() + " where given.");
        }
        final var first = functionCall.getArguments().get(0);
        final NameDesc firstName;
        switch (first) {
            case NameDesc n -> firstName = n;
            default ->
                    throw execException("The first argument has to be a name, but " + first.getClass() + " was given.");
        }
        final var second = functionCall.getArguments().get(1);
        final NameDesc secondName;
        switch (second) {
            case NameDesc n -> secondName = n;
            default ->
                    throw execException("The second argument has to be a name, but " + first.getClass() + " was given.");
        }
        if (firstName.getValue().equals(INTEGER_TYPE)) {
            result = Optional.of(integerAttribute(secondName.getValue()));
        } else if (firstName.getValue().equals(STRING_TYPE)) {
            result = Optional.of(stringAttribute(secondName.getValue()));
        } else {
            throw execException("The first argument has to be a reference to the integer or the string type, but " + firstName.getValue() + " was given.");
        }
        throw notImplementedYet();
    }

    @Override
    public FunctionCallRunner setSubject(Optional<Object> argSubject) {
        subject = argSubject;
        return this;
    }

    @Override
    public FunctionCallRunner setContext(Optional<Editor> argContext) {
        context = argContext;
        return this;
    }
}
