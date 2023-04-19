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
import static net.splitcells.gel.rating.rater.lib.classification.RaterBasedOnGrouping.raterBasedGrouping;

import net.splitcells.dem.data.set.list.Lists;
import net.splitcells.dem.object.Discoverable;
import net.splitcells.gel.constraint.type.framework.ConstraintBasedOnLocalGroupsAI;
import net.splitcells.gel.constraint.Constraint;
import net.splitcells.gel.constraint.Report;
import net.splitcells.gel.rating.rater.framework.Rater;
import net.splitcells.gel.rating.rater.lib.classification.ForAllAttributeValues;
import net.splitcells.gel.rating.rater.lib.classification.Propagation;
import net.splitcells.gel.rating.rater.lib.classification.RaterBasedOnGrouping;

import java.util.Optional;

public class ForAll extends ConstraintBasedOnLocalGroupsAI {

    public static Constraint forAll(Rater classifier) {
        return create(classifier);
    }

    public static Constraint forAll(Rater classifier, Optional<Discoverable> parent) {
        return constraintAspect(new ForAll(classifier
                , parent.map(d -> d.child(Lists.list(classifier.name())))
        ));
    }

    @Deprecated
    public static Constraint create(Rater classifier) {
        return constraintAspect(new ForAll(classifier));
    }

    protected ForAll(Rater classifier) {
        super(raterBasedGrouping(classifier), Optional.empty());
    }

    protected ForAll(Rater classifier, Optional<Discoverable> parent) {
        super(raterBasedGrouping(classifier), parent);
    }

    public Rater classification() {
        return rater;
    }

    @Override
    public Class<? extends Constraint> type() {
        return ForAll.class;
    }

    @Override
    protected String localNaturalArgumentation(Report report) {
        final var raterBasedOnGrouping = rater.casted(RaterBasedOnGrouping.class);
        if (raterBasedOnGrouping.isPresent()) {
            if (raterBasedOnGrouping.get().classifier().type().equals(Propagation.class)) {
                return raterBasedOnGrouping.get().classifier().toSimpleDescription(report.line()
                        , lineProcessing
                                .columnView(Constraint.INCOMING_CONSTRAINT_GROUP)
                                .lookup(report.group())
                        , report.group());
            }
        }
        if (rater.type().equals(ForAllAttributeValues.class)) {
            return rater.toSimpleDescription(report.line()
                    , lineProcessing.columnView(Constraint.INCOMING_CONSTRAINT_GROUP).lookup(report.group())
                    , report.group());
        } else {
            return "For all " + rater.toSimpleDescription(report.line()
                    , lineProcessing.columnView(Constraint.INCOMING_CONSTRAINT_GROUP).lookup(report.group())
                    , report.group());
        }
    }
}
