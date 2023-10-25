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
package net.splitcells.gel.ui;

import net.splitcells.dem.lang.perspective.Perspective;
import net.splitcells.dem.resource.Trail;
import net.splitcells.website.Formats;
import net.splitcells.website.server.processor.Processor;
import net.splitcells.website.server.processor.Request;
import net.splitcells.website.server.processor.Response;

import static net.splitcells.dem.lang.perspective.PerspectiveI.perspective;
import static net.splitcells.dem.utils.StringUtils.toBytes;
import static net.splitcells.dem.utils.StringUtils.parseString;
import static net.splitcells.gel.solution.optimization.primitive.repair.ConstraintGroupBasedRepair.constraintGroupBasedRepair;
import static net.splitcells.gel.solution.optimization.primitive.repair.RepairConfig.repairConfig;
import static net.splitcells.gel.ui.ProblemParser.parseProblem;
import static net.splitcells.website.server.processor.BinaryMessage.binaryMessage;
import static net.splitcells.website.server.processor.Response.binaryResponse;

public class SolutionCalculator implements Processor<Perspective, Perspective> {

    public static final Trail PATH = Trail.trail("net/splitcells/gel/ui/calculate-solution.form");

    public static Processor solutionCalculator() {
        return new SolutionCalculator();
    }

    private SolutionCalculator() {

    }

    @Override
    public Response<Perspective> process(Request<Perspective> request) {
        PATH.requireEqualityTo(request.trail());
        final var solution = parseProblem(request
                .data()
                .namedChild("net-splitcells-gel-ui-editor-form-problem-definition")
                .child(0)
                .name())
                .asSolution();
        solution.optimize(constraintGroupBasedRepair(repairConfig()));
        return binaryResponse(perspective("net-splitcells-websiter-server-form-update")
                .withProperty("net-splitcells-gel-ui-editor-form-solution", solution.toCSV()));
    }
}
