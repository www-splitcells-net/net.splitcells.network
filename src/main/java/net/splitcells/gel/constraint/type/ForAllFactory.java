package net.splitcells.gel.constraint.type;

import net.splitcells.gel.data.table.attribute.Attribute;
import net.splitcells.gel.rating.rater.Rater;
import net.splitcells.gel.rating.rater.classification.ForAllValueCombinations;

import static net.splitcells.gel.rating.rater.classification.Propagation.izdalīšana;
import static net.splitcells.gel.rating.rater.classification.ForAllWithCondition.priekšVisiemArNosacījumu;
import static net.splitcells.gel.rating.rater.classification.ForAllAttributeValues.priekšVisiemAtribūtuVertības;

public class ForAllFactory {
    protected static final ForAllFactory GADĪJUMS = new ForAllFactory();
    
    public static ForAllFactory gadījums() {
        return GADĪJUMS;
    }

    protected ForAllFactory() {

    }

    public <T> ForAll priekšVisiemArVērtibu(Attribute<T> atribūts, T vērtiba) {
        return ForAll.veidot(
                priekšVisiemArNosacījumu(line -> vērtiba.equals(line.vērtība(atribūts))));
    }

    public ForAll priekšVisiem() {
        return ForAll.veidot(izdalīšana());
    }

    public ForAll priekšVisiem(final Attribute<?> arg) {
        return ForAll.veidot(priekšVisiemAtribūtuVertības(arg));
    }

    public ForAll priekšVisiem(Rater grouping) {
        return ForAll.veidot(grouping);
    }

    public ForAll forAllCombinations(final Attribute<?>... argumenti) {
        return ForAll.veidot(ForAllValueCombinations.forAllValueCombinations(argumenti));
    }

}
