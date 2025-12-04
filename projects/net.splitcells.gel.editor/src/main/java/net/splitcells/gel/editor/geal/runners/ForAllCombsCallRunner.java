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
import net.splitcells.gel.constraint.Query;
import net.splitcells.gel.data.view.attribute.Attribute;
import net.splitcells.gel.editor.Editor;
import net.splitcells.gel.editor.geal.lang.FunctionCallDesc;
import net.splitcells.gel.editor.geal.lang.NameDesc;

import java.util.Optional;

import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.data.set.list.Lists.toList;
import static net.splitcells.dem.lang.namespace.NameSpaces.SEW;
import static net.splitcells.dem.lang.tree.TreeI.tree;
import static net.splitcells.dem.utils.ExecutionException.execException;
import static net.splitcells.dem.utils.NotImplementedYet.notImplementedYet;
import static net.splitcells.gel.constraint.QueryI.query;
import static net.splitcells.gel.constraint.type.ForAlls.FOR_ALL_COMBINATIONS_OF;
import static net.splitcells.gel.constraint.type.ForAlls.FOR_EACH_NAME;
import static net.splitcells.gel.editor.geal.runners.FunctionCallRun.functionCallRun;
import static net.splitcells.gel.editor.geal.runners.FunctionCallRunnerParser.functionCallRunnerParser;

public class ForAllCombsCallRunner implements FunctionCallRunner {
    public static ForAllCombsCallRunner forAllCombsCallRunner() {
        return new ForAllCombsCallRunner();
    }

    private ForAllCombsCallRunner() {

    }

    private static class Args {
        List<Attribute<? extends Object>> groupingAttributes;
        Query subjectVal;
    }

    /**
     * The variation is 2 as {@link ForEachAttributeCallRunner} is already using 1.
     */
    private static final FunctionCallRunnerParser<Args> PARSER = functionCallRunnerParser(FOR_EACH_NAME
            , 2
            , fcr -> {
                val args = new Args();
                fcr.requireArgumentMinimalCount(2);
                args.groupingAttributes = fcr.parseAttributeArguments("groupingAttributes");
                args.subjectVal = fcr.parseQuerySubject();
                fcr.addDescription(tree("paragraph", SEW).withText("Creates and returns a constraint node, that groups incoming lines by the values of the given attributes. The constraint node is added to the receiver, which is the parent constraint."));
                return args;
            });

    private boolean supports(FunctionCallDesc functionCall, Optional<Object> subject, Editor context) {
        return functionCall.getName().getValue().equals(FOR_EACH_NAME);
    }

    @Override
    public FunctionCallRun execute(FunctionCallDesc functionCall, Optional<Object> subject, Editor context) {
        final var run = functionCallRun(subject, context);
        if (!supports(functionCall, subject, context)) {
            return run;
        }
        val args = PARSER.parse(subject, context, functionCall);
        run.setResult(Optional.of(args.subjectVal.forAllCombinationsOf(args.groupingAttributes)));
        return run;
    }

    @Override
    public List<FunctionCallRunnerParser<?>> parsers() {
        return list(PARSER);
    }
}
