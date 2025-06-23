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
import net.splitcells.gel.constraint.type.ForAlls;
import net.splitcells.gel.data.view.attribute.Attribute;
import net.splitcells.gel.editor.lang.geal.FunctionCallDesc;
import net.splitcells.gel.editor.lang.geal.NameDesc;
import net.splitcells.gel.solution.Solution;

import java.util.Optional;

import static net.splitcells.dem.utils.ConstructorIllegal.constructorIllegal;
import static net.splitcells.dem.utils.NotImplementedYet.notImplementedYet;
import static net.splitcells.gel.constraint.type.ForAlls.*;
import static net.splitcells.gel.editor.EditorParser.CONSTRAINT_FUNCTION;
import static net.splitcells.gel.editor.executors.BaseCallRunner.baseCallRunner;

public class ConstraintCallRunners {
    public static FunctionCallRunner constraintCallRunner() {
        return baseCallRunner(new BaseCallRunnerParser() {

            @Override
            public boolean supports(BaseCallRunner base, FunctionCallDesc functionCall) {
                return functionCall.getName().getValue().equals(CONSTRAINT_FUNCTION)
                        && functionCall.getArguments().isEmpty();
            }

            @Override
            public void execute(BaseCallRunner base, FunctionCallDesc functionCall) {
                base.setResult(Optional.of(ForAlls.forAll()));
            }
        });
    }

    public static FunctionCallRunner forEachCallRunner() {
        return baseCallRunner(new BaseCallRunnerParser() {

            @Override
            public boolean supports(BaseCallRunner base, FunctionCallDesc functionCall) {
                return base.getSubject().isPresent()
                        && (base.getSubject().orElseThrow() instanceof Solution
                        || base.getSubject().orElseThrow() instanceof Constraint)
                        && functionCall.getName().getValue().equals(FOR_EACH_NAME)
                        && functionCall.getArguments().size() == 1
                        && functionCall.getArguments().get(0) instanceof NameDesc;
            }

            @Override
            public void execute(BaseCallRunner base, FunctionCallDesc functionCall) {
                final var groupingAttribute = (NameDesc) functionCall.getArguments().get(0);
                final var forAll = ForAlls.forEach(base.getContext().get().getAttributes().get(groupingAttribute.getValue()));
                if (base.getSubject().orElseThrow() instanceof Solution solution) {
                    solution.constraint().withChildren(forAll);
                } else if (base.getSubject().orElseThrow() instanceof Constraint constraint) {
                    constraint.withChildren(forAll);
                } else {
                    throw notImplementedYet();
                }
                base.setResult(Optional.of(forAll));
            }
        });
    }

    public static FunctionCallRunner forAllCombinationsCallRunner() {
        return baseCallRunner(new BaseCallRunnerParser() {

            @Override
            public boolean supports(BaseCallRunner base, FunctionCallDesc functionCall) {
                return base.getSubject().isPresent()
                        && (base.getSubject().orElseThrow() instanceof Solution
                        || base.getSubject().orElseThrow() instanceof Constraint)
                        && functionCall.getName().getValue().equals(FOR_ALL_COMBINATIONS_OF)
                        && functionCall.getArguments().size() >= 1
                        && functionCall.getArguments()
                        .stream()
                        .hasNoMatch(n -> !(n instanceof NameDesc));
            }

            @Override
            public void execute(BaseCallRunner base, FunctionCallDesc functionCall) {
                final var groupingAttributes = functionCall.getArguments().stream()
                        .map(a -> base.getContext().orElseThrow().<Attribute<? extends Object>>resolve(((NameDesc) a)))
                        .toList();
                final var forAll = forAllCombinationsOf(groupingAttributes);
                if (base.getSubject().orElseThrow() instanceof Solution solution) {
                    solution.constraint().withChildren(forAll);
                } else if (base.getSubject().orElseThrow() instanceof Constraint constraint) {
                    constraint.withChildren(forAll);
                } else {
                    throw notImplementedYet();
                }
                base.setResult(Optional.of(forAll));
            }
        });
    }

    private ConstraintCallRunners() {
        throw constructorIllegal();
    }
}
