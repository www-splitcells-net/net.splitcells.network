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

import net.splitcells.dem.data.set.list.List;
import net.splitcells.gel.constraint.Query;
import net.splitcells.gel.data.view.attribute.Attribute;
import net.splitcells.gel.editor.Editor;
import net.splitcells.gel.editor.geal.lang.FunctionCallDesc;
import net.splitcells.gel.rating.rater.framework.Rater;
import net.splitcells.gel.solution.Solution;

import java.util.Optional;

import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.utils.ExecutionException.execException;
import static net.splitcells.gel.constraint.QueryI.query;
import static net.splitcells.gel.editor.TableFormatting.tableFormat;
import static net.splitcells.gel.editor.geal.runners.FunctionCallRun.functionCallRun;

public class OutputFormatCallRunner implements FunctionCallRunner {
    public static OutputFormatCallRunner outputFormatCallRunner() {
        return new OutputFormatCallRunner();
    }

    private static final String COLUMN_FORMAT = "columnAttributesForOutputFormat";
    private static final String ROW_FORMAT = "rowAttributesForOutputFormat";

    private OutputFormatCallRunner() {

    }

    private boolean supports(FunctionCallDesc functionCall, Optional<Object> subject, Editor context) {
        return subject.isPresent()
                && subject.orElseThrow() instanceof Solution
                && (functionCall.getName().getValue().equals(COLUMN_FORMAT) || functionCall.getName().getValue().equals(ROW_FORMAT))
                && functionCall.getArguments().stream().map(arg -> arg.getExpression() instanceof FunctionCallDesc)
                .reduce(true, (a, b) -> a && b);
    }

    @Override
    public FunctionCallRun execute(FunctionCallDesc functionCall, Optional<Object> subject, Editor context) {
        final var run = functionCallRun(subject, context);
        if (!supports(functionCall, subject, context)) {
            return run;
        }
        final Solution subjectVal;
        switch (subject.orElseThrow()) {
            case Solution s -> subjectVal = s;
            default -> throw execException("Subject has to be a solution: " + subject.orElseThrow());
        }
        final List<Attribute<?>> attributes = list();
        functionCall.getArguments().forEach(arg -> {
            final var attribute = context.parse(arg);
            switch (attribute) {
                case Attribute<?> a -> {
                    attributes.add(a);
                    run.setResult(Optional.of(subjectVal));
                }
                default -> throw execException("" + attribute);
            }
        });
        final var subjectKey = context.lookupTableLikeName(subjectVal);
        if (functionCall.getName().getValue().equals(COLUMN_FORMAT)) {
            if (context.getTableFormatting().hasKey(subjectKey)) {
                context.getTableFormatting().get(subjectKey).setColumnAttributes(attributes);
            } else {
                context.getTableFormatting().put(subjectKey, tableFormat().setColumnAttributes(attributes));
            }
        } else if (functionCall.getName().getValue().equals(ROW_FORMAT)) {
            if (context.getTableFormatting().hasKey(subjectKey)) {
                context.getTableFormatting().get(subjectKey).setRowAttributes(attributes);
            } else {
                context.getTableFormatting().put(subjectKey, tableFormat().setRowAttributes(attributes));
            }
        }
        return run;
    }
}
