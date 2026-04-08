/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.gel.editor.geal.runners;

import lombok.val;
import net.splitcells.dem.data.set.list.List;
import net.splitcells.gel.constraint.Query;
import net.splitcells.gel.editor.Editor;
import net.splitcells.gel.editor.geal.lang.FunctionCallDesc;
import net.splitcells.gel.rating.rater.framework.Rater;

import java.util.Optional;

import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.lang.namespace.NameSpaces.SEW;
import static net.splitcells.dem.lang.tree.TreeI.tree;
import static net.splitcells.gel.editor.geal.runners.FunctionCallRun.functionCallRun;
import static net.splitcells.gel.editor.geal.runners.FunctionCallRunnerParser.functionCallRunnerParser;
import static net.splitcells.gel.rating.rater.lib.classification.ThenAtLeastFastRater.thenAtLeastFastRater;

public class ThenAtLeastFastRunner implements FunctionCallRunner {
    public static ThenAtLeastFastRunner thenAtLeastFastRunner() {
        return new ThenAtLeastFastRunner();
    }

    private static final String NAME = "thenAtLeastFast";
    private static final String DESCRIPTION = """
            Creates and returns a constraint node, where groups are rated with no cost,
            if $atLeastCount number of lines comply with the given $rater.
            If this is not the case, the cost of the group is the difference between $atLeastCount
            and the number of lines, that comply with $rater.
            
            This is basically a quite faster version of thenAtLeast,
            which gets its speed by assigning costs to only one line in the group.
            This is an incorrect distribution of cost and such a constraint, should not have child constraints.""";

    private static class Args {
        Query subjectVal;
        Integer atLeastCount;
        Rater rater;
    }

    private static final FunctionCallRunnerParser<Args> PARSER = functionCallRunnerParser(NAME, 1
            , fcr -> {
                val args = new Args();
                args.subjectVal = fcr.parseQuerySubject();
                fcr.requireArgumentCount(2);
                args.atLeastCount = fcr.parseArgument(Integer.class, 0, "atLeastCount");
                args.rater = fcr.parseArgument(Rater.class, 1, "rater");
                fcr.addDescription(tree("paragraph", SEW).withText(DESCRIPTION));
                return args;
            });

    private ThenAtLeastFastRunner() {

    }

    @Override public FunctionCallRun execute(FunctionCallDesc functionCall, Optional<Object> subject, Editor context) {
        val run = functionCallRun(subject, context);
        if (functionCall.getName().getValue().equals(NAME)) {
            val args = PARSER.parse(subject, context, functionCall);
            run.setResultVal(args.subjectVal.then(thenAtLeastFastRater(args.atLeastCount, args.rater)));
        }
        return run;
    }

    @Override public List<FunctionCallRunnerParser<?>> parsers() {
        return list(PARSER);
    }
}
