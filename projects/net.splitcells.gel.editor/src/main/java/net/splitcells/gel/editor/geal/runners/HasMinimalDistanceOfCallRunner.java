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
import net.splitcells.gel.data.view.attribute.Attribute;
import net.splitcells.gel.editor.Editor;
import net.splitcells.gel.editor.geal.lang.FunctionCallDesc;
import net.splitcells.gel.editor.geal.lang.IntegerDesc;
import net.splitcells.gel.rating.rater.lib.MinimalDistance;

import java.util.Optional;

import static net.splitcells.dem.lang.tree.TreeI.tree;
import static net.splitcells.dem.utils.ExecutionException.execException;
import static net.splitcells.gel.editor.geal.runners.FunctionCallRun.functionCallRun;
import static net.splitcells.gel.rating.rater.lib.HasSize.hasSize;
import static net.splitcells.gel.rating.rater.lib.MinimalDistance.MINIMAL_DISTANCE_NAME;
import static net.splitcells.gel.rating.rater.lib.MinimalDistance.has_minimal_distance_of;

public class HasMinimalDistanceOfCallRunner implements FunctionCallRunner {
    public static HasMinimalDistanceOfCallRunner hasMinimalDistanceOfCallRunner() {
        return new HasMinimalDistanceOfCallRunner();
    }

    @Override
    public FunctionCallRun execute(FunctionCallDesc functionCall, Optional<Object> subject, Editor context) {
        final var run = functionCallRun(subject, context);
        if (!functionCall.getName().getValue().equals(MINIMAL_DISTANCE_NAME)) {
            return run;
        }
        try (val fcr = context.functionCallRecord(MINIMAL_DISTANCE_NAME, 1)) {
            fcr.requireArgumentCount(functionCall, 2);
            val distanceAttribute = fcr.parseAttribute(functionCall, Integer.class, 0);
            final var secondArg = context.parse(functionCall.getArguments().get(1));
            final int minimalDistance;
            if (secondArg instanceof Integer convertedSecondArg) {
                minimalDistance = convertedSecondArg;
            } else {
                throw execException(tree("The second argument of "
                        + MINIMAL_DISTANCE_NAME + " has to be an integer, but a"
                        + distanceAttribute.type().getName() + " is given instead.")
                        .withProperty("Affected function call", functionCall.getSourceCodeQuote().userReferenceTree()));
            }
            run.setResult(Optional.of(has_minimal_distance_of(distanceAttribute, minimalDistance)));
            return run;
        }
    }
}
