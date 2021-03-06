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
    public static ForAll for_all() {
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
    public static ForAll for_all_combinations_of(final Attribute<?>... arguments) {
        return ForAllFactory.instance().forAllCombinations(arguments);
    }
}
