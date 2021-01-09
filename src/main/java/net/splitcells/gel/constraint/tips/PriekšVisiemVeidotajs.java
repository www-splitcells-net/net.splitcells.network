package net.splitcells.gel.constraint.tips;

import net.splitcells.gel.data.tabula.atribūts.Atribūts;
import net.splitcells.gel.rating.rater.Rater;
import net.splitcells.gel.rating.rater.klasifikators.PriekšVisiemVērtībasKombinācija;

import static net.splitcells.gel.rating.rater.klasifikators.Izdalīšana.izdalīšana;
import static net.splitcells.gel.rating.rater.klasifikators.PriekšVisiemArNosacījumu.priekšVisiemArNosacījumu;
import static net.splitcells.gel.rating.rater.klasifikators.PriekšVisiemAtribūtsVērtībam.priekšVisiemAtribūtuVertības;

public class PriekšVisiemVeidotajs {
    protected static final PriekšVisiemVeidotajs GADĪJUMS = new PriekšVisiemVeidotajs();
    
    public static PriekšVisiemVeidotajs gadījums() {
        return GADĪJUMS;
    }

    protected PriekšVisiemVeidotajs() {

    }

    public <T> PriekšVisiem priekšVisiemArVērtibu(Atribūts<T> atribūts, T vērtiba) {
        return PriekšVisiem.veidot(
                priekšVisiemArNosacījumu(line -> vērtiba.equals(line.vērtība(atribūts))));
    }

    public PriekšVisiem priekšVisiem() {
        return PriekšVisiem.veidot(izdalīšana());
    }

    public PriekšVisiem priekšVisiem(final Atribūts<?> arg) {
        return PriekšVisiem.veidot(priekšVisiemAtribūtuVertības(arg));
    }

    public PriekšVisiem priekšVisiem(Rater grouping) {
        return PriekšVisiem.veidot(grouping);
    }

    public PriekšVisiem forAllCombinations(final Atribūts<?>... argumenti) {
        return PriekšVisiem.veidot(PriekšVisiemVērtībasKombinācija.forAllValueCombinations(argumenti));
    }

}
