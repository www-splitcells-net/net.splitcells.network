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
package net.splitcells.cin.deprecated.raters;

import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.lang.dom.Domable;
import net.splitcells.gel.constraint.Constraint;
import net.splitcells.gel.constraint.GroupId;
import net.splitcells.gel.data.assignment.Assignments;
import net.splitcells.gel.data.view.Line;
import net.splitcells.gel.data.view.View;
import net.splitcells.gel.rating.rater.framework.Rater;
import net.splitcells.gel.rating.rater.framework.RatingEvent;
import net.splitcells.gel.rating.rater.framework.RatingEventI;

import static net.splitcells.dem.utils.NotImplementedYet.notImplementedYet;
import static net.splitcells.gel.rating.rater.framework.RatingEventI.ratingEvent;

public class TemplateAdherence implements Rater {

    public static Rater templateAdherence(View template, Assignments subject) {
        return new TemplateAdherence(template, subject);
    }

    private final View template;
    private final Assignments subject;

    private TemplateAdherence(View template, Assignments subject) {
        this.template = template;
        this.subject = subject;
        throw notImplementedYet();

    }

    @Override
    public List<Domable> arguments() {
        throw notImplementedYet();
    }

    @Override
    public RatingEvent ratingAfterAddition(View lines, Line addition, List<Constraint> children, View lineProcessing) {
        throw notImplementedYet();
    }

    @Override public RatingEvent rating_before_removal(View lines, Line removal, List<Constraint> children, View lineProcessingBeforeRemoval) {
        return ratingEvent();
    }

    @Override
    public String toSimpleDescription(Line line, View groupsLineProcessing, GroupId incomingGroup) {
        throw notImplementedYet();
    }
}
