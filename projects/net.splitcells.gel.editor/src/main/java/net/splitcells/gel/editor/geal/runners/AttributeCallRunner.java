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
package net.splitcells.gel.editor.geal.runners;

import net.splitcells.gel.editor.Editor;
import net.splitcells.gel.editor.geal.lang.FunctionCallDesc;
import net.splitcells.gel.editor.geal.lang.NameDesc;
import net.splitcells.gel.editor.geal.lang.StringDesc;

import java.util.Optional;

import static net.splitcells.dem.lang.tree.TreeI.tree;
import static net.splitcells.dem.utils.ExecutionException.execException;
import static net.splitcells.dem.utils.NotImplementedYet.notImplementedYet;
import static net.splitcells.gel.data.view.attribute.AttributeI.integerAttribute;
import static net.splitcells.gel.data.view.attribute.AttributeI.stringAttribute;
import static net.splitcells.gel.editor.EditorParser.*;
import static net.splitcells.gel.editor.geal.runners.FunctionCallRun.functionCallRun;

public class AttributeCallRunner implements FunctionCallRunner {
    public static AttributeCallRunner attributeCallRunner() {
        return new AttributeCallRunner();
    }

    private AttributeCallRunner() {

    }

    private boolean supports(FunctionCallDesc functionCall) {
        return functionCall.getName().getValue().equals(ATTRIBUTE_FUNCTION);
    }

    /**
     *
     * @param functionCall The second argument is a {@link StringDesc} instead of an {@link NameDesc} as it is not a variable reference and
     *                     because the attribute's name is used for the UI, where using whitespace can make a lot of sense for users.
     * @return
     */
    @Override
    public FunctionCallRun execute(FunctionCallDesc functionCall, Optional<Object> subject, Editor context) {
        final var run = functionCallRun(subject, context);
        if (!supports(functionCall)) {
            return run;
        }
        if (functionCall.getArguments().size() != 2) {
            throw execException("The attribute function requires exactly 2 arguments, but " + functionCall.getArguments().size() + " were given.");
        }
        final var first = functionCall.getArguments().get(0).getExpression();
        final NameDesc firstName;
        switch (first) {
            case FunctionCallDesc n -> {
                if (n.getArguments().hasElements()) {
                    throw notImplementedYet();
                }
                firstName = NameDesc.nameDesc(n.getName().getValue(), n.getSourceCodeQuote());
            }
            default ->
                    throw execException("The first argument has to be a name, but " + first.getClass() + " was given.");
        }
        final var second = functionCall.getArguments().get(1).getExpression();
        final StringDesc secondName;
        switch (second) {
            case StringDesc n -> secondName = n;
            default ->
                    throw execException("The second argument has to be a string, but " + second.getClass() + " was given.");
        }
        final Optional<Object> result;
        if (firstName.getValue().equals(INTEGER_TYPE)) {
            result = Optional.of(integerAttribute(secondName.getValue()));
        } else if (firstName.getValue().equals(STRING_TYPE)) {
            result = Optional.of(stringAttribute(secondName.getValue()));
        } else {
            throw execException("The first argument has to be a reference to the integer or the string type, but " + firstName.getValue() + " was given.");
        }
        return run.setResult(result);
    }
}
