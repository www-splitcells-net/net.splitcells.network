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
package net.splitcells.gel.problem;

import net.splitcells.dem.data.set.list.List;
import net.splitcells.gel.data.table.attribute.Attribute;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;

import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.utils.NotImplementedYet.notImplementedYet;
import static net.splitcells.gel.constraint.type.ForAlls.forAll;
import static net.splitcells.gel.data.assignment.Assignmentss.assignments;
import static net.splitcells.gel.data.database.Databases.database;
import static net.splitcells.gel.problem.ProblemI.problem;

public class ProblemParser {
    public static Problem parseProblem(String arg) {
        final var lexer = new net.splitcells.gel.ext.problem.ProblemLexer(CharStreams.fromString(arg));
        final var parser = new net.splitcells.gel.ext.problem.ProblemParser(new CommonTokenStream(lexer));
        final var source_unit = parser.source_unit();
        final var constraints = forAll();
        final var name = "TODO";
        final List<Attribute<? extends Object>> demandAttributes = list();
        final List<Attribute<? extends Object>> supplyAttributes = list();
        final var demands = database(demandAttributes);
        final var supplies = database(supplyAttributes);
        final var assignments = assignments(name, demands, supplies);
        problem(assignments, constraints);
        throw notImplementedYet();
    }

    private ProblemParser() {

    }
}
