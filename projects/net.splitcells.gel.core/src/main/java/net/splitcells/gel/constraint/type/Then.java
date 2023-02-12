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
package net.splitcells.gel.constraint.type;

import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.gel.constraint.type.framework.ConstraintAspect.constraintAspect;
import static net.splitcells.gel.rating.rater.ConstantRater.constantRater;

import net.splitcells.gel.constraint.type.framework.ConstraintAspect;
import net.splitcells.gel.constraint.type.framework.ConstraintBasedOnLocalGroupsAI;
import net.splitcells.gel.constraint.Constraint;
import net.splitcells.gel.constraint.Report;
import net.splitcells.gel.rating.framework.Rating;
import net.splitcells.gel.rating.rater.Rater;

import java.util.Optional;

public class Then extends ConstraintBasedOnLocalGroupsAI {

    public static Constraint then(Rater rater) {
        return constraintAspect(new Then(rater));
    }

    public static Constraint then(Rating rating) {
        return constraintAspect(new Then(constantRater(rating)));
    }

    protected Then(Rater rater) {
        super(rater, rater.getClass().getSimpleName(), Optional.empty());
    }

    @Override
    public Class<? extends Constraint> type() {
        return Then.class;
    }

    @Override
    protected String localNaturalArgumentation(Report report) {
        return "Then " + rater.toSimpleDescription(report.line()
                , lineProcessing.columnView(Constraint.INCOMING_CONSTRAINT_GROUP).lookup(report.group())
                , report.group());
    }
}
