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
import net.splitcells.gel.data.view.attribute.Attribute;
import net.splitcells.gel.editor.Editor;
import net.splitcells.gel.editor.geal.lang.FunctionCallDesc;
import net.splitcells.gel.solution.Solution;

import java.util.Optional;

import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.lang.tree.TreeI.tree;
import static net.splitcells.dem.utils.ExecutionException.execException;
import static net.splitcells.gel.constraint.QueryI.query;
import static net.splitcells.gel.data.view.TableFormatting.tableFormat;
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
        return (functionCall.getName().getValue().equals(COLUMN_FORMAT) || functionCall.getName().getValue().equals(ROW_FORMAT));
    }

    @Override
    public FunctionCallRun execute(FunctionCallDesc functionCall, Optional<Object> subject, Editor context) {
        final var run = functionCallRun(subject, context);
        if (!supports(functionCall, subject, context)) {
            return run;
        }
        final Solution subjectVal;
        if (subject.isEmpty()) {
            throw execException(tree("The "
                    + functionCall.getName().getValue()
                    + " function requires a Solution as a subject, but no subject was given instead.")
                    .withChild(functionCall.getSourceCodeQuote().userReferenceTree()));
        }
        switch (subject.orElseThrow()) {
            case Solution s -> subjectVal = s;
            default -> throw execException(tree("The "
                    + functionCall.getName().getValue()
                    + " function requires a Solution as a subject, but "
                    + subject.orElseThrow().getClass().getName()
                    + " was given instead.")
                    .withChild(functionCall.getSourceCodeQuote().userReferenceTree()));
        }
        final List<Attribute<?>> attributes = list();
        functionCall.getArguments().forEachIndex((arg, i) -> {
            final var attribute = context.parse(arg);
            switch (attribute) {
                case Attribute<?> a -> {
                    attributes.add(a);
                    run.setResult(Optional.of(subjectVal));
                }
                default -> throw execException(tree("The "
                        + functionCall.getName().getValue()
                        + " function only supports attributes as arguments, but "
                        + subject.orElseThrow().getClass().getName()
                        + " was given as argument "
                        + i
                        + " instead.")
                        .withProperty("Affected function call ", functionCall.getSourceCodeQuote().userReferenceTree())
                        .withProperty("Affected argument", arg.getSourceCodeQuote().userReferenceTree()));
            }
        });
        final var subjectKey = context.lookupTableLikeName(subjectVal);
        if (subjectKey.isEmpty()) {
            throw execException(tree("Could not lookup table for " + functionCall.getName().getValue() + ".")
                    .withProperty("table", subjectVal.name())
                    .withChild(functionCall.getSourceCodeQuote().userReferenceTree()));
        }
        if (functionCall.getName().getValue().equals(COLUMN_FORMAT)) {
            if (context.getTableFormatting().hasKey(subjectKey.get())) {
                context.getTableFormatting().get(subjectKey).setColumnAttributes(attributes);
            } else {
                context.getTableFormatting().put(subjectKey.get(), tableFormat().setColumnAttributes(attributes));
            }
        } else if (functionCall.getName().getValue().equals(ROW_FORMAT)) {
            if (context.getTableFormatting().hasKey(subjectKey.get())) {
                context.getTableFormatting().get(subjectKey.get()).setRowAttributes(attributes);
            } else {
                context.getTableFormatting().put(subjectKey.get(), tableFormat().setRowAttributes(attributes));
            }
        }
        return run;
    }
}
