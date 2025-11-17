/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.gel.editor.geal.runners;

import lombok.val;
import net.splitcells.dem.data.set.list.List;
import net.splitcells.gel.data.view.attribute.Attribute;
import net.splitcells.gel.editor.Editor;
import net.splitcells.gel.editor.geal.lang.FunctionCallDesc;

import java.util.Optional;

import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.gel.editor.geal.runners.FunctionCallRun.functionCallRun;
import static net.splitcells.gel.editor.geal.runners.FunctionCallRunnerParser.functionCallRunnerParser;
import static net.splitcells.gel.rating.rater.lib.RaterBasedOnLineValue.lineValueRater;

public class EqualsRunner implements FunctionCallRunner {
    public static FunctionCallRunner equalsRunner() {
        return new EqualsRunner();
    }

    private static final String EQUALS_NAME = "equals";


    private static class Args {
        Attribute<?> attribute;
        Object targetValue;
    }

    private static final FunctionCallRunnerParser<Args> PARSER = functionCallRunnerParser(EQUALS_NAME
            , 1
            , fcr -> {
                val args = new Args();
                fcr.requireSubjectAbsence();
                args.attribute = fcr.parseAttributeArgument(0, "attribute");
                args.targetValue = fcr.parseArgument(Object.class, 1, "targetValue");
                fcr.requireArgumentCount(2);
                return args;
            });


    private EqualsRunner() {
    }

    @Override public FunctionCallRun execute(FunctionCallDesc functionCall, Optional<Object> subject, Editor context) {
        val run = functionCallRun(subject, context);
        if (!functionCall.getName().getValue().equals(EQUALS_NAME)
                || subject.isPresent()
                || functionCall.getArguments().size() != 2) {
            return run;
        }
        val args = PARSER.parse(subject, context, functionCall);
        val equalityRater = lineValueRater(line -> args.targetValue.equals(line.value(args.attribute))
                , "Require " + args.attribute.name() + " to be equals to " + args.targetValue);
        run.setResult(Optional.of(equalityRater));
        return run;
    }

    @Override public List<FunctionCallRunnerParser<?>> parsers() {
        return list(PARSER);
    }
}
