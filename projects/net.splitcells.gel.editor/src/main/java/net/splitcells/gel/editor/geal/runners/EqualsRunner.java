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
import static net.splitcells.dem.lang.namespace.NameSpaces.SEW;
import static net.splitcells.dem.lang.tree.TreeI.tree;
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
        Attribute<?> targetAttribute;
    }


    private static final FunctionCallRunnerParser<Args> PARSER_1 = functionCallRunnerParser(EQUALS_NAME
            , 1
            , fcr -> {
                val args = new Args();
                fcr.requireSubjectAbsence();
                args.attribute = fcr.parseAttributeArgument(0, "attribute");
                args.targetAttribute = fcr.parseAttributeArgument(1, "targetAttribute");
                fcr.requireArgumentCount(2);
                fcr.addDescription(tree("paragraph", SEW)
                        .withText("Rates a given line with no cost, if $attribute and $targetAttribute of the line have the same value."));
                return args;
            });

    private static final FunctionCallRunnerParser<Args> PARSER_2 = functionCallRunnerParser(EQUALS_NAME
            , 2
            , fcr -> {
                val args = new Args();
                fcr.requireSubjectAbsence();
                args.attribute = fcr.parseAttributeArgument(0, "attribute");
                args.targetValue = fcr.parseArgument(Object.class, 1, "targetValue");
                fcr.requireArgumentCount(2);
                fcr.addDescription(tree("paragraph", SEW)
                        .withText("Rates a given line with no cost, if the line's $attribute value is equals to $targetValue."));
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
        final var secondArg = context.parse(functionCall.getArguments().get(1));
        final Args args;
        final Rater equalityRater;
        if (secondArg instanceof Attribute<?> targetAttribute) {
            args = PARSER_1.parse(subject, context, functionCall);
            equalityRater = lineValueRater(line -> line.value(args.attribute).equals(line.value(args.targetAttribute))
                    , "Require " + args.attribute.descriptivePathName() + " to be equals to the attribute " + args.targetAttribute.descriptivePathName()
                    , args.attribute.descriptivePathName() + "-equals-" + args.targetAttribute.descriptivePathName());
        } else {
            args = PARSER_2.parse(subject, context, functionCall);
            equalityRater = lineValueRater(line -> args.targetValue.equals(line.value(args.attribute))
                    , "Require " + args.attribute.descriptivePathName() + " to be equals to " + args.targetValue
                    , args.attribute.descriptivePathName() + "-equals-value-" + args.targetValue);
        }
        run.setResult(Optional.of(equalityRater));
        return run;
    }

    @Override public List<FunctionCallRunnerParser<?>> parsers() {
        return list(PARSER_1, PARSER_2);
    }
}
