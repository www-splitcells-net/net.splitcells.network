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
package net.splitcells.gel.editor.geal.parser;

import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.source.geal.GealParser;
import net.splitcells.gel.editor.geal.lang.FunctionCallChainDesc;
import net.splitcells.gel.editor.geal.lang.FunctionCallDesc;

import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.gel.editor.geal.lang.FunctionCallDesc.functionCallDesc;
import static net.splitcells.gel.editor.geal.lang.NameDesc.nameDesc;
import static net.splitcells.gel.editor.geal.parser.FunctionCallChainParser.parseFunctionCallChain;
import static net.splitcells.gel.editor.lang.SourceCodeQuote.sourceCodeQuote;

public class FunctionCallParser extends net.splitcells.dem.source.geal.GealParserBaseVisitor<FunctionCallDesc> {
    public static FunctionCallDesc parseFunctionCall(GealParser.Function_callContext arg) {
        return new FunctionCallParser().visitFunction_call(arg);
    }

    private FunctionCallParser() {

    }

    @Override public FunctionCallDesc visitFunction_call(GealParser.Function_callContext ctx) {
        final var name = nameDesc(ctx.Name().getText(), sourceCodeQuote(ctx.Name()));
        final FunctionCallDesc baseCall;
        if (ctx.function_call_arguments() == null) {
            baseCall = functionCallDesc(name, list(), sourceCodeQuote(ctx));
        } else {
            final List<FunctionCallChainDesc> arguments = list();
            arguments.add(parseFunctionCallChain(ctx.function_call_arguments().function_call_chain()));
            final var secondaryArgs = ctx.function_call_arguments().function_call_arguments_next();
            if (secondaryArgs != null) {
                secondaryArgs.forEach(a -> arguments.add(parseFunctionCallChain(a.function_call_chain())));
            }
            baseCall = functionCallDesc(name, arguments, sourceCodeQuote(ctx));
        }
        return baseCall;
    }
}
