package net.splitcells.gel.constraint.type;

import net.splitcells.gel.data.table.attribute.Attribute;
import net.splitcells.gel.rating.rater.Rater;

public class ForAlls {

    public static <T> ForAll priekšVisiemArVērtibu(Attribute<T> atribūts, T vērtiba) {
        return ForAllFactory.gadījums().priekšVisiemArVērtibu(atribūts, vērtiba);
    }

    /**
     * JAUDA DARĪT Specializēts ierobežojums šitam.
     * <p>
     * PĀRBAUDI Testu trukst.
     */
    public static ForAll priekšVisiem() {
        return ForAllFactory.gadījums().priekšVisiem();
    }

    /**
     * This Method exists in order to boost performance. TEST If this method does
     * indeed boost performance compared to some other methods with the same name.
     *
     * @param arg
     * @return
     */
    public static ForAll priekšVisiem(final Attribute<?> arg) {
        return ForAllFactory.gadījums().priekšVisiem(arg);
    }

    public static ForAll priekšVisiem(Rater grouping) {
        return ForAllFactory.gadījums().priekšVisiem(grouping);
    }

    /**
     * TEST Tests are missing.
     *
     * @param argumenti
     * @return
     */
    public static ForAll forAllCombinations(final Attribute<?>... argumenti) {
        return ForAllFactory.gadījums().forAllCombinations(argumenti);
    }
}
