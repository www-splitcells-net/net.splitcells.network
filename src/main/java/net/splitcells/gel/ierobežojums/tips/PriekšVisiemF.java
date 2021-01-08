package net.splitcells.gel.ierobežojums.tips;

import net.splitcells.gel.dati.tabula.atribūts.Atribūts;
import net.splitcells.gel.novērtējums.vērtētājs.Vērtētājs;

public class PriekšVisiemF {

    public static <T> PriekšVisiem priekšVisiemArVērtibu(Atribūts<T> atribūts, T vērtiba) {
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
    public static PriekšVisiem priekšVisiem(final Atribūts<?> arg) {
        return PriekšVisiemVeidotajs.gadījums().priekšVisiem(arg);
    }

    public static PriekšVisiem priekšVisiem(Vērtētājs grouping) {
        return PriekšVisiemVeidotajs.gadījums().priekšVisiem(grouping);
    }

    /**
     * TEST Tests are missing.
     *
     * @param argumenti
     * @return
     */
    public static PriekšVisiem forAllCombinations(final Atribūts<?>... argumenti) {
        return PriekšVisiemVeidotajs.gadījums().forAllCombinations(argumenti);
    }
}
