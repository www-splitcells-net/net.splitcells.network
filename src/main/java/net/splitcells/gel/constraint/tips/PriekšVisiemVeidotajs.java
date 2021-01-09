package net.splitcells.gel.constraint.tips;

import net.splitcells.gel.data.table.attribute.Attribute;
import net.splitcells.gel.rating.rater.Rater;
import net.splitcells.gel.rating.rater.classification.ForAllValueCombinations;

import static net.splitcells.gel.rating.rater.classification.Propagation.izdalīšana;
import static net.splitcells.gel.rating.rater.classification.ForAllWithCondition.priekšVisiemArNosacījumu;
import static net.splitcells.gel.rating.rater.classification.ForAllAttributeValues.priekšVisiemAtribūtuVertības;

public class PriekšVisiemVeidotajs {
    protected static final PriekšVisiemVeidotajs GADĪJUMS = new PriekšVisiemVeidotajs();
    
    public static PriekšVisiemVeidotajs gadījums() {
        return GADĪJUMS;
    }

    protected PriekšVisiemVeidotajs() {

    }

    public <T> PriekšVisiem priekšVisiemArVērtibu(Attribute<T> atribūts, T vērtiba) {
        return PriekšVisiem.veidot(
                priekšVisiemArNosacījumu(line -> vērtiba.equals(line.vērtība(atribūts))));
    }

    public PriekšVisiem priekšVisiem() {
        return PriekšVisiem.veidot(izdalīšana());
    }

    public PriekšVisiem priekšVisiem(final Attribute<?> arg) {
        return PriekšVisiem.veidot(priekšVisiemAtribūtuVertības(arg));
    }

    public PriekšVisiem priekšVisiem(Rater grouping) {
        return PriekšVisiem.veidot(grouping);
    }

    public PriekšVisiem forAllCombinations(final Attribute<?>... argumenti) {
        return PriekšVisiem.veidot(ForAllValueCombinations.forAllValueCombinations(argumenti));
    }

}
