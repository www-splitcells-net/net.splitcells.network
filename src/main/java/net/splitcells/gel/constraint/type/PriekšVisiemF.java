package net.splitcells.gel.constraint.type;

import net.splitcells.gel.data.table.attribute.Attribute;
import net.splitcells.gel.rating.rater.Rater;

public class PriekšVisiemF {

    public static <T> PriekšVisiem priekšVisiemArVērtibu(Attribute<T> atribūts, T vērtiba) {
        return PriekšVisiemVeidotajs.gadījums().priekšVisiemArVērtibu(atribūts, vērtiba);
    }

    /**
     * JAUDA DARĪT Specializēts ierobežojums šitam.
     * <p>
     * PĀRBAUDI Testu trukst.
     */
    public static PriekšVisiem priekšVisiem() {
        return PriekšVisiemVeidotajs.gadījums().priekšVisiem();
    }

    /**
     * This Method exists in order to boost performance. TEST If this method does
     * indeed boost performance compared to some other methods with the same name.
     *
     * @param arg
     * @return
     */
    public static PriekšVisiem priekšVisiem(final Attribute<?> arg) {
        return PriekšVisiemVeidotajs.gadījums().priekšVisiem(arg);
    }

    public static PriekšVisiem priekšVisiem(Rater grouping) {
        return PriekšVisiemVeidotajs.gadījums().priekšVisiem(grouping);
    }

    /**
     * TEST Tests are missing.
     *
     * @param argumenti
     * @return
     */
    public static PriekšVisiem forAllCombinations(final Attribute<?>... argumenti) {
        return PriekšVisiemVeidotajs.gadījums().forAllCombinations(argumenti);
    }
}
