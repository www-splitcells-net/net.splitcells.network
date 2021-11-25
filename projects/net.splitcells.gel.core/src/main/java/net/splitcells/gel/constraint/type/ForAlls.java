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

import net.splitcells.gel.data.table.attribute.Attribute;
import net.splitcells.gel.rating.rater.Rater;

public class ForAlls {

    public static <T> ForAll forAllWithValue(Attribute<T> attribute, T value) {
        return ForAllFactory.instance().forAllWithValue(attribute, value);
    }

    /**
     * TODO PERFORMANCE Specialize implementation for this case.
     * <p>
     * TODO TEST
     */
    public static ForAll forAll() {
        return ForAllFactory.instance().forAll();
    }

    /**
     * This Method exists in order to boost performance. TEST If this method does
     * indeed boost performance compared to some other methods with the same name.
     *
     * @param attribute
     * @return
     */
    public static ForAll forEach(final Attribute<?> attribute) {
        return ForAllFactory.instance().forAll(attribute);
    }

    public static ForAll forEach(Rater classifier) {
        return ForAllFactory.instance().forAll(classifier);
    }

    /**
     * TEST Tests are missing.
     *
     * @param arguments
     * @return
     */
    public static ForAll forAllCombinationsOf(final Attribute<?>... arguments) {
        return ForAllFactory.instance().forAllCombinations(arguments);
    }
}
