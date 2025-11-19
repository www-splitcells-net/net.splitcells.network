/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.gel.editor.geal.runners;

import lombok.val;
import net.splitcells.dem.data.set.list.List;
import net.splitcells.gel.editor.Editor;
import net.splitcells.gel.editor.geal.lang.FunctionCallDesc;

import java.util.Optional;

import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.gel.editor.geal.runners.FunctionCallRun.functionCallRun;
import static net.splitcells.gel.editor.geal.runners.FunctionCallRunnerParser.functionCallRunnerParser;
import static net.splitcells.gel.rating.rater.lib.HasMaximalSize.hasMaximalSize;
import static net.splitcells.gel.rating.rater.lib.HasMinimalSize.hasMinimalSize;

public class MaxSizeCallRunner implements FunctionCallRunner {
    public static FunctionCallRunner maxSizeCallRunner() {
        return new MaxSizeCallRunner();
    }

    private static final String MAX_SIZE_NAME = "hasMaximumSize";

    private static class Args {
        int maximalSize;
    }

    private static final FunctionCallRunnerParser<Args> PARSER = functionCallRunnerParser(MAX_SIZE_NAME
            , 1
            , fcr -> {
                val args = new Args();
                fcr.requireSubjectAbsence();
                args.maximalSize = fcr.parseArgument(Integer.class, 0, "maximalSize");
                fcr.requireArgumentCount(1);
                return args;
            });

    private MaxSizeCallRunner() {

    }

    @Override public FunctionCallRun execute(FunctionCallDesc functionCall, Optional<Object> subject, Editor context) {
        final var run = functionCallRun(subject, context);
        if (!functionCall.getName().getValue().equals(MAX_SIZE_NAME) || subject.isPresent() || functionCall.getArguments().size() != 1) {
            return run;
        }
        val args = PARSER.parse(subject, context, functionCall);
        run.setResult(Optional.of(hasMaximalSize(args.maximalSize)));
        return run;
    }

    @Override public List<FunctionCallRunnerParser<?>> parsers() {
        return list(PARSER);
    }
}
