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

import net.splitcells.gel.constraint.Query;
import net.splitcells.gel.editor.Editor;
import net.splitcells.gel.editor.geal.lang.FunctionCallDesc;
import net.splitcells.gel.editor.geal.lang.NameDesc;
import net.splitcells.gel.solution.Solution;

import java.util.Optional;

import static net.splitcells.dem.utils.NotImplementedYet.notImplementedYet;
import static net.splitcells.gel.constraint.QueryI.query;
import static net.splitcells.gel.constraint.type.ForAlls.FOR_EACH_NAME;
import static net.splitcells.gel.editor.geal.runners.FunctionCallRun.functionCallRun;

public class ForEachCallRunner implements FunctionCallRunner {
    public static ForEachCallRunner forEachCallRunner() {
        return new ForEachCallRunner();
    }

    private ForEachCallRunner() {

    }

    private boolean supports(FunctionCallDesc functionCall, Optional<Object> subject, Editor context) {
        return subject.isPresent()
                && (subject.orElseThrow() instanceof Solution
                || subject.orElseThrow() instanceof Query)
                && functionCall.getName().getValue().equals(FOR_EACH_NAME)
                && functionCall.getArguments().size() == 1
                && functionCall.getArguments().get(0).getExpression() instanceof FunctionCallDesc;
    }

    @Override
    public FunctionCallRun execute(FunctionCallDesc functionCall, Optional<Object> subject, Editor context) {
        final var run = functionCallRun(subject, context);
        if (!supports(functionCall, subject, context)) {
            return run;
        }
        final var groupingName = ((FunctionCallDesc) functionCall.getArguments().get(0).getExpression()).getName();
        final var groupingAttribute = context.getAttributes().get(groupingName.getValue());
        final Query subjectVal;
        if (subject.orElseThrow() instanceof Solution solution) {
            subjectVal = query(solution.constraint());
        } else if (subject.orElseThrow() instanceof Query query) {
            subjectVal = query;
        } else {
            throw notImplementedYet();
        }
        run.setResult(Optional.of(subjectVal.forAll(groupingAttribute)));
        return run;
    }
}
