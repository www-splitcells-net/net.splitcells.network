/*
 * Copyright (c) 2021 Mārtiņš Avots (Martins Avots) and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0, or the MIT License,
 * which is available at https://spdx.org/licenses/MIT.html.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the Eclipse
 * Public License, v. 2.0 are satisfied: GNU General Public License, version 2
 * or any later versions with the GNU Classpath Exception which is
 * available at https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR MIT OR GPL-2.0-or-later WITH Classpath-exception-2.0
 */
package net.splitcells.gel.constraint.type;

import net.splitcells.gel.data.table.attribute.Attribute;
import net.splitcells.gel.rating.rater.Rater;

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

    public <T> ForAll forAllWithValue(Attribute<T> attribute, T value) {
        return ForAll.forAll(
                forAllWithCondition(line -> value.equals(line.value(attribute))));
    }

    public ForAll forAll() {
        return ForAll.forAll(propagation());
    }

    public ForAll forAll(final Attribute<?> attribute) {
        return ForAll.forAll(forAllAttributeValues(attribute));
    }

    public ForAll forAll(Rater classifier) {
        return ForAll.forAll(classifier);
    }

    public ForAll forAllCombinations(final Attribute<?>... attributes) {
        return ForAll.forAll(forAllValueCombinations(attributes));
    }

}
