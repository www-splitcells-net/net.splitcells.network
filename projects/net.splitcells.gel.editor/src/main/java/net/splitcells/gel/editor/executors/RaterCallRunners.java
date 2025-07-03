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

import net.splitcells.gel.constraint.Query;
import net.splitcells.gel.editor.lang.geal.FunctionCallDesc;
import net.splitcells.gel.editor.lang.geal.IntegerDesc;
import net.splitcells.gel.editor.lang.geal.NameDesc;
import net.splitcells.gel.rating.rater.lib.HasSize;
import net.splitcells.gel.solution.Solution;

import java.util.Optional;

import static net.splitcells.dem.utils.NotImplementedYet.notImplementedYet;
import static net.splitcells.gel.constraint.QueryI.query;
import static net.splitcells.gel.constraint.type.ForAlls.FOR_EACH_NAME;
import static net.splitcells.gel.editor.executors.BaseCallRunner.baseCallRunner;
import static net.splitcells.gel.rating.rater.lib.HasSize.HAS_SIZE_NAME;
import static net.splitcells.gel.rating.rater.lib.HasSize.hasSize;

public class RaterCallRunners {
    private RaterCallRunners() {
    }

    public static FunctionCallRunner hasSizeCallRunner() {
        return baseCallRunner(new BaseCallRunnerParser() {

            @Override
            public boolean supports(BaseCallRunner base, FunctionCallDesc functionCall) {
                return base.getSubject().isEmpty()
                        && functionCall.getName().getValue().equals(HAS_SIZE_NAME)
                        && functionCall.getArguments().size() == 1
                        && functionCall.getArguments().get(0).getExpression() instanceof IntegerDesc;
            }

            @Override
            public void execute(BaseCallRunner base, FunctionCallDesc functionCall) {
                final var targetSize = functionCall.getArguments().get(0).getExpression().to(IntegerDesc.class).getValue();
                base.setResult(Optional.of(hasSize(targetSize)));
            }
        });
    }
}
