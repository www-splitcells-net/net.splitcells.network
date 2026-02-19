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

import lombok.val;
import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.object.Discoverable;
import net.splitcells.gel.constraint.Constraint;
import net.splitcells.gel.data.view.Line;
import net.splitcells.gel.data.view.attribute.Attribute;
import net.splitcells.gel.rating.rater.framework.Rater;
import net.splitcells.gel.rating.rater.framework.RaterPredicate;

import java.util.Optional;

import static net.splitcells.gel.rating.rater.lib.classification.ForAllValueCombinations.forAllValueCombinations;
import static net.splitcells.gel.rating.rater.lib.classification.Propagation.propagation;
import static net.splitcells.gel.rating.rater.lib.classification.ForAllWithCondition.forAllWithCondition;
import static net.splitcells.gel.rating.rater.lib.classification.ForAllAttributeValues.forAllAttributeValues;

public class ForAllFactory {
    private static final ForAllFactory INSTANCE = new ForAllFactory();

    public static ForAllFactory instance() {
        return INSTANCE;
    }

    private ForAllFactory() {

    }

    public <T> Constraint forAllWithValue(Attribute<T> attribute, T value) {
        val predicate = new RaterPredicate<Line>() {
            @Override public boolean test(Line line) {
                return value.equals(line.value(attribute));
            }

            @Override public String descriptivePathName() {
                return attribute.name() + "-equals-" + value;
            }
        };
        return ForAll.forAll(forAllWithCondition(predicate));
    }

    public Constraint forAll() {
        return ForAll.forAll(propagation());
    }

    public Constraint forAll(Optional<Discoverable> discoverable) {
        return ForAll.forAll(propagation(), discoverable);
    }

    public Constraint forAll(final Attribute<?> attribute, Optional<Discoverable> parent) {
        return ForAll.forAll(forAllAttributeValues(attribute), parent);
    }

    public Constraint forAll(final Attribute<?> attribute) {
        return ForAll.forAll(forAllAttributeValues(attribute));
    }

    public Constraint forAll(Rater classifier) {
        return ForAll.forAll(classifier);
    }

    public Constraint forAll(Rater classifier, Optional<Discoverable> parent) {
        return ForAll.forAll(classifier, parent);
    }

    public Constraint forAllCombinations(final Attribute<?>... attributes) {
        return ForAll.forAll(forAllValueCombinations(attributes));
    }

    public Constraint forAllCombinations(List<? extends Attribute<? extends Object>> attributes) {
        return ForAll.forAll(forAllValueCombinations(attributes));
    }

}
