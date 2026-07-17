/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.gel.constraint.type;

import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.object.Discoverable;
import net.splitcells.gel.constraint.Constraint;
import net.splitcells.gel.data.view.attribute.Attribute;
import net.splitcells.gel.rating.rater.framework.Rater;

import java.util.Optional;

public class ForAlls {

    public static final String FOR_EACH_NAME = "forEach";
    @Deprecated
    public static final String FOR_ALL_COMBINATIONS_OF = "forAllCombinationsOf";

    public static <T> Constraint forAllWithValue(Attribute<T> attribute, T value) {
        return ForAllFactory.instance().forAllWithValue(attribute, value);
    }

    /**
     * <p>TODO PERFORMANCE Specialize implementation for this case.</p>
     * <p>TODO TEST</p>
     * 
     * @deprecated {@link #forAll(Optional)}
     */
    @Deprecated
    public static Constraint forAll() {
        return ForAllFactory.instance().forAll();
    }

    public static Constraint forAll(Optional<Discoverable> parent) {
        return ForAllFactory.instance().forAll(parent);
    }

    /**
     * This Method exists in order to boost performance. TEST If this method does
     * indeed boost performance compared to some other methods with the same name.
     *
     * @param attribute
     * @return
     */
    public static Constraint forEach(final Attribute<?> attribute) {
        return ForAllFactory.instance().forAll(attribute);
    }

    public static Constraint forEach(final Attribute<?> attribute, Optional<Discoverable> parent) {
        return ForAllFactory.instance().forAll(attribute, parent);
    }

    public static Constraint forEach(Rater classifier) {
        return ForAllFactory.instance().forAll(classifier);
    }

    public static Constraint forEach(Rater classifier, Optional<Discoverable> parent) {
        return ForAllFactory.instance().forAll(classifier, parent);
    }

    /**
     * TEST Tests are missing.
     *
     * @param arguments
     * @return
     */
    public static Constraint forAllCombinationsOf(final Attribute<?>... arguments) {
        return ForAllFactory.instance().forAllCombinations(arguments);
    }

    public static Constraint forAllCombinationsOf(List<? extends Attribute<? extends Object>> arguments) {
        return ForAllFactory.instance().forAllCombinations(arguments);
    }

    private ForAlls() {

    }
}
