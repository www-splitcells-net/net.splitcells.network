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

import net.splitcells.gel.editor.Editor;
import net.splitcells.gel.editor.geal.lang.FunctionCallDesc;
import net.splitcells.gel.editor.geal.lang.IntegerDesc;

import java.util.Optional;

import static net.splitcells.gel.editor.geal.runners.FunctionCallRun.functionCallRun;
import static net.splitcells.gel.rating.rater.lib.HasSize.HAS_SIZE_NAME;
import static net.splitcells.gel.rating.rater.lib.HasSize.hasSize;

public class HasSizeCallRunner implements FunctionCallRunner {
    public static HasSizeCallRunner hasSizeCallRunner() {
        return new HasSizeCallRunner();
    }

    private boolean supports(FunctionCallDesc functionCall, Optional<Object> subject, Editor context) {
        return subject.isEmpty()
                && functionCall.getName().getValue().equals(HAS_SIZE_NAME)
                && functionCall.getArguments().size() == 1
                && functionCall.getArguments().get(0).getExpression() instanceof IntegerDesc;
    }

    @Override
    public FunctionCallRun execute(FunctionCallDesc functionCall, Optional<Object> subject, Editor context) {
        final var run = functionCallRun(subject, context);
        if (!supports(functionCall, subject, context)) {
            return run;
        }
        final var targetSize = functionCall.getArguments().get(0).getExpression().to(IntegerDesc.class).getValue();
        run.setResult(Optional.of(hasSize(targetSize)));
        return run;
    }
}
