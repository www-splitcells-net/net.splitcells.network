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
import net.splitcells.gel.editor.geal.lang.FunctionCallDesc;

import java.util.Optional;

import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.lang.namespace.NameSpaces.SEW;
import static net.splitcells.dem.lang.tree.TreeI.tree;
import static net.splitcells.gel.editor.geal.runners.FunctionCallRun.functionCallRun;
import static net.splitcells.gel.editor.geal.runners.FunctionCallRunnerParser.functionCallRunnerParser;
import static net.splitcells.gel.rating.rater.lib.HasSizeFast.hasSizeFast;

public class HasSizeFastCallRunner implements FunctionCallRunner {
    public static final String HAS_SIZE_FAST_NAME = "hasSizeFast";

    public static HasSizeFastCallRunner hasSizeFastCallRunner() {
        return new HasSizeFastCallRunner();
    }

    private static class Args {
        Integer targetSize;
    }

    private static final FunctionCallRunnerParser<Args> PARSER = functionCallRunnerParser(HAS_SIZE_FAST_NAME, 1
            , fcr -> {
                val args = new Args();
                fcr.requireSubjectAbsence();
                fcr.requireArgumentCount(1);
                args.targetSize = fcr.parseArgument(Integer.class, 0, "targetSize");
                fcr.addDescription(tree("paragraph", SEW).withText("""
                        Creates a rater, that requires a given line group to have at least as many lines as the given argument.
                        
                        This is basically a quite faster version of hasSize,
                        which gets its speed by assigning costs to only one line in the group.
                        This is an incorrect distribution of cost and such a constraint, should not have child constraints."""));
                return args;
            });

    private HasSizeFastCallRunner() {

    }

    private boolean supports(FunctionCallDesc functionCall, Optional<Object> subject, Editor context) {
        return functionCall.getName().getValue().equals(HAS_SIZE_FAST_NAME);
    }

    @Override
    public FunctionCallRun execute(FunctionCallDesc functionCall, Optional<Object> subject, Editor context) {
        final var run = functionCallRun(subject, context);
        if (!supports(functionCall, subject, context)) {
            return run;
        }
        val args = PARSER.parse(subject, context, functionCall);
        run.setResult(Optional.of(hasSizeFast(args.targetSize)));
        return run;
    }

    @Override
    public List<FunctionCallRunnerParser<?>> parsers() {
        return list(PARSER);
    }
}
