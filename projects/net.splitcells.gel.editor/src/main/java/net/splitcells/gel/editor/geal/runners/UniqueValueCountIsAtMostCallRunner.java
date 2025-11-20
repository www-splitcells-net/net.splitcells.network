/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.gel.editor.geal.runners;

import lombok.val;
import net.splitcells.dem.data.set.list.List;
import net.splitcells.gel.data.view.attribute.Attribute;
import net.splitcells.gel.editor.Editor;
import net.splitcells.gel.editor.geal.lang.FunctionCallDesc;
import net.splitcells.gel.rating.rater.framework.Rater;

import java.util.Optional;

import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.gel.editor.geal.runners.FunctionCallRun.functionCallRun;
import static net.splitcells.gel.editor.geal.runners.FunctionCallRunnerParser.functionCallRunnerParser;
import static net.splitcells.gel.rating.rater.lib.Not.not;
import static net.splitcells.gel.rating.rater.lib.UniqueValueCountIsAtMost.uniqueValueCountIsAtMost;

public class UniqueValueCountIsAtMostCallRunner implements FunctionCallRunner {
    public static FunctionCallRunner uniqueValueCountIsAtMostCallRunner() {
        return new UniqueValueCountIsAtMostCallRunner();
    }

    private static final String NAME = "uniqueValueCountIsAtMost";

    private static class Args {
        Integer maxCount;
        Attribute<? extends Object> attribute;
    }

    private static final FunctionCallRunnerParser<Args> PARSER = functionCallRunnerParser(NAME
            , 1
            , fcr -> {
                val args = new Args();
                fcr.requireSubjectAbsence();
                args.maxCount = fcr.parseArgument(Integer.class, 0, "maxCount");
                args.attribute = fcr.parseAttributeArgument(1, "attribute");
                return args;
            });

    private UniqueValueCountIsAtMostCallRunner() {
    }

    @Override public FunctionCallRun execute(FunctionCallDesc functionCall, Optional<Object> subject, Editor context) {
        final var run = functionCallRun(subject, context);
        if (!functionCall.getName().getValue().equals(NAME) || subject.isPresent() || functionCall.getArguments().size() != 2) {
            return run;
        }
        val args = PARSER.parse(subject, context, functionCall);
        run.setResult(Optional.of(uniqueValueCountIsAtMost(args.maxCount, args.attribute)));
        return run;
    }

    @Override public List<FunctionCallRunnerParser<?>> parsers() {
        return list(PARSER);
    }
}
