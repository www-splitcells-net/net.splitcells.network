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

import lombok.val;
import net.splitcells.dem.data.set.list.List;
import net.splitcells.gel.editor.Editor;
import net.splitcells.gel.editor.geal.FunctionCallRecord;
import net.splitcells.gel.editor.geal.lang.FunctionCallDesc;
import net.splitcells.gel.editor.geal.lang.NameDesc;
import net.splitcells.gel.editor.geal.lang.StringDesc;

import java.util.Optional;
import java.util.function.Function;

import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.lang.tree.TreeI.tree;
import static net.splitcells.dem.utils.ExecutionException.execException;
import static net.splitcells.dem.utils.NotImplementedYet.notImplementedYet;
import static net.splitcells.gel.data.view.attribute.AttributeI.integerAttribute;
import static net.splitcells.gel.data.view.attribute.AttributeI.stringAttribute;
import static net.splitcells.gel.editor.EditorParser.*;
import static net.splitcells.gel.editor.geal.runners.FunctionCallRun.functionCallRun;
import static net.splitcells.gel.editor.geal.runners.FunctionCallRunnerParser.functionCallRunnerParser;

public class AttributeCallRunner implements FunctionCallRunner {
    public static AttributeCallRunner attributeCallRunner() {
        return new AttributeCallRunner();
    }

    private AttributeCallRunner() {

    }

    private boolean supports(FunctionCallDesc functionCall) {
        return functionCall.getName().getValue().equals(ATTRIBUTE_FUNCTION);
    }

    private static class AttributeFunctionArgs {
        NameDesc type;
        String name;
    }

    private static final FunctionCallRunnerParser<AttributeFunctionArgs> PARSER = functionCallRunnerParser(fcr -> {
        val args = new AttributeFunctionArgs();
        fcr.requireArgumentCount(2);
        args.type = fcr.parseArgumentAsType(0, INTEGER_TYPE, STRING_TYPE);
        args.name = fcr.parseArgumentAsStringDesc(1).getValue();
        return args;
    });


    /**
     *
     * @param functionCall The second argument is a {@link StringDesc} instead of an {@link NameDesc} as it is not a variable reference and
     *                     because the attribute's name is used for the UI, where using whitespace can make a lot of sense for users.
     * @return
     */
    @Override
    public FunctionCallRun execute(FunctionCallDesc functionCall, Optional<Object> subject, Editor context) {
        val run = functionCallRun(subject, context);
        if (!supports(functionCall)) {
            return run;
        }
        val args = PARSER.parse(subject, context, functionCall, 1);
        final Optional<Object> result;
        if (args.type.getValue().equals(INTEGER_TYPE)) {
            result = Optional.of(integerAttribute(args.name));
        } else if (args.type.getValue().equals(STRING_TYPE)) {
            result = Optional.of(stringAttribute(args.name));
        } else {
            throw execException();
        }
        return run.setResult(result);
    }

    @Override
    public List<FunctionCallRunnerParser<?>> parsers() {
        return list(PARSER);
    }
}
