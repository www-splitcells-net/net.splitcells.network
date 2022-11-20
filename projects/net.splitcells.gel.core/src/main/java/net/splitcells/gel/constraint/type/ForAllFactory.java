/*
 * Copyright (c) 2021 Mārtiņš Avots (Martins Avots) and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0, or the MIT License,
 * which is available at https://spdx.org/licenses/MIT.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR MIT
 */
package net.splitcells.gel.constraint.type;

import net.splitcells.dem.object.Discoverable;
import net.splitcells.gel.constraint.Constraint;
import net.splitcells.gel.data.table.attribute.Attribute;
import net.splitcells.gel.rating.rater.Rater;

import java.util.Optional;

import static net.splitcells.gel.rating.rater.classification.ForAllValueCombinations.forAllValueCombinations;
import static net.splitcells.gel.rating.rater.classification.Propagation.propagation;
import static net.splitcells.gel.rating.rater.classification.ForAllWithCondition.forAllWithCondition;
import static net.splitcells.gel.rating.rater.classification.ForAllAttributeValues.forAllAttributeValues;

public class ForAllFactory {
    protected static final ForAllFactory INSTANCE = new ForAllFactory();

    public static ForAllFactory instance() {
        return INSTANCE;
    }

    protected ForAllFactory() {

    }

    public <T> Constraint forAllWithValue(Attribute<T> attribute, T value) {
        return ForAll.forAll(
                forAllWithCondition(line -> value.equals(line.value(attribute))));
    }

    public Constraint forAll() {
        return ForAll.forAll(propagation());
    }

    public Constraint forAll(Optional<Discoverable> discoverable) {
        return ForAll.forAll(propagation(), discoverable);
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

}
