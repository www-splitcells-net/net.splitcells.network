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
package net.splitcells.gel.editor.executors;

import net.splitcells.gel.constraint.Constraint;
import net.splitcells.gel.constraint.Query;
import net.splitcells.gel.constraint.QueryI;
import net.splitcells.gel.constraint.type.ForAlls;
import net.splitcells.gel.data.view.attribute.Attribute;
import net.splitcells.gel.editor.lang.geal.FunctionCallChainDesc;
import net.splitcells.gel.editor.lang.geal.FunctionCallDesc;
import net.splitcells.gel.editor.lang.geal.NameDesc;
import net.splitcells.gel.editor.lang.geal.StringDesc;
import net.splitcells.gel.rating.rater.framework.Rater;
import net.splitcells.gel.solution.Solution;

import java.util.Optional;

import static net.splitcells.dem.utils.ConstructorIllegal.constructorIllegal;
import static net.splitcells.dem.utils.ExecutionException.execException;
import static net.splitcells.dem.utils.NotImplementedYet.notImplementedYet;
import static net.splitcells.gel.constraint.QueryI.query;
import static net.splitcells.gel.constraint.type.ForAlls.*;
import static net.splitcells.gel.constraint.type.Then.THEN_NAME;
import static net.splitcells.gel.editor.EditorParser.CONSTRAINT_FUNCTION;
import static net.splitcells.gel.editor.executors.BaseCallRunner.baseCallRunner;

public class ConstraintCallRunners {

    public static FunctionCallRunner forAllCombinationsCallRunner() {
        return baseCallRunner(new BaseCallRunnerParser() {

            @Override
            public boolean supports(BaseCallRunner base, FunctionCallDesc functionCall) {
                return base.getSubject().isPresent()
                        && (base.getSubject().orElseThrow() instanceof Solution
                        || base.getSubject().orElseThrow() instanceof Query)
                        && functionCall.getName().getValue().equals(FOR_ALL_COMBINATIONS_OF)
                        && functionCall.getArguments().size() >= 1
                        && functionCall.getArguments()
                        .stream()
                        .hasNoMatch(n -> !(n.getExpression() instanceof NameDesc));
            }

            @Override
            public void execute(BaseCallRunner base, FunctionCallDesc functionCall) {
                final var groupingAttributes = functionCall.getArguments().stream()
                        .map(a -> base.getContext().orElseThrow().<Attribute<? extends Object>>resolve(((NameDesc) a.getExpression())))
                        .toList();
                final Query subject;
                if (base.getSubject().orElseThrow() instanceof Solution solution) {
                    subject = query(solution.constraint());
                } else if (base.getSubject().orElseThrow() instanceof Query query) {
                    subject = query;
                } else {
                    throw notImplementedYet();
                }
                base.setResult(Optional.of(subject.forAllCombinationsOf(groupingAttributes)));
            }
        });
    }

    public static FunctionCallRunner thenCallRunner() {
        return baseCallRunner(new BaseCallRunnerParser() {

            @Override
            public boolean supports(BaseCallRunner base, FunctionCallDesc functionCall) {
                return base.getSubject().isPresent()
                        && (base.getSubject().orElseThrow() instanceof Solution
                        || base.getSubject().orElseThrow() instanceof Query)
                        && functionCall.getName().getValue().equals(THEN_NAME)
                        && functionCall.getArguments().size() == 1
                        && (functionCall.getArguments().get(0).getExpression() instanceof NameDesc
                        || functionCall.getArguments().get(0).getExpression() instanceof FunctionCallDesc);
            }

            @Override
            public void execute(BaseCallRunner base, FunctionCallDesc functionCall) {
                final Query subject;
                switch (base.getSubject().orElseThrow()) {
                    case Solution s -> subject = query(s.constraint());
                    case Query q -> subject = q;
                    default ->
                            throw execException("Subject has to be a solution or a query: " + base.getSubject().orElseThrow());
                }
                final var rawRater = base.getContext().orElseThrow().parseObject(functionCall.getArguments().get(0));
                switch (rawRater) {
                    case Rater r -> base.setResult(Optional.of(subject.then(r)));
                    default ->
                            throw execException("The argument of the then constraint requires exactly one argument, that has to be a rater. Instead the following was returned" + rawRater);
                }
            }
        });
    }

    private ConstraintCallRunners() {
        throw constructorIllegal();
    }
}
