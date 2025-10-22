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

import lombok.val;
import net.splitcells.gel.editor.Editor;
import net.splitcells.gel.editor.geal.lang.FunctionCallDesc;
import net.splitcells.gel.editor.geal.lang.IntegerDesc;

import java.util.Optional;

import static net.splitcells.dem.lang.tree.TreeI.tree;
import static net.splitcells.dem.utils.ExecutionException.execException;
import static net.splitcells.gel.editor.geal.runners.FunctionCallRun.functionCallRun;
import static net.splitcells.gel.rating.rater.lib.HasSize.HAS_SIZE_NAME;
import static net.splitcells.gel.rating.rater.lib.HasSize.hasSize;
import static net.splitcells.gel.rating.rater.lib.MinimalDistance.MINIMAL_DISTANCE_NAME;

public class HasSizeCallRunner implements FunctionCallRunner {
    public static HasSizeCallRunner hasSizeCallRunner() {
        return new HasSizeCallRunner();
    }

    private boolean supports(FunctionCallDesc functionCall, Optional<Object> subject, Editor context) {
        return functionCall.getName().getValue().equals(HAS_SIZE_NAME);
    }

    @Override
    public FunctionCallRun execute(FunctionCallDesc functionCall, Optional<Object> subject, Editor context) {
        final var run = functionCallRun(subject, context);
        if (!supports(functionCall, subject, context)) {
            return run;
        }
        try (val fcr = context.functionCallRecord(functionCall, HAS_SIZE_NAME, 1)) {
            fcr.requireSubjectAbsence(subject);
            fcr.requireArgumentCount(1);
            final var targetSize = fcr.parseArgument(Integer.class, 0);
            run.setResult(Optional.of(hasSize(targetSize)));
            return run;
        }
    }
}
